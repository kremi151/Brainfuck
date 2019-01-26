package lu.kremi151.brainfuck;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Method;

import lu.kremi151.brainfuck.adapters.ScriptsAdapter;

/**
 * Created by michm on 11.10.2016.
 */

public class FragmentEditor extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, TextWatcher {

    EditText textbox;
    private Spinner spinner;
    private View root;
    private ScriptsAdapter scriptsAdapter;

    private View keyboard;
    private ScriptsAdapter.ScriptContent currentContent;
    private boolean lockTextChanges = false, lockSpinner = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.layout_editor, container, false);
        textbox = (EditText) root.findViewById(R.id.editText);
        spinner = (Spinner) root.findViewById(R.id.spinner);
        this.scriptsAdapter = ((MainActivity)getActivity()).getScriptsAdapter();
        spinner.setAdapter(scriptsAdapter);

        currentContent = scriptsAdapter.create();
        scriptsAdapter.notifyDataSetChanged();
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(spinnerItemListener);

        textbox.setOnFocusChangeListener(this);
        textbox.addTextChangedListener(this);

        keyboard = root.findViewById(R.id.bf_keyboard);
        root.findViewById(R.id.buttonP).setOnClickListener(this);
        root.findViewById(R.id.buttonM).setOnClickListener(this);
        root.findViewById(R.id.buttonL).setOnClickListener(this);
        root.findViewById(R.id.buttonR).setOnClickListener(this);
        root.findViewById(R.id.buttonI).setOnClickListener(this);
        root.findViewById(R.id.buttonO).setOnClickListener(this);
        root.findViewById(R.id.buttonS).setOnClickListener(this);
        root.findViewById(R.id.buttonE).setOnClickListener(this);
        root.findViewById(R.id.buttonDelete).setOnClickListener(this);
        root.findViewById(R.id.buttonMoveLeft).setOnClickListener(this);
        root.findViewById(R.id.buttonMoveRight).setOnClickListener(this);
        root.findViewById(R.id.buttonSwitchKeyboard).setOnClickListener(this);

        if(Brainfuck.getInstance().useBFKeyboardAsDefault()){
            setDefaultKeyboard(false);
            keyboard.setVisibility(View.VISIBLE);
        }else{
            setDefaultKeyboard(true);
            keyboard.setVisibility(View.GONE);
        }

        return root;
    }

    /*private final AdapterView.OnItemClickListener spinnerItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switchContent(scriptsAdapter.get(i));
        }
    };*/

    private final AdapterView.OnItemSelectedListener spinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(!lockSpinner)switchContent(scriptsAdapter.get(i));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };

    public void sneakToLastScript(){
        lockSpinner = true;
        spinner.setSelection(scriptsAdapter.getCount() - 1);
        lockSpinner = false;
    }

    public void switchContent(ScriptsAdapter.ScriptContent content){
        lockTextChanges = true;
        currentContent.setContent(textbox.getText().toString());
        currentContent = content;
        textbox.setText(currentContent.getContent());
        lockTextChanges = false;
    }

    public ScriptsAdapter.ScriptContent getCurrentContent(){
        return currentContent;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        /*if(view == textbox){
            ((MainActivity)getActivity()).displayBFKeyboard(b);
        }*/
    }

    public void setDefaultKeyboard(boolean v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textbox.setShowSoftInputOnFocus(v);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        //, new Class[]{boolean.class});
                        , boolean.class);
                method.setAccessible(true);
                method.invoke(textbox, v);
            } catch (Exception e) {
                Log.e("reflection", "setShowSoftInputOnFocus", e);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonP:
                textbox.getText().insert(textbox.getSelectionStart(), "+");
                textbox.requestFocus();
                break;
            case R.id.buttonM:
                textbox.getText().insert(textbox.getSelectionStart(), "-");
                textbox.requestFocus();
                break;
            case R.id.buttonL:
                textbox.getText().insert(textbox.getSelectionStart(), "<");
                textbox.requestFocus();
                break;
            case R.id.buttonR:
                textbox.getText().insert(textbox.getSelectionStart(), ">");
                textbox.requestFocus();
                break;
            case R.id.buttonI:
                textbox.getText().insert(textbox.getSelectionStart(), ",");
                textbox.requestFocus();
                break;
            case R.id.buttonO:
                textbox.getText().insert(textbox.getSelectionStart(), ".");
                textbox.requestFocus();
                break;
            case R.id.buttonS:
                textbox.getText().insert(textbox.getSelectionStart(), "[");
                textbox.requestFocus();
                break;
            case R.id.buttonE:
                textbox.getText().insert(textbox.getSelectionStart(), "]");
                textbox.requestFocus();
                break;
            case R.id.buttonDelete:
                int end = textbox.getSelectionEnd();
                if(textbox.getSelectionStart() == end && textbox.getSelectionStart() > 0){
                    textbox.getText().delete(textbox.getSelectionStart() - 1, textbox.getSelectionStart());
                }else{
                    textbox.getText().delete(textbox.getSelectionStart(), end);
                }
                break;
            case R.id.buttonMoveLeft:
                if(textbox.getSelectionStart() == textbox.getSelectionEnd()){
                    int s = textbox.getSelectionStart();
                    if(s > 0)s--;
                    textbox.setSelection(s);
                }else{
                    textbox.setSelection(textbox.getSelectionStart());
                }
                break;
            case R.id.buttonMoveRight:
                if(textbox.getSelectionStart() == textbox.getSelectionEnd()){
                    int s = textbox.getSelectionEnd();
                    if(s < textbox.length())s++;
                    textbox.setSelection(s);
                }else{
                    textbox.setSelection(textbox.getSelectionEnd());
                }
                break;
            case R.id.buttonSwitchKeyboard:
                if(keyboard.getVisibility() == View.GONE){
                    setDefaultKeyboard(false);
                    ((MainActivity)getActivity()).getInputMethodManager().hideSoftInputFromWindow(textbox.getWindowToken(), 0);
                    keyboard.setVisibility(View.VISIBLE);
                }else{
                    keyboard.setVisibility(View.GONE);
                    setDefaultKeyboard(true);
                    ((MainActivity)getActivity()).getInputMethodManager().showSoftInput(textbox, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        if(!lockTextChanges){
            currentContent.setHasChanged(true);
            scriptsAdapter.notifyDataSetChanged();
        }
    }
}
