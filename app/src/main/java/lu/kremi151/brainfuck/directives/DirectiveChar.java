package lu.kremi151.brainfuck.directives;

import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.CharIterator;

/**
 * Created by michm on 24.10.2016.
 */

public class DirectiveChar implements DirectiveBase {
    @Override
    public char getId() {
        return 'c';
    }

    @Override
    public int process(SlotRegister reg, CharIterator codeIterator) {
        int ticks = 0;
        if(codeIterator.hasNext()){
            int cv = (int)codeIterator.next();
            for(int i = 0 ; i < cv ; i++){
                reg.increment();
                ticks++;
            }
        }
        return ticks;
    }

    @Override
    public String translate(CharIterator codeIterator) {
        StringBuilder sb = new StringBuilder();
        if(codeIterator.hasNext()){
            int cv = (int)codeIterator.next();
            for(int i = 0 ; i < cv ; i++){
                sb.append('+');
            }
        }
        return sb.toString();
    }
}
