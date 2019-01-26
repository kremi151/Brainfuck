package lu.kremi151.brainfuck.converters;

/**
 * Created by michm on 09.06.2017.
 */

public interface CodeConverter {

    String convert(String bfCode, boolean compressed);
}
