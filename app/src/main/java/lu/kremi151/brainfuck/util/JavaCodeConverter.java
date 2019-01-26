package lu.kremi151.brainfuck.util;

/**
 * Created by michm on 09.06.2017.
 */

public class JavaCodeConverter {

    public static String convertToJavaCode(String bfCode){
        return convertToJavaCode(bfCode, false);
    }

    public static String convertToJavaCode(String bfCode, boolean compressed){
        StringBuilder sb = new StringBuilder();
        sb.append("import java.io.IOException;\n\n");
        sb.append("public class BrainfuckCode{\n");
        sb.append("\tpublic static void main(String args[]) throws IOException{\n");
        sb.append("\t\tchar tape[] = new char[4096]; //May be adapted to another size\n");
        sb.append("\t\tint ptr = 0; //Current pointer index to the tape\n\n");

        int indentations = 2;
        for(int i = 0 ; i < bfCode.length() ; i++){
            char c = bfCode.charAt(i);
            switch(c){
                case '+':
                    fillIndentations(sb, indentations);
                    if(compressed){
                        int amount = 1;
                        for(i++ ; i < bfCode.length() ; i++){
                            if(bfCode.charAt(i) == '+'){
                                amount++;
                            }else{
                                i--;
                                break;
                            }
                        }
                        if(amount == 1){
                            sb.append("tape[ptr]++;\n");
                        }else{
                            sb.append("tape[ptr] += " + amount + ";\n");
                        }
                    }else{
                        sb.append("tape[ptr]++;\n");
                    }
                    break;
                case '-':
                    fillIndentations(sb, indentations);
                    if(compressed){
                        int amount = 1;
                        for(i++ ; i < bfCode.length() ; i++){
                            if(bfCode.charAt(i) == '-'){
                                amount++;
                            }else{
                                i--;
                                break;
                            }
                        }
                        if(amount == 1){
                            sb.append("tape[ptr]--;\n");
                        }else{
                            sb.append("tape[ptr] -= " + amount + ";\n");
                        }
                    }else{
                        sb.append("tape[ptr]--;\n");
                    }
                    break;
                case '>':
                    fillIndentations(sb, indentations);
                    if(compressed){
                        int amount = 1;
                        for(i++ ; i < bfCode.length() ; i++){
                            if(bfCode.charAt(i) == '>'){
                                amount++;
                            }else{
                                i--;
                                break;
                            }
                        }
                        if(amount == 1){
                            sb.append("ptr++;\n");
                        }else{
                            sb.append("ptr += " + amount + ";\n");
                        }
                    }else{
                        sb.append("ptr++;\n");
                    }
                    break;
                case '<':
                    fillIndentations(sb, indentations);
                    if(compressed){
                        int amount = 1;
                        for(i++ ; i < bfCode.length() ; i++){
                            if(bfCode.charAt(i) == '<'){
                                amount++;
                            }else{
                                i--;
                                break;
                            }
                        }
                        if(amount == 1){
                            sb.append("ptr--;\n");
                        }else{
                            sb.append("ptr -= " + amount + ";\n");
                        }
                    }else{
                        sb.append("ptr--;\n");
                    }
                    break;
                case '.':
                    fillIndentations(sb, indentations);
                    sb.append("System.out.print(tape[ptr]);\n");
                    break;
                case ',':
                    fillIndentations(sb, indentations);
                    sb.append("tape[ptr] = (char)System.in.read();\n");
                    break;
                case '[':
                    fillIndentations(sb, indentations);
                    sb.append("while(tape[ptr] > 0) {\n");
                    indentations++;
                    break;
                case ']':
                    indentations--;
                    fillIndentations(sb, indentations);
                    sb.append("}\n");
                    break;
            }
        }

        sb.append("\t}\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void fillIndentations(StringBuilder sb, int amount){
        while(amount-- > 0)sb.append('\t');
    }
}
