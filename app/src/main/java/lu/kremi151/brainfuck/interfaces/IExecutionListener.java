package lu.kremi151.brainfuck.interfaces;

import lu.kremi151.brainfuck.util.ExecutionIterator;

/**
 * Created by michm on 13.10.2016.
 */

public interface IExecutionListener {

    void onResumed(IExecutable it);
    void onPaused(IExecutable it);
    void onStopped(IExecutable it);
}
