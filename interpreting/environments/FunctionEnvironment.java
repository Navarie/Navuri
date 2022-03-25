package interpreting.environments;

import interpreting.datatypes.CoreFunction;
import interpreting.datatypes.LibFunction;

import java.util.HashMap;
import java.util.Map;

public class FunctionEnvironment {

    private final Map<String, CoreFunction> funEnv;
    private final Map<String, CoreFunction> parentFunEnv;

    public FunctionEnvironment() {
        this.funEnv = new HashMap<>();
        this.parentFunEnv = new HashMap<>();
    }

    public CoreFunction getValue(String variableName) {
        CoreFunction closure = funEnv.getOrDefault(variableName, null);
        if (parentFunEnv.size() > 0) {
            closure = parentFunEnv.getOrDefault(variableName, null);
        }
        return closure;
    }

    public void setValue(String variableName, CoreFunction closure) {
        funEnv.put(variableName, closure);
    }

    @SuppressWarnings("unused")
    public void remove(String variableName) {
        funEnv.remove(variableName);
    }

    public void populateEnvironment() {
        setValue("print", new LibFunction("Print"));
        setValue("run", new LibFunction("Run"));
    }
}
