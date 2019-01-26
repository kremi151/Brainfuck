package lu.kremi151.brainfuck.util;

import java.util.ArrayList;

import lu.kremi151.brainfuck.interfaces.SlotRegister;

/**
 * Created by michm on 12.10.2016.
 */

public class ExecutionScope {

    public static void run(SlotRegister reg, String code, int start){
        int length = code.length();

        for(int i = start ; i < length ; i++) {
            char c = code.charAt(i);
            switch (c) {
                case '[':
                    while (reg.value() != (char) 0) {
                        run(reg, code, i + 1);
                    }
                    for (; i < length; i++) {
                        c = code.charAt(i);
                        if (c == ']') {
                            break;
                        }
                    }
                    break;
                case ']':
                    return;
                case '<':
                    reg.left();
                    break;
                case '>':
                    reg.right();
                    break;
                case '+':
                    reg.increment();
                    break;
                case '-':
                    reg.decrement();
                    break;
                case ',':
                    //reg.in();
                    break;
                case '.':
                    reg.out();
                    break;
            }
        }
    }

}
