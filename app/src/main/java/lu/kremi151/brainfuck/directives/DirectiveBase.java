package lu.kremi151.brainfuck.directives;

import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.CharIterator;

/**
 * Created by michm on 22.10.2016.
 */

public interface DirectiveBase {

    /**
     * The id of the directive
     * @return
     */
    char getId();

    /**
     * Processes the directive at runtime
     * @param reg The Slot register (Tape).
     * @param charIterator The code iterator.
     * @return Returns the pseudo amount of ticks elapsed. It should be the real amount of ticks if this directive would have been translated to code.
     */
    int process(SlotRegister reg, CharIterator charIterator);

    /**
     * Translates the directive to native Brainfuck code
     * @param charIterator The code iterator
     * @return Returns the translated code snippet
     */
    String translate(CharIterator charIterator);
}
