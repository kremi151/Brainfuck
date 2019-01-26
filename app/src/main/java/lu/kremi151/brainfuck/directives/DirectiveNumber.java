package lu.kremi151.brainfuck.directives;

import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.CharIterator;
import lu.kremi151.brainfuck.util.CodeHelper;

/**
 * Created by michm on 24.10.2016.
 */

public class DirectiveNumber implements DirectiveBase {
    @Override
    public char getId() {
        return 'n';
    }

    @Override
    public int process(SlotRegister reg, CharIterator codeIterator) {
        int ticks = 0;
        if(codeIterator.hasNext()){
            int nr = 0;
            char nrc = codeIterator.next();
            boolean positive = true;
            if(nrc == '+'){
                positive = true;
            }else if(nrc == '-'){
                positive = false;
            }else{
                codeIterator.previous();
            }
            while(codeIterator.hasNext()){
                nrc = codeIterator.next();
                byte rv = CodeHelper.charToDigit(nrc);
                if(rv != -1){
                    nr *= 10;
                    nr += rv;
                }else{
                    codeIterator.previous();
                    break;
                }
            }
            if(positive){
                for(int i = 0 ; i < nr ; i++){
                    reg.increment();
                    ticks++;
                }
            }else{
                for(int i = 0 ; i < nr ; i++){
                    reg.decrement();
                    ticks++;
                }
            }
        }
        return ticks;
    }

    @Override
    public String translate(CharIterator codeIterator) {
        StringBuilder sb = new StringBuilder();
        if(codeIterator.hasNext()){
            int nr = 0;
            char nrc = codeIterator.next();
            boolean positive = true;
            if(nrc == '+'){
                positive = true;
            }else if(nrc == '-'){
                positive = false;
            }else{
                codeIterator.previous();
            }
            while(codeIterator.hasNext()){
                nrc = codeIterator.next();
                byte rv = CodeHelper.charToDigit(nrc);
                if(rv != -1){
                    nr *= 10;
                    nr += rv;
                }else{
                    codeIterator.previous();
                    break;
                }
            }
            if(positive){
                for(int i = 0 ; i < nr ; i++){
                    sb.append('+');
                }
            }else{
                for(int i = 0 ; i < nr ; i++){
                    sb.append('-');
                }
            }
        }
        return sb.toString();
    }
}
