package interpreting.datatypes;

import interpreting.RuntimeResult;
import lexing.Position;

import java.util.List;

public class BoolLiteral implements Value {

    private final String value;
    private Position posBegin = null;
    private Position posEnd = null;

    public BoolLiteral(String value) {
        this.value = value;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        return null;
    }

    public void setName(String name) {}
    public BoolLiteral setPosition(Position posBegin, Position posEnd) {
        if (posBegin != null) this.posBegin = posBegin;
        if (posEnd != null) this.posEnd = posEnd;

        return this;
    }

    public Position getPosEnd() {
        return posEnd;
    }
    public Position getPosBegin() {
        return posBegin;
    }
    public Object getIntValue() {
        return null;
    }
    public String getValue() { return value; }

    public boolean getBooleanValue() {
        return value.equals(" true");
    }

    public String toString() {
        if (value == null) return "";
        return Boolean.toString(getBooleanValue());
    }

    public BoolLiteral copyBoolLiteral() {
        BoolLiteral copy = new BoolLiteral(getValue());
        copy.setPosition(getPosBegin(), getPosEnd());

        return copy;
    }
}
