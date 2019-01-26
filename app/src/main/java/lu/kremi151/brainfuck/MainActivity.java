package lu.kremi151.brainfuck;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import lu.kremi151.brainfuck.adapters.ScriptsAdapter;
import lu.kremi151.brainfuck.converters.CodeConverters;
import lu.kremi151.brainfuck.directives.DirectiveBase;
import lu.kremi151.brainfuck.enums.EnumCellWidth;
import lu.kremi151.brainfuck.exceptions.InfiniteLoopException;
import lu.kremi151.brainfuck.interfaces.IConsumer;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.IExecutionListener;
import lu.kremi151.brainfuck.interfaces.IRunner;
import lu.kremi151.brainfuck.interfaces.ITickCounter;
import lu.kremi151.brainfuck.threading.ExecutionTask;
import lu.kremi151.brainfuck.threading.IntervalExecutionTask;
import lu.kremi151.brainfuck.util.CCodeConverter;
import lu.kremi151.brainfuck.util.CharIterator;
import lu.kremi151.brainfuck.util.CodeHelper;
import lu.kremi151.brainfuck.util.DialogHelper;
import lu.kremi151.brainfuck.util.ExecutionIterator;
import lu.kremi151.brainfuck.util.FSHelper;
import lu.kremi151.brainfuck.util.JavaCodeConverter;
import lu.kremi151.brainfuck.util.Optional;

public class MainActivity extends AppCompatActivity{

