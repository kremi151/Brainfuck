package lu.kremi151.brainfuck.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lu.kremi151.brainfuck.R;

/**
 * Created by michm on 18.10.2016.
 */

public class AdapterCommandDescription extends BaseAdapter {

    Context c;
    LayoutInflater li;

    public AdapterCommandDescription(Context c, LayoutInflater li){
        this.c = c;
        this.li = li;
    }

    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view != null){
            vh = (ViewHolder) view.getTag();
        }else{
            vh = new ViewHolder();
            view = li.inflate(R.layout.layout_command_desc, viewGroup, false);
            vh.command = (TextView) view.findViewById(R.id.command);
            vh.desc = (TextView) view.findViewById(R.id.description);
            view.setTag(vh);
        }

        switch(i){
            case 0:
                vh.command.setText(">");
                vh.desc.setText(R.string.command_desc_right);
                break;
            case 1:
                vh.command.setText("<");
                vh.desc.setText(R.string.command_desc_left);
                break;
            case 2:
                vh.command.setText("+");
                vh.desc.setText(R.string.command_desc_plus);
                break;
            case 3:
                vh.command.setText("-");
                vh.desc.setText(R.string.command_desc_minus);
                break;
            case 4:
                vh.command.setText(".");
                vh.desc.setText(R.string.command_desc_output);
                break;
            case 5:
                vh.command.setText(",");
                vh.desc.setText(R.string.command_desc_input);
                break;
            case 6:
                vh.command.setText("[");
                vh.desc.setText(R.string.command_desc_loop_start);
                break;
            case 7:
                vh.command.setText("]");
                vh.desc.setText(R.string.command_desc_loop_end);
                break;
            case 8:
                vh.command.setText(c.getString(R.string.source, "Wikipedia"));
                vh.desc.setText("");
                break;
        }

        return view;
    }

    private static class ViewHolder{
        TextView command, desc;
    }
}
