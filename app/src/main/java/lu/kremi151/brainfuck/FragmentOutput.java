package lu.kremi151.brainfuck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import lu.kremi151.brainfuck.adapters.TapeAdapter;
import lu.kremi151.brainfuck.enums.EnumCellWidth;
import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.views.HorizontalListView;

/**
 * Created by michm on 11.10.2016.
 */

public class FragmentOutput extends Fragment {

    private RecyclerView recList;
    private EditText output;
    private TextView status;
    private ProgressBar progress;

    private LinearLayoutManager llm;
    private TapeAdapter tapeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_output, container, false);
        recList = (RecyclerView) root.findViewById(R.id.tapeLayout);
        output = (EditText) root.findViewById(R.id.editText3);
        status = (TextView) root.findViewById(R.id.status);
        progress = (ProgressBar) root.findViewById(R.id.progressBar);

        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);
        tapeAdapter = new TapeAdapter((MainActivity) getActivity());
        recList.setAdapter(tapeAdapter);

        return root;
    }

    public SlotRegister getSlotRegister(){
        return tapeAdapter.getTape();
    }

    public void updateRegister(){
        //tapeAdapter.notifyDataSetChanged();
        System.out.println("Manual updateRegister() is deprecated");
    }

    public EditText getOutputBox(){
        return output;
    }

    public void setSixteenBitMode(boolean v){
        tapeAdapter.setSixteenBitMode(v);
    }

    public void setStatus(int textResId){
        this.status.setText(textResId);
    }

    @Deprecated
    public void setStatus(CharSequence text){
        this.status.setText(text);
    }

    public void setProgress(boolean v){
        this.progress.setVisibility(v?View.VISIBLE:View.INVISIBLE);
    }

}
