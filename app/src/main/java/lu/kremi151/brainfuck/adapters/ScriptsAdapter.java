package lu.kremi151.brainfuck.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import lu.kremi151.brainfuck.MainActivity;
import lu.kremi151.brainfuck.R;

/**
 * Created by michm on 08.06.2017.
 */

public class ScriptsAdapter extends BaseAdapter {

    private final MainActivity main;
    private final ArrayList<ScriptContent> scripts = new ArrayList<>();

    public ScriptsAdapter(MainActivity main){
        this.main = main;
    }

    public ScriptContent get(int idx){
        return scripts.get(idx);
    }

    public ScriptContent create(){
        ScriptContent content = new ScriptContent();
        content.meta = new ScriptMeta("New", false);
        scripts.add(content);
        return content;
    }

    public ScriptContent load(File src) throws IOException {
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(src));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            ScriptMeta meta = new ScriptMeta(src.getName().split("\\.")[0], true);
            ScriptContent content = new ScriptContent();
            content.meta = meta;
            content.content = sb.toString();
            scripts.add(content);
            return content;
        }finally{
            if(br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isScriptLoaded(String name){
        for(ScriptContent content : scripts){
            if(content.meta.name.equals(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return scripts.size();
    }

    @Override
    public Object getItem(int i) {
        return scripts.get(i).meta;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = main.getLayoutInflater().inflate(R.layout.layout_simple, viewGroup, false);
        }
        TextView tv = (TextView)view;
        ScriptContent script = scripts.get(i);
        if(script.meta.hasFile && !script.hasChanged){
            tv.setText(script.meta.name);
        }else{
            tv.setText(main.getString(R.string.unsaved_entry, script.meta.name));
        }
        return tv;
    }

    public static class ScriptMeta{
        public final String name;
        public final boolean hasFile;

        private ScriptMeta(String name, boolean hasFile){
            this.name = name;
            this.hasFile = hasFile;
        }

        @Override
        public boolean equals(Object obj){
            if(obj == this) {
                return true;
            }else if(obj == null){
                return false;
            }else if(obj instanceof ScriptMeta){
                return ((ScriptMeta)obj).name.equals(this.name) && ((ScriptMeta)obj).hasFile == this.hasFile;
            }else{
                return false;
            }
        }

        @Override
        public int hashCode(){
            return name.hashCode() + (hasFile ? 31 : 0);
        }
    }

    public static class ScriptContent{
        private ScriptMeta meta;
        private boolean hasChanged = false;
        private String content;

        public void setHasChanged(boolean v){
            this.hasChanged = v;
        }

        public boolean hasChanged(){
            return hasChanged;
        }

        public void setContent(String content){
            this.content = content;
        }

        public String getContent(){
            return content;
        }

        public ScriptMeta getMeta(){
            return meta;
        }

        public void updateMeta(String scriptName){
            ScriptMeta meta = new ScriptMeta(scriptName, true);
            this.meta = meta;
        }
    }
}
