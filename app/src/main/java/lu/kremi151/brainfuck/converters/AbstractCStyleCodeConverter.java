package lu.kremi151.brainfuck.converters;

/**
 * Created by michm on 09.06.2017.
 */

abstract class AbstractCStyleCodeConverter implements CodeConverter {

    /**
     * Builds the header (top part, everything above the actual converted code)
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     * @return Returns the default indentation (the amount of tabs) to continue with
     */
    protected abstract int appendHeader(StringBuilder builder, boolean compressed);

    /**
     * Appends the syntax which increments the current value by the specific amount
     * @param builder The builder to append on
     * @param amount The amount of incrementation
     */
    protected abstract void appendValueIncrement(StringBuilder builder, int amount);

    /**
     * Appends the syntax which decrements the current value by the specific amount
     * @param builder The builder to append on
     * @param amount The amount of incrementation
     */
    protected abstract void appendValueDecrement(StringBuilder builder, int amount);

    /**
     * Appends the syntax which increments the pointer by the specific amount
     * @param builder The builder to append on
     * @param amount The amount of incrementation
     */
    protected abstract void appendPointerIncrement(StringBuilder builder, int amount);

    /**
     * Appends the syntax which decrements the pointer by the specific amount
     * @param builder The builder to append on
     * @param amount The amount of incrementation
     */
    protected abstract void appendPointerDecrement(StringBuilder builder, int amount);

    /**
     * Appends the syntax which reads a character from input to the tape
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     */
    protected abstract void appendInput(StringBuilder builder, boolean compressed);

    /**
     * Appends the syntax which writes the current character to the console
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     */
    protected abstract void appendOutput(StringBuilder builder, boolean compressed);

    /**
     * Appends the loop start syntax
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     */
    protected abstract void appendStartLoop(StringBuilder builder, boolean compressed);

    /**
     * Appends the loop close syntax
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     */
    protected abstract void appendCloseLoop(StringBuilder builder, boolean compressed);

    /**
     * Builds the footer (bottom part, everything below the actual converted code)
     * @param builder The builder to append on
     * @param compressed The flag indicating if the converted code should be compressed
     * @param currentIndentation The amount of the current indentation level
     */
    protected abstract void appendFooter(StringBuilder builder, boolean compressed, int currentIndentation);

    @Override
    public String convert(String bfCode, boolean compressed) {
        StringBuilder sb = new StringBuilder();

        int indentations = appendHeader(sb, compressed);

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
                            appendValueIncrement(sb, 1);
                        }else{
                            appendValueIncrement(sb, amount);
                        }
                    }else{
                        appendValueIncrement(sb, 1);
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
                            appendValueDecrement(sb, 1);
                        }else{
                            appendValueDecrement(sb, amount);
                        }
                    }else{
                        appendValueDecrement(sb, 1);
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
                            appendPointerIncrement(sb, 1);
                        }else{
                            appendPointerIncrement(sb, amount);
                        }
                    }else{
                        appendPointerIncrement(sb, 1);
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
                            appendPointerDecrement(sb, 1);
                        }else{
                            appendPointerDecrement(sb, amount);
                        }
                    }else{
                        appendPointerDecrement(sb, 1);
                    }
                    break;
                case '.':
                    fillIndentations(sb, indentations);
                    appendOutput(sb, compressed);
                    break;
                case ',':
                    fillIndentations(sb, indentations);
                    appendInput(sb, compressed);
                    break;
                case '[':
                    fillIndentations(sb, indentations);
                    appendStartLoop(sb, compressed);
                    indentations++;
                    break;
                case ']':
                    indentations--;
                    fillIndentations(sb, indentations);
                    appendCloseLoop(sb, compressed);
                    break;
            }
        }

        appendFooter(sb, compressed, indentations);

        return sb.toString();
    }

    protected void fillIndentations(StringBuilder sb, int amount){
        while(amount-- > 0)sb.append('\t');
    }
}
