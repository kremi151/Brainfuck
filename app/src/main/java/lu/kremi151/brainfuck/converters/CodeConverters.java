package lu.kremi151.brainfuck.converters;

/**
 * Created by michm on 09.06.2017.
 */

public class CodeConverters {

    public static final CodeConverter C_CONVERTER = new CCodeConverter();
    public static final CodeConverter JAVA_CONVERTER = new JavaCodeConverter();
    public static final CodeConverter GENUINE_CONVERTER = new GenuineConverter();
}
