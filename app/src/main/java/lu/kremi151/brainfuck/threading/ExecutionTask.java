package lu.kremi151.brainfuck.threading;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import lu.kremi151.brainfuck.exceptions.InfiniteLoopException;
import lu.kremi151.brainfuck.interfaces.IBuffer;
import lu.kremi151.brainfuck.interfaces.IConsumer;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.IExecutionListener;
import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.ExecutionIterator;
import lu.kremi151.brainfuck.util.Tape;

/**
 * Created by michm on 07.06.2017.
 */

public class ExecutionTask extends AsyncTask<Object, Object, Exception> {

    protected final IExecutable iterator;
    private Listener listener;

    public ExecutionTask(SlotRegister register, String code){
        this(new ExecutionIterator(register, code, 0));
    }

    public ExecutionTask(IExecutable iterator){
        this.iterator = iterator;
    }

    public ExecutionTask setCompletitionListener(Listener listener){
        this.listener = listener;
        return this;
    }

    @Override
    protected Exception doInBackground(Object... objects) {
        try{
            iterator.resumeBF();
        }catch(RuntimeException e){
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Exception result) {
        if(result != null){
            if(listener != null)listener.onError(iterator, result);
        }else{
            if(listener != null)listener.onFinished(iterator);
        }
    }

    @Override
    protected void onProgressUpdate(Object... values) {}

    public static interface Listener{
        void onFinished(IExecutable it);
        void onError(IExecutable it, Exception e);
    }
}
