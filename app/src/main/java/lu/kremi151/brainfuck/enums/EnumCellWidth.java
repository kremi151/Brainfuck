package lu.kremi151.brainfuck.enums;

/**
 * Created by michm on 19.10.2016.
 */

public enum EnumCellWidth {
    EIGHT_BIT((int)Math.pow(2, 8) - 1),
    SIXTEEN_BIT((int)Math.pow(2, 16) - 1);

    public final int max;

    EnumCellWidth(int max){
        this.max = max;
    }
}
