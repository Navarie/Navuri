package interpreting.environments;

import java.util.HashMap;
import java.util.Map;

import static errors.CustomException.*;
import static lexing.Token.*;
import static utility.FloatUtility.getSeparatorIndex;

public class VariableEnvironment {

    private final Map<String, Object> valuesEnv;

    public VariableEnvironment() {
        this.valuesEnv = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String variableName) throws VariableNotInitialised {
        return (T) valuesEnv.get(variableName);
    }

    @SuppressWarnings("CommentedOutCode")
    public <T> void setValue(String variableName, T value) {
//        if (value != null) {
//            System.out.println("Set " + variableName + " to " + value);
//        }
        valuesEnv.put(variableName, value);
    }

    public void populateEnvironment() {

    }

    public String toString() {
        StringBuilder builtString = new StringBuilder();
        builtString.append(T_L_BRACKET);
        if (valuesEnv.isEmpty()) {
            builtString.append(T_R_BRACKET);
            return builtString.toString();
        }
        for (Map.Entry<String, Object> entry : valuesEnv.entrySet()) {
            if (entry.getValue() != null) {
                builtString.append(entry.getKey()).append(": ");
                if (roundFloats(entry.getValue()).length() > 0) {
                    builtString.append(roundFloats(entry.getValue()));
                } else {
                    builtString.append(entry.getValue());
                }
                builtString.append(T_COMMA).append(T_WHITESPACE);
            }
        }
        builtString.replace(builtString.length() - 2, builtString.length(), T_EMPTY_STRING);

        builtString.append(T_R_BRACKET);
        return builtString.toString();
    }

    private <T> String roundFloats(T value) {
        StringBuilder builtString = new StringBuilder();
        if (value instanceof Double) {
            int separatorIndex = getSeparatorIndex(String.valueOf(value));
            if (String.valueOf(value).length() > (separatorIndex + 3)) {
                builtString.append("\u2248");
                builtString.append(String.valueOf(value), 0, separatorIndex + 3);
            }
        }
        return builtString.toString();
    }
}
