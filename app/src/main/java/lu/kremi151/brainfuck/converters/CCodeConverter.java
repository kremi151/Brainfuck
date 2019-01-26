package lu.kremi151.brainfuck.converters;

/**
 * Created by michm on 09.06.2017.
 */

class CCodeConverter extends AbstractCStyleCodeConverter {

    @Override
    protected int appendHeader(StringBuilder builder, boolean compressed) {
        builder.append("#include <stdio.h>\n\n");
        builder.append("int main() {\n");
        builder.append("\tunsigned char tape[4096] = {0}; //May be adapted to another size\n");
        builder.append("\tunsigned char *ptr = tape; //Current pointer to the tape\n\n");
        return 1;
    }

    @Override
    protected void appendValueIncrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("(*ptr)++;\n");
        }else{
            builder.append("(*ptr) += ").append(amount).append(";\n");
        }
    }

    @Override
    protected void appendValueDecrement(StringBuilder builder, int amount) {
        if(amount == 1){
            builder.append("(*ptr)--;\n");
        }else{
            builder.append("(*ptr) -= ").append(amount).append(";\n");
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
        builder.append("*ptr = getchar();\n");
    }

    @Override
    protected void appendOutput(StringBuilder builder, boolean compressed) {
        builder.append("putchar(*ptr);\n");
    }

    @Override
    protected void appendStartLoop(StringBuilder builder, boolean compressed) {
        builder.append("while(*ptr) {\n");
    }

    @Override
    protected void appendCloseLoop(StringBuilder builder, boolean compressed) {
        builder.append("}\n");
    }

    @Override
    protected void appendFooter(StringBuilder builder, boolean compressed, int currentIndentation) {
        builder.append("\treturn 0;\n");
        builder.append("}\n");
    }
}