    private ViewPager pager;
    public TabAdapter tabAdapter;//TODO
    private MenuItem menuItemRun, menuItemRunIntvl, menuItemStepFwd, menuItemExamples, menuItemConvertEEM;
    private Menu menu;
    private ExecutionIterator currentExecution = null;
    private InputMethodManager imm;
    private boolean forceLockItemsWhileRunning = false;
    private ScriptsAdapter scriptsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.viewPager);

        scriptsAdapter = new ScriptsAdapter(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        pager.setAdapter(tabAdapter);

        // Specify that tabs should be displayed in the action bar.
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition(), true);
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < tabAdapter.getCount(); i++) {
            getSupportActionBar().addTab(
                    getSupportActionBar().newTab()
                            .setText(tabAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }

        pager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        this.menu = menu;
        menuItemRun = menu.findItem(R.id.item_play);
        menuItemStepFwd = menu.findItem(R.id.item_step_fwd);
        menuItemRunIntvl = menu.findItem(R.id.item_run_delayed);
        menuItemExamples = menu.findItem(R.id.item_examples);
        menuItemConvertEEM = menu.findItem(R.id.item_convert_genuine);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemConvertEEM.setVisible(Brainfuck.getInstance().isExtendedModeEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final String code = tabAdapter.getCodeBox().getText().toString();
        switch(item.getItemId()){
            case R.id.item_run_delayed:
                DialogHelper.buildIntervalDialog(this, new IConsumer<Long>() {
                    @Override
                    public void consume(Long interval) {
                        if (currentExecution == null) {
                            //slotRegister = new SlotRegisterImpl();

                            tabAdapter.getFragOut().getOutputBox().setText("");

                            tabAdapter.getFragOut().getSlotRegister().reset();
                            currentExecution = new ExecutionIterator(tabAdapter.getFragOut().getSlotRegister(), code, 0);
                            currentExecution.setListener(exec_listener);
                            //currentExecution = new ExecutionThread(code, syncRunner, exec_listener, tabAdapter.getFragOut().getSlotRegister());
                        } else {
                            if (currentExecution.getState() == ExecutionIterator.State.FINISHED) {
                                tabAdapter.getFragOut().getOutputBox().setText("");
                            }
                        }
                        //exec_listener.onResumed(currentExecution);
                        forceLockItemsWhileRunning = true;
                        currentExecution.setStepping(true);
                        currentExecution.setExtended(Brainfuck.getInstance().isExtendedModeEnabled());
                        pager.setCurrentItem(1);
                        new IntervalExecutionTask(currentExecution, interval).setCompletitionListener(new ExecutionTask.Listener() {
                            @Override
                            public void onFinished(final IExecutable it) {
                                tabAdapter.getFragOut().updateRegister();
                                //exec_listener.onStopped(it);
                            }

                            @Override
                            public void onError(IExecutable it, Exception e) {
                                if(e instanceof InfiniteLoopException){
                                    exec_listener.onStopped(currentExecution);
                                    currentExecution = null;
                                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.infinite_loop_detected).show();
                                    Log.e("Infinite loop error", "An error occured while executing the code", e);
                                }else{
                                    exec_listener.onStopped(currentExecution);
                                    currentExecution = null;
                                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.exec_error_msg).show();
                                    Log.e("Execution error", "An error occured while executing the code", e);
                                }
                            }
                        }).execute();
                    }
                }).show();
                break;
            case R.id.item_play:
                if (currentExecution == null) {
                    //slotRegister = new SlotRegisterImpl();

                    tabAdapter.getFragOut().getOutputBox().setText("");

                    tabAdapter.getFragOut().getSlotRegister().reset();
                    currentExecution = new ExecutionIterator(tabAdapter.getFragOut().getSlotRegister(), code, 0);
                    currentExecution.setListener(exec_listener);
                    //currentExecution = new ExecutionThread(code, syncRunner, exec_listener, tabAdapter.getFragOut().getSlotRegister());
                } else {
                    if (currentExecution.getState() == ExecutionIterator.State.FINISHED) {
                        tabAdapter.getFragOut().getOutputBox().setText("");
                    }
                }
                //exec_listener.onResumed(currentExecution);
                currentExecution.setStepping(false);
                currentExecution.setExtended(Brainfuck.getInstance().isExtendedModeEnabled());
                pager.setCurrentItem(1);
                new ExecutionTask(currentExecution).setCompletitionListener(new ExecutionTask.Listener() {
                    @Override
                    public void onFinished(final IExecutable it) {
                        tabAdapter.getFragOut().updateRegister();
                        //exec_listener.onStopped(it);
                    }

                    @Override
                    public void onError(IExecutable it, Exception e) {
                        if(e instanceof InfiniteLoopException){
                            exec_listener.onStopped(currentExecution);
                            currentExecution = null;
                            DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.infinite_loop_detected).show();
                            Log.e("Infinite loop error", "An error occured while executing the code", e);
                        }else{
                            exec_listener.onStopped(currentExecution);
                            currentExecution = null;
                            DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.exec_error_msg).show();
                            Log.e("Execution error", "An error occured while executing the code", e);
                        }
                    }
                }).execute();
                break;
            case R.id.item_step_fwd:
            //case R.id.item_play:
                try {
                    boolean stepping = id == R.id.item_step_fwd;
                    if (!CodeHelper.checkBrackets(code)) {
                        DialogHelper.buildInfoDialog(MainActivity.this, R.string.warning, R.string.bracket_missing).show();
                        break;
                    }

                    /*if(arrayAdapter == null){
                        arrayAdapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_list_item_1);
                        tabAdapter.getFragOut().getGrid().setAdapter(arrayAdapter);
                    }*/

                    pager.setCurrentItem(1);
                    if (currentExecution == null) {
                        //slotRegister = new SlotRegisterImpl();

                        tabAdapter.getFragOut().getOutputBox().setText("");

                        tabAdapter.getFragOut().getSlotRegister().reset();
                        currentExecution = new ExecutionIterator(tabAdapter.getFragOut().getSlotRegister(), code, 0);
                        currentExecution.setListener(exec_listener);
                        //currentExecution = new ExecutionThread(code, syncRunner, exec_listener, tabAdapter.getFragOut().getSlotRegister());
                    } else {
                        if (currentExecution.getState() == ExecutionIterator.State.FINISHED) {
                            tabAdapter.getFragOut().getOutputBox().setText("");
                        }
                    }

                    currentExecution.setStepping(item.getItemId() == R.id.item_step_fwd);
                    currentExecution.setExtended(Brainfuck.getInstance().isExtendedModeEnabled());
                    currentExecution.resume();
                    //slotRegister.update();
                    tabAdapter.getFragOut().updateRegister();
                }catch (InfiniteLoopException e1){
                    exec_listener.onStopped(currentExecution);
                    currentExecution = null;
                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.infinite_loop_detected).show();
                    Log.e("Infinite loop error", "An error occured while executing the code", e1);
                }catch(RuntimeException e){
                    exec_listener.onStopped(currentExecution);
                    currentExecution = null;
                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.error_occured, R.string.exec_error_msg).show();
                    Log.e("Execution error", "An error occured while executing the code", e);
                }
                break;
            case R.id.item_commands:
                DialogHelper.buildCommandsDialog(MainActivity.this).show();
                break;
            case R.id.item_examples:
                DialogHelper.buildExamplesDialog(MainActivity.this, new IConsumer<Integer>() {
                    @Override
                    public void consume(Integer res) {
                        switch(res){
                            case 0:
                                tabAdapter.getCodeBox().setText("++++++++[->+<]>[[->>+<<]>>[-<+<+>>]<-]<[<]>[,>]<[<]>[.>]");
                                pager.setCurrentItem(0);
                                break;
                            case 1:
                                DialogHelper.buildInputDialog(MainActivity.this, R.string.input_req, new IConsumer<String>() {
                                    @Override
                                    public void consume(String res) {
                                        String bf = CodeHelper.convertToBF(res);
                                        tabAdapter.getCodeBox().setText(bf);
                                        pager.setCurrentItem(0);
                                    }
                                }).show();
                                break;
                            case 2:
                                DialogHelper.buildInputDialog(MainActivity.this, R.string.input_req, new IConsumer<String>() {
                                    @Override
                                    public void consume(String res) {
                                        String bf = CodeHelper.displayInBF(res, Brainfuck.getInstance().sixteenBitModeEnabled()?EnumCellWidth.SIXTEEN_BIT:EnumCellWidth.EIGHT_BIT);
                                        tabAdapter.getCodeBox().setText(bf);
                                        pager.setCurrentItem(0);
                                    }
                                }).show();
                                break;
                            case 3:
                                tabAdapter.getCodeBox().setText("+++[->++++<]");
                                pager.setCurrentItem(0);
                                break;
                            case 4:
                                tabAdapter.getCodeBox().setText("++++++++[[->>+<<]>>[-<+<+>>]<-]");
                                pager.setCurrentItem(0);
                                break;
                            case 5:
                                tabAdapter.getCodeBox().setText("++++++++[>++++++++<-]>[-<++>]<-----" +
                                        " >[-]++++++++[>[-]<[->+<]>-]<<<<<<<<<" +
                                        " [->+<]>[>+<-<+>]>[>>>>>[->+<]>+<<<<<" +
                                        " ++++++++++<[->>+<-[>>>]>[[<+>-]>+>>]" +
                                        " <<<<<]>[-]>[-<<+>>]>[-<<+>>]<<]>>>>>" +
                                        " [<<<<+++++++[-<+++++++>]<-[<+>-]<.[-" +
                                        " ]>>>>>>[-<+>]<-]<<<<<<<");
                                pager.setCurrentItem(0);
                                break;
                            case 6:
                                tabAdapter.getCodeBox().setText(
                                        "[" + getString(R.string.basic_math_oper_desc) + "]\n\n"
                                        + "[>+<-]  C1 = C1 plus C0\n"
                                        + "[>-<-]  C1 = C1 minus C0\n"
                                        + "[>+++++<-]  C1 = C0 * 5\n"
                                        + "[>[>+>+<<-]>>[<<+>>-]<<<-]  C2 = C1 times C0\n"
                                        + "+++++[>+++++[>+++++<-]<-]  C2 = 5^3\n"
                                        + "[>+<-----]  C1 = C0 divided by 5"

                                );
                                break;
                            case 7:
                                tabAdapter.getCodeBox().setText("[" + getString(R.string.cat_prog_desc) + "]\n\n,[.>,]");
                        }
                    }
                }).show();
                break;
            case R.id.item_clear:
                tabAdapter.getFragOut().getSlotRegister().reset();
                tabAdapter.getFragOut().updateRegister();
                tabAdapter.getCodeBox().setText("");
                tabAdapter.getFragOut().getOutputBox().setText("");
                tabAdapter.getFragOut().setStatus(R.string.status_idle);
                if(currentExecution != null)exec_listener.onStopped(currentExecution);
                break;
            case R.id.item_settings:
                DialogHelper.buildSettigsDialog(MainActivity.this, new IConsumer<boolean[]>() {
                    @Override
                    public void consume(boolean[] res) {
                        Brainfuck bf = Brainfuck.getInstance();

                        bf.setExtendedModeEnabled(res[0]);

                        bf.setSixteenBitMode(res[1]);
                        tabAdapter.getFragOut().setSixteenBitMode(res[1]);

                        bf.setInfiniteLoopDetectionEnabled(res[2]);

                        bf.setUseBFKeyboardAsDefault(res[3]);
                    }
                }).show();
                break;
            case R.id.item_convert_genuine:
                String genuineConvert = CodeConverters.GENUINE_CONVERTER.convert(code, false);
                if(!code.equals(genuineConvert)){
                    DialogHelper.buildConfirmCodeChangementDialog(MainActivity.this, genuineConvert, new IConsumer<String>() {
                        @Override
                        public void consume(String res) {
                            tabAdapter.getCodeBox().setText(res);
                        }
                    }).show();
                }else{
                    Toast.makeText(MainActivity.this, R.string.no_changed_applicable, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.item_convert_c:
                if(CodeHelper.checkBrackets(code)){
                    DialogHelper.buildConversionLevelDialog(this, new IConsumer<Integer>() {
                        @Override
                        public void consume(Integer res) {
                            String c_code;
                            if(res == 1){
                                c_code = CodeConverters.C_CONVERTER.convert(code, true);
                            }else{
                                c_code = CodeConverters.C_CONVERTER.convert(code, false);
                            }
                            DialogHelper.buildCopyableMessageDialog(MainActivity.this, R.string.converted_c_code, c_code).show();
                            //System.out.println("Generated code:\n" + c_code);
                        }
                    }).show();
                }else{
                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.warning, R.string.bracket_missing).show();
                }
                break;
            case R.id.item_convert_java:
                if(CodeHelper.checkBrackets(code)){
                    DialogHelper.buildConversionLevelDialog(this, new IConsumer<Integer>() {
                        @Override
                        public void consume(Integer res) {
                            String c_code;
                            if(res == 1){
                                c_code = CodeConverters.JAVA_CONVERTER.convert(code, true);
                            }else{
                                c_code = CodeConverters.JAVA_CONVERTER.convert(code, false);
                            }
                            DialogHelper.buildCopyableMessageDialog(MainActivity.this, R.string.converted_java_code, c_code).show();
                            //System.out.println("Generated code:\n" + c_code);
                        }
                    }).show();
                }else{
                    DialogHelper.buildInfoDialog(MainActivity.this, R.string.warning, R.string.bracket_missing).show();
                }
                break;
            case R.id.item_save:
                final ScriptsAdapter.ScriptContent saveContent = tabAdapter.getFragEditor().getCurrentContent();
                if(saveContent.getMeta().hasFile){
                    if(saveContent.hasChanged()){
                        File dest = new File(Brainfuck.getInstance().getScriptsDir(), saveContent.getMeta().name + ".bf");
                        if(!saveScript(dest, saveContent)){
                            DialogHelper.buildMessageDialog(MainActivity.this, R.string.error_occured, getString(R.string.error_while_saving_script)).show();
                        }else{
                            saveContent.setHasChanged(false);
                            scriptsAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                }/*else{
                    Fall through
                }
                break;*/
            case R.id.item_save_as:
                final ScriptsAdapter.ScriptContent saveAsContent = tabAdapter.getFragEditor().getCurrentContent();
                DialogHelper.buildInputDialog(this, R.string.enter_script_name, new IConsumer<String>() {
                    @Override
                    public void consume(final String res) {
                        if(FSHelper.checkFileNameWithoutSuffix(res)){
                            final File dest = new File(Brainfuck.getInstance().getScriptsDir(), res + ".bf");
                            if(!dest.exists()){
                                if(!saveScript(dest, saveAsContent)){
                                    DialogHelper.buildMessageDialog(MainActivity.this, R.string.error_occured, getString(R.string.error_while_saving_script)).show();
                                }else{
                                    saveAsContent.updateMeta(res);
                                    saveAsContent.setHasChanged(false);
                                    scriptsAdapter.notifyDataSetChanged();
                                }
                            }else{
                                DialogHelper.buildConfirmDialog(MainActivity.this, R.string.warning, R.string.existing_file_name, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!saveScript(dest, saveAsContent)){
                                            DialogHelper.buildMessageDialog(MainActivity.this, R.string.error_occured, getString(R.string.error_while_saving_script)).show();
                                        }else{
                                            saveAsContent.updateMeta(res);
                                            saveAsContent.setHasChanged(false);
                                            scriptsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }).show();
                            }

                        }else{
                            DialogHelper.buildMessageDialog(MainActivity.this, R.string.warning, getString(R.string.invalid_file_name)).show();
                        }

                    }
                }).show();
                break;
            case R.id.item_open:
                final File scripts[] = Brainfuck.getInstance().getScriptsDir().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile() && file.getName().endsWith(".bf") && !scriptsAdapter.isScriptLoaded(file.getName().split("\\.")[0]);
                    }
                });
                final CharSequence items[] = new CharSequence[scripts.length];
                for(int i = 0 ; i < scripts.length ; i++)items[i] = scripts[i].getName();
                DialogHelper.buildItemsDialog(this, R.string.load_script, items, new IConsumer<Integer>() {
                    @Override
                    public void consume(Integer index) {
                        File script = scripts[index];
                        try {
                            tabAdapter.getFragEditor().switchContent(scriptsAdapter.load(script));
                            scriptsAdapter.notifyDataSetChanged();
                            tabAdapter.getFragEditor().sneakToLastScript();
                        } catch (IOException e) {
                            e.printStackTrace();
                            DialogHelper.buildMessageDialog(MainActivity.this, R.string.error_occured, getString(R.string.error_while_loading_script)).show();
                        }
                    }
                }).show();
                break;
            case R.id.item_new_script:
                ScriptsAdapter.ScriptContent newContent = scriptsAdapter.create();
                tabAdapter.getFragEditor().switchContent(newContent);
                scriptsAdapter.notifyDataSetChanged();
                tabAdapter.getFragEditor().sneakToLastScript();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveScript(File dest, ScriptsAdapter.ScriptContent content){
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter(dest));
            String actualContent = tabAdapter.getCodeBox().getText().toString();
            bw.write(actualContent, 0, actualContent.length());
            bw.flush();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }finally{
            if(bw != null) try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public InputMethodManager getInputMethodManager(){
        return imm;
    }

    public ScriptsAdapter getScriptsAdapter(){
        return scriptsAdapter;
    }

    public void resumeExecution(){
        if(currentExecution != null){
            currentExecution.resume();
        }
    }

    private final IRunner syncRunner = new IRunner() {
        @Override
        public void run(Runnable r) {
            MainActivity.this.runOnUiThread(r);
        }
    };

    private void hideItemsWhileRunning(boolean hide){
        tabAdapter.getCodeBox().setEnabled(!hide);
        menuItemExamples.setEnabled(!hide);
        menuItemRun.setEnabled(!hide);
        menuItemStepFwd.setEnabled(!hide);
        menuItemRunIntvl.setEnabled(!hide);
        menuItemConvertEEM.setEnabled(!hide);
    }

    private final IExecutionListener exec_listener = new IExecutionListener() {
        @Override
        public void onResumed(IExecutable it) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideItemsWhileRunning(true);
                    tabAdapter.getFragOut().setProgress(true);
                    tabAdapter.getFragOut().setStatus(R.string.status_running);
                    //System.out.println("resumed");
                }
            });
        }

        @Override
        public void onPaused(IExecutable it) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!forceLockItemsWhileRunning){
                        menuItemRun.setEnabled(true);
                        menuItemStepFwd.setEnabled(true);
                        menuItemRunIntvl.setEnabled(true);
                    }
                    tabAdapter.getFragOut().setProgress(true);
                    tabAdapter.getFragOut().setStatus(R.string.status_paused);
                    //tabAdapter.getFragOut().updateRegister();
                    //System.out.println("paused");
                }
            });
        }

        @Override
        public void onStopped(final IExecutable it) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideItemsWhileRunning(false);
                    //menu.setGroupEnabled(R.id.hideable_when_run, true);
                    currentExecution = null;
                    forceLockItemsWhileRunning = false;
                    tabAdapter.getFragOut().setProgress(false);
                    if(it != null && it instanceof ITickCounter){
                        tabAdapter.getFragOut().setStatus(getString(R.string.status_terminated, ((ITickCounter)it).tickCount()));
                    }else{
                        tabAdapter.getFragOut().setStatus(R.string.status_terminated_simple);
                    }
                    //tabAdapter.getFragOut().updateRegister();
                    //System.out.println("stopped");
                }
            });
        }
    };

}
