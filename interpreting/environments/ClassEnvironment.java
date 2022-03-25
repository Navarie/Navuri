package interpreting.environments;

import interpreting.datatypes.Value;

import java.util.HashMap;
import java.util.Map;

public class ClassEnvironment {

    private final Map<String, Value> classEnv;
    private final Map<String, Value> parentClassEnv;

    public ClassEnvironment() {
        this.classEnv = new HashMap<>();
        this.parentClassEnv = new HashMap<>();
    }

    public Map<String, Value> getClassEnv() { return classEnv; }

    public Value getValue(String variableName) {
        Value closure = classEnv.getOrDefault(variableName, null);
        if (parentClassEnv.size() > 0) {
            closure = parentClassEnv.getOrDefault(variableName, null);
        }
        return closure;
    }

    public void setValue(String variableName, Value closure) {
        classEnv.put(variableName, closure);
    }

    @SuppressWarnings("unused")
    public void remove(String variableName) {
        classEnv.remove(variableName);
    }

    public void populateEnvironment() {

    }
}
