package interpreting.datatypes;

import interpreting.RuntimeResult;
import interpreting.environments.ClassEnvironment;
import lexing.Position;

import java.util.List;

import static errors.CustomException.*;

public class Klass implements Value {

    private String name;
    private Position posBegin;
    private Position posEnd;
    private final ClassEnvironment classEnv;

    public Klass(String name, Position posBegin, Position posEnd, ClassEnvironment classEnv) {
        this.name = name;
        this.posBegin = posBegin;
        this.posEnd = posEnd;
        this.classEnv = classEnv;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        Instance instance = new Instance(this);

        instance.createClassEnv(getClassEnv());

        for (String name : getClassEnv().getClassEnv().keySet()) {
            instance.getClassEnv().setValue(name, getClassEnv().getClassEnv().get(name));
        }
        for (String name : instance.getClassEnv().getClassEnv().keySet()) {
            instance.getClassEnv().getClassEnv().get(name);
        }

        instance.getClassEnv().setValue("this", instance);
        Value method = instance.getClassEnv().getClassEnv().getOrDefault(getName(), null);

        if (method != null) {
            runtimeResult.register(method.execute(args, null));
        }

        return runtimeResult.success(instance.setPosition(getPosBegin(), getPosEnd()));
    }

    public void setName(String name) { this.name = name; }
    public ClassEnvironment getClassEnv() { return classEnv; }

    public Value setPosition(Position posBegin, Position posEnd) {
        this.posBegin = posBegin;
        this.posEnd = posEnd;
        return this;
    }

    public Position getPosEnd() { return posEnd; }
    public Position getPosBegin() { return posBegin; }
    public String getName() { return name; }
    public Object getIntValue() { return null; }
}
