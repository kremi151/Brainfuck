package lu.kremi151.brainfuck.interfaces;

/**
 * Created by michm on 19.10.2016.
 */

public interface IExecutable {

    /**
     * Resumes the execution
     * @return Returns true if the execution has not yet finished. Otherwise, false is returned.
     */
    boolean resumeBF();

    /**
     * Pauses the execution
     */
    void pauseBF();

    /**
     * Stops the execution. Further {@code resumeBF()} calls should return false after this invocation.
     */
    void stopBF();
}
