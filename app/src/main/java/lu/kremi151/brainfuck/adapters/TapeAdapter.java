package lu.kremi151.brainfuck.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lu.kremi151.brainfuck.Brainfuck;
import lu.kremi151.brainfuck.MainActivity;
import lu.kremi151.brainfuck.R;
import lu.kremi151.brainfuck.interfaces.IBuffer;
import lu.kremi151.brainfuck.interfaces.IConsumer;
import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.DialogHelper;
import lu.kremi151.brainfuck.util.TrackableTape;

/**
 * Created by michm on 18.10.2016.
 */

public class TapeAdapter extends RecyclerView.Adapter<TapeAdapter.TapeViewHolder>{

    private final int hover_color, neutral_color;
    private final MainActivity main;
    private final TrackableTape tape;
    private boolean inputRequestState = false;

    public TapeAdapter(@NonNull MainActivity main){
        this.main = main;
        hover_color = main.getResources().getColor(R.color.colorAccent);
        neutral_color = Color.BLACK;
        this.tape = new TrackableTape(input_request_consumer, output_consumer).setChangeListener(tapeChangeListener);
        tape.setSixteenBitMode(Brainfuck.getInstance().sixteenBitModeEnabled());
    }

    private final TrackableTape.ChangeListener tapeChangeListener = new TrackableTape.ChangeListener() {
        @Override
        public void onChange(SlotRegister register) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public TapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_tape_item, parent, false);

        return new TapeViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(TapeViewHolder holder, int position) {
        holder.item.setText("" + (int)tape.valueAt(position));
        holder.item.setTextColor(tape.ptr() == position ? hover_color : neutral_color);
    }

    @Override
    public int getItemCount() {
        return tape.cellCount();
    }

    public SlotRegister getTape(){
        return tape;
    }

    public void setSixteenBitMode(boolean v){
        tape.setSixteenBitMode(v);
    }

    public static class TapeViewHolder extends RecyclerView.ViewHolder{

        TextView item;

        public TapeViewHolder(View itemView) {
            super(itemView);

            this.item = (TextView) itemView.findViewById(R.id.tapeItem);
        }
    }

    private final IConsumer<Character> output_consumer = new IConsumer<Character>() {
        @Override
        public void consume(final Character res) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    main.tabAdapter.getFragOut().getOutputBox().append(""+res);
                }
            });
        }
    };

    private final IConsumer<IBuffer> input_request_consumer = new IConsumer<IBuffer>() {
        @Override
        public void consume(final IBuffer buffer) {
            if(!inputRequestState){
                inputRequestState = true;
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.buildInputDialog(main, R.string.input_req, new IConsumer<String>() {
                            @Override
                            public void consume(String res) {
                                buffer.setBuffer(res.toCharArray());
                                main.resumeExecution();
                                inputRequestState = false;
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                inputRequestState = false;
                            }
                        }).show();
                    }
                });
            }
        }
    };
}
