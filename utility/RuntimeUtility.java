package utility;

import interpreting.RuntimeResult;

import static utility.FloatUtility.getSeparatorIndex;

public class RuntimeUtility {

    public static RuntimeResult registerNumericResult(RuntimeResult runtimeResult, RuntimeResult storedResult, String doubleValue) {
        if (doubleValue.endsWith(".0")) {
            return runtimeResult.success(runtimeResult.register(storedResult));
        } else if (Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) - 1))
                && Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) + 1))) {
            return runtimeResult.success(runtimeResult.registerDouble(storedResult));
        }

        return null;
    }
}
