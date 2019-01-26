package lu.kremi151.brainfuck.interfaces;

/**
 * Created by michm on 08.06.2017.
 */

public interface ISteppableExecutable extends IExecutable {

    /**
     * Sets if this executable instance should pause after each step
     * @param v
     */
    void setStepping(boolean v);

}
