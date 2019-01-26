package lu.kremi151.brainfuck.util;

import lu.kremi151.brainfuck.enums.EnumCellWidth;

/**
 * Created by michm on 13.10.2016.
 */

public class CodeHelper {

    public static boolean checkBrackets(String code){
        int b = 0;
        int l = code.length();
        for(int i = 0 ; i < l ; i++){
            char c = code.charAt(i);
            if(c == '['){
                b++;
            }else if(c == ']'){
                b--;
            }
        }
        return b == 0;
    }

    public static String convertToBF(String text){
        StringBuilder sb = new StringBuilder(">");
        int length = text.length();
        for(int i = 0 ; i < length ; i++){
            char c = text.charAt(i);
            int val = ((int)c) % 256;
            for(int j = 0 ; j < val ; j++){
                sb.append('+');
            }
            if(i != length - 1){
                sb.append('>');
            }
        }
        sb.append("[<]>[.>]");
        return sb.toString();
    }

    public static String displayInBF(String text){
        return displayInBF(text, EnumCellWidth.EIGHT_BIT);
    }

    public static String displayInBF(String text, EnumCellWidth cellWidth){
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        int lv = -1;
        for(int i = 0 ; i < length ; i++){
            char c = text.charAt(i);
            int val = ((int)c) % (cellWidth.max + 1);
            if(lv == -1){
                sb.append(comprimizeNumber(val));
                sb.append('.');
            }else{
                int dif = val - lv;
                if(dif >= 0){
                    for(int j = 0 ; j < dif ; j++){
                        sb.append('+');
                    }
                }else{
                    for(int j = dif ; j < 0 ; j++){
                        sb.append('-');
                    }
                }
                /*if(isComprimizable(dif))sb.append('<');
                sb.append(comprimizeNumber(dif));*/
                sb.append('.');
            }
            lv = val;
        }
        return sb.toString();
    }

    private static boolean isComprimizable(int n){
        return Math.abs(n) > 9;
    }

    public static String comprimizeNumber(int n){
        int a = Math.abs(n);
        StringBuilder sb = new StringBuilder();
        char sc = n < 0 ? '-' : '+', dsc;

        if(!isComprimizable(n)){
            for(int i = 0 ; i < a ; i++){
                sb.append(sc);
            }
            return sb.toString();
        }

        int sl = (int)Math.sqrt(a);System.out.println("" + a + " -> lower sqrt(a) = " + sl);
        int su = sl + 1;System.out.println("" + a + " -> upper sqrt(a) = " + su);
        int dl = a - (sl*sl);System.out.println("dl: " + dl);
        int du = (su*su) - a;System.out.println("du: " + du);
        int s, d;
        if(dl <= du){
            dsc = '+';
            s = sl;
            d = dl;
        }else{
            dsc = '-';
            s = su;
            d = du;
        }System.out.println("d: " + d);
        for(int i = 0 ; i < s ; i++){
            sb.append('+');
        }
        sb.append("[->");
        for(int i = 0 ; i < s ; i++){
            sb.append(sc);
        }
        sb.append("<]>");
        for(int i = 0 ; i < d ; i++){
            sb.append(dsc);
        }
        return sb.toString();
    }

    public static byte charToDigit(char c){
        byte val = (byte)c;
        if(val >= 48 && val <= 57){
            return (byte) (val - 48);
        }else{
            return -1;
        }
    }

    public static boolean isLegitBFCommand(char c){
        return c == '+' || c == '-' || c == ',' || c == '.' || c == '[' || c == ']' || c == '<' || c == '>';
    }
}
