package lu.kremi151.brainfuck;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.EditText;

import lu.kremi151.brainfuck.FragmentEditor;

/**
 * Created by michm on 11.10.2016.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    FragmentEditor fragEdit = new FragmentEditor();
    FragmentOutput fragOut = new FragmentOutput();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    public EditText getCodeBox(){
        return fragEdit.textbox;
    }

    public FragmentOutput getFragOut(){
        return fragOut;
    }

    public FragmentEditor getFragEditor(){
        return fragEdit;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return fragEdit;
            case 1:
                return fragOut;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Code";
            case 1:
                return "Output";
            default:
                return null;
        }
    }
}
