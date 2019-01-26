package lu.kremi151.brainfuck.exceptions;

/**
 * Created by michm on 20.10.2016.
 */

public class InfiniteLoopException extends RuntimeException {

    public InfiniteLoopException(String msg){
        super(msg);
    }

    public InfiniteLoopException(){
        super();
    }
}
