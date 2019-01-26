package lu.kremi151.brainfuck.exceptions;

/**
 * Created by michm on 17.10.2016.
 */

public class ScopeCalculationException extends RuntimeException {

    public ScopeCalculationException(String msg){
        super(msg);
    }

    public ScopeCalculationException(){
        super();
    }
}
