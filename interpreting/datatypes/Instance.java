package interpreting.datatypes;

import interpreting.RuntimeResult;
import interpreting.environments.ClassEnvironment;
import lexing.Position;

import java.util.List;

public class Instance implements Value {

    private final Klass parent;
    private ClassEnvironment classEnv;


    public Instance(Klass parent) {
        this.parent = parent;
        this.classEnv = null;
    }

    public ClassEnvironment getClassEnv() { return classEnv; }

    public void createClassEnv(ClassEnvironment classEnv) {
        this.classEnv = classEnv;
    }


    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        return null;
    }

    public void setName(String name) {

    }

    public Value setPosition(Position posBegin, Position posEnd) {
        return null;
    }

    public Position getPosEnd() {
        return null;
    }

    public Position getPosBegin() {
        return null;
    }

    public Object getIntValue() {
        return null;
    }

    public Instance copy() {
        return this;
    }
}
