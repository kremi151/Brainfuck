package lu.kremi151.brainfuck.util;

/**
 * Created by michm on 08.06.2017.
 */

public class FSHelper {

    public static boolean checkFileNameWithoutSuffix(String name){
        for(int i = 0 ; i < name.length() ; i++){
            char c = name.charAt(i);
            if(!isValidFileNameChar(c)){
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFileNameChar(char c){
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57) || c == ' ' || c == '-' || c == '_';
    }
}
