package lu.kremi151.brainfuck.directives;

import lu.kremi151.brainfuck.interfaces.SlotRegister;
import lu.kremi151.brainfuck.util.CharIterator;

/**
 * Created by michm on 24.10.2016.
 */

public class DirectiveString implements DirectiveBase {

    private boolean display = true;

    public DirectiveString(boolean display){
        //this.display = display;
    }

    @Override
    public char getId() {
        return display?'d':'s';
    }

    @Override
    public int process(SlotRegister reg, CharIterator codeIterator) {
        return baseFunction(runtimeHandler, reg, codeIterator);
    }

    @Override
    public String translate(CharIterator codeIterator) {
        Translator tr = new Translator();
        baseFunction(tr, null, codeIterator);
        return tr.sb.toString();
    }

    private int baseFunction(Handler handler, SlotRegister reg, CharIterator codeIterator){
        int ticks = 0;
        char last = (char)-1, c;
        while(codeIterator.hasNext() && (c = codeIterator.next()) != ':'){
            int dif;
            if(last == (char)-1){
                dif = (int)c;
            }else{
                dif = (int)c - (int)last;
            }
            last = c;
            if(dif >= 0){
                for(int i = 0 ; i < dif ; i++){
                    handler.increment(reg);
                    ticks++;
                }
                if(display){
                    handler.out(reg);
                    ticks++;
                }
            }else{
                for(int i = dif ; i < 0 ; i++){
                    handler.decrement(reg);
                    ticks++;
                }
                if(display){
                    handler.out(reg);
                    ticks++;
                }
            }
        }
        return ticks;
    }

    private interface Handler{
        void increment(SlotRegister reg);
        void decrement(SlotRegister reg);
        void out(SlotRegister reg);
    }

    private final Handler runtimeHandler = new Handler() {
        @Override
        public void increment(SlotRegister reg) {
            reg.increment();
        }

        @Override
        public void decrement(SlotRegister reg) {
            reg.decrement();
        }

        @Override
        public void out(SlotRegister reg) {
            reg.out();
        }
    };

    private class Translator implements Handler{

        StringBuilder sb = new StringBuilder();

        @Override
        public void increment(SlotRegister reg) {
            sb.append('+');
        }

        @Override
        public void decrement(SlotRegister reg) {
            sb.append('-');
        }

        @Override
        public void out(SlotRegister reg) {
            sb.append('.');
        }
    }
}
