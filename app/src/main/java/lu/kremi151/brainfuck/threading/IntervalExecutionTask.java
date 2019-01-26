package lu.kremi151.brainfuck.threading;

import lu.kremi151.brainfuck.exceptions.InfiniteLoopException;
import lu.kremi151.brainfuck.interfaces.ISteppableExecutable;
import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.ExecutionIterator;

/**
 * Created by michm on 08.06.2017.
 */

public class IntervalExecutionTask extends ExecutionTask {

    private final long interval;

    public IntervalExecutionTask(SlotRegister register, String code, long interval) {
        this(new ExecutionIterator(register, code, 0), interval);
    }

    public IntervalExecutionTask(ISteppableExecutable iterator, long interval) {
        super(iterator);
        this.interval = interval;
    }

    @Override
    protected Exception doInBackground(Object... objects) {
        ((ISteppableExecutable)iterator).setStepping(true);
        try{
            while(iterator.resumeBF()){
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch(RuntimeException e){
            return e;
        }
        return null;
    }
}
