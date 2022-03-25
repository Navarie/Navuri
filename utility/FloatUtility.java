package utility;

public class FloatUtility {

    public static int getSeparatorIndex(String floatValue) {
        int separatorIndex = 0;
        for (int i = 0; i < floatValue.length(); i++) {
            if (floatValue.charAt(i) == '.') {
                separatorIndex = i;
                break;
            }
        }
        return separatorIndex;
    }
}
