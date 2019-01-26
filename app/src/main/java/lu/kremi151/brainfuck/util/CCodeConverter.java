package lu.kremi151.brainfuck.util;

/**
 * Created by michm on 08.06.2017.
 */

public class CCodeConverter {

    public static String convertToCCode(String bfCode){
        return convertToCCode(bfCode, false);
    }

    public static String convertToCCode(String bfCode, boolean compressed){
        StringBuilder sb = new StringBuilder();
        sb.append("#include <stdio.h>\n\n");
        sb.append("int main() {\n");
        sb.append("\tunsigned char tape[4096] = {0}; //May be adapted to another size\n");
        sb.append("\tunsigned char *ptr = tape; //Current pointer to the tape\n\n");

        int indentations = 1;
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
                            sb.append("(*ptr)++;\n");
                        }else{
                            sb.append("(*ptr) += " + amount + ";\n");
                        }
                    }else{
                        sb.append("(*ptr)++;\n");
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
                            sb.append("(*ptr)--;\n");
                        }else{
                            sb.append("(*ptr) -= " + amount + ";\n");
                        }
                    }else{
                        sb.append("(*ptr)--;\n");
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
                    sb.append("putchar(*ptr);\n");
                    break;
                case ',':
                    fillIndentations(sb, indentations);
                    sb.append("*ptr = getchar();\n");
                    break;
                case '[':
                    fillIndentations(sb, indentations);
                    sb.append("while(*ptr) {\n");
                    indentations++;
                    break;
                case ']':
                    indentations--;
                    fillIndentations(sb, indentations);
                    sb.append("}\n");
                    break;
            }
        }

        sb.append("\treturn 0;\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void fillIndentations(StringBuilder sb, int amount){
        while(amount-- > 0)sb.append('\t');
    }
}
