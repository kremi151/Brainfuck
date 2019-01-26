package lu.kremi151.brainfuck.converters;

/**
 * Created by michm on 09.06.2017.
 */

class JavaCodeConverter extends AbstractCStyleCodeConverter {
    @Override
    protected int appendHeader(StringBuilder builder, boolean compressed) {
        builder.append("import java.io.IOException;\n\n");
        builder.append("public class BrainfuckCode{\n");
        builder.append("\tpublic static void main(String args[]) throws IOException{\n");
        builder.append("\t\tchar tape[] = new char[4096]; //May be adapted to another size\n");
        builder.append("\t\tint ptr = 0; //Current pointer index to the tape\n\n");
        return 2;
    }

    @Override
    protected void appendValueIncrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("tape[ptr]++;\n");
        }else{
            builder.append("tape[ptr] += ").append(amount).append(";\n");
        }
    }

    @Override
    protected void appendValueDecrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("tape[ptr]--;\n");
        }else{
            builder.append("tape[ptr] -= ").append(amount).append(";\n");
        }
    }

    @Override
    protected void appendPointerIncrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("ptr++;\n");
        }else{
            builder.append("ptr += ").append(amount).append(";\n");
        }
    }

    @Override
    protected void appendPointerDecrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("ptr--;\n");
        }else{
            builder.append("ptr -= ").append(amount).append(";\n");
        }
    }

    @Override
    protected void appendInput(StringBuilder builder, boolean compressed) {
        builder.append("tape[ptr] = (char)System.in.read();\n");
    }

    @Override
    protected void appendOutput(StringBuilder builder, boolean compressed) {
        builder.append("System.out.print(tape[ptr]);\n");
    }

    @Override
    protected void appendStartLoop(StringBuilder builder, boolean compressed) {
        builder.append("while(tape[ptr] > 0) {\n");
    }

    @Override
    protected void appendCloseLoop(StringBuilder builder, boolean compressed) {
        builder.append("}\n");
    }

    @Override
    protected void appendFooter(StringBuilder builder, boolean compressed, int currentIndentation) {
        builder.append("\t}\n");
        builder.append("}\n");
    }
}
