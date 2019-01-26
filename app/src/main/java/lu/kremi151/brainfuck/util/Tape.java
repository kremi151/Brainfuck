package lu.kremi151.brainfuck.util;

import android.support.annotation.NonNull;

import lu.kremi151.brainfuck.interfaces.IBuffer;
import lu.kremi151.brainfuck.interfaces.IConsumer;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.SlotRegister;

/**
 * Created by michm on 31.10.2016.
 */

public class Tape implements SlotRegister {

    private static final int EIGHT_BIT_MAX = (int)(Math.pow(2, 8) - 1);
    private static final int SIXTEEN_BIT_MAX = (int)(Math.pow(2, 16) - 1);

    private char tape[], inbuffer[] = null;
    private int ptr = 0, b_index = 0, tape_length = 0, cellWidth = EIGHT_BIT_MAX;
    private boolean b16_mode = false;

    private IConsumer<Character> output_consumer;
    private IConsumer<IBuffer> input_request_consumer;

    public Tape(@NonNull IConsumer<IBuffer> input_request_consumer, @NonNull IConsumer<Character> output_consumer){
        this(input_request_consumer, output_consumer, 16);
    }

    public Tape(@NonNull IConsumer<IBuffer> input_request_consumer, @NonNull IConsumer<Character> output_consumer, int size){
        this.tape = new char[size];
        this.tape_length = 1;
        this.ptr = 0;
        this.output_consumer = output_consumer;
        this.input_request_consumer = input_request_consumer;
    }

    public Tape setSixteenBitMode(boolean v){
        this.b16_mode = v;
        this.cellWidth = v?SIXTEEN_BIT_MAX:EIGHT_BIT_MAX;
        return this;
    }

    @Override
    public void right() {
        if(ptr + 1 >= tape.length){
            char tmp[] = tape;
            tape = new char[tape.length * 2];
            for(int i = 0 ; i < tmp.length ; i++){
                tape[i] = tmp[i];
            }
        }
        if(ptr + 1 >= tape_length){
            tape_length++;
        }
        ptr++;
    }

    @Override
    public void left() {
        if(ptr > 0){
            ptr--;
        }
    }

    @Override
    public void increment() {
        int tmp = tape[ptr];
        char c;
        if(tmp == cellWidth){
            c = (char)0;
        }else {
            c = (char)(tmp+1);
        }
        tape[ptr] = c;
    }

    @Override
    public void decrement() {
        int tmp = tape[ptr];
        char c;
        if(tmp == 0){
            c = (char)cellWidth;
        }else {
            c = (char)(tmp-1);
        }
        tape[ptr] = c;
    }

    @Override
    public void set(char c) {
        tape[ptr] = c;
    }

    @Override
    public void out() {
        output_consumer.consume(tape[ptr]);
    }

    @Override
    public boolean in(IExecutable it) {
        if(inbuffer == null){
            input_request_consumer.consume(new BufferWrapper());
            return false;
        }else{
            if(b_index < inbuffer.length){
                tape[ptr] = inbuffer[b_index++];
            }else{
                tape[ptr] = (char)0;
            }
            return true;
        }
    }

    @Override
    public char value() {
        return tape[ptr];
    }

    public char valueAt(int ptr){
        return tape[ptr];
    }

    @Override
    public int ptr() {
        return ptr;
    }

    @Override
    public int cellCount() {
        return tape_length;
    }

    @Override
    public void reset() {
        tape_length = 1;
        ptr = 0;
        inbuffer = null;
        b_index = 0;
        for(int i = 0 ; i < tape.length ; i++){
            tape[i] = (char)0;
        }
    }

    private class BufferWrapper implements IBuffer{

        private boolean active = true;

        @Override
        public void setBuffer(char[] buffer) {
            if(active){
                inbuffer = buffer;
                b_index = 0;
                active = false;
            }
        }
    }
}
