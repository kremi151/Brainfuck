package lu.kremi151.brainfuck.interfaces;

import lu.kremi151.brainfuck.util.ExecutionIterator;

/**
 * Created by michm on 12.10.2016.
 */

public interface SlotRegister {

    void right();
    void left();
    void increment();
    void decrement();
    void set(char c);
    void out();
    boolean in(IExecutable it);
    char value();
    int ptr();
    int cellCount();
    void reset();
}
