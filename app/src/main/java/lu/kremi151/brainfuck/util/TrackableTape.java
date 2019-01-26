package lu.kremi151.brainfuck.util;

import android.support.annotation.NonNull;

import lu.kremi151.brainfuck.interfaces.IBuffer;
import lu.kremi151.brainfuck.interfaces.IConsumer;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.SlotRegister;

/**
 * Created by michm on 08.06.2017.
 */

public class TrackableTape extends Tape {

    private ChangeListener listener = null;

    public TrackableTape(@NonNull IConsumer<IBuffer> input_request_consumer, @NonNull IConsumer<Character> output_consumer) {
        super(input_request_consumer, output_consumer);
    }

    public TrackableTape(@NonNull IConsumer<IBuffer> input_request_consumer, @NonNull IConsumer<Character> output_consumer, int size) {
        super(input_request_consumer, output_consumer, size);
    }

    public TrackableTape setChangeListener(ChangeListener listener){
        this.listener = listener;
        return this;
    }

    @Override
    public void right() {
        super.right();
        if(listener != null)listener.onChange(this);
    }

    @Override
    public void left() {
        super.left();
        if(listener != null)listener.onChange(this);
    }

    @Override
    public void increment() {
        super.increment();
        if(listener != null)listener.onChange(this);
    }

    @Override
    public void decrement() {
        super.decrement();
        if(listener != null)listener.onChange(this);
    }

    @Override
    public void set(char c) {
        super.set(c);
        if(listener != null)listener.onChange(this);
    }

    @Override
    public boolean in(IExecutable it) {
        boolean res = super.in(it);
        if(listener != null)listener.onChange(this);
        return res;
    }

    @Override
    public void reset() {
        super.reset();
        if(listener != null)listener.onChange(this);
    }

    public static interface ChangeListener{
        void onChange(SlotRegister register);
    }
}
