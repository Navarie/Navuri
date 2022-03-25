package interpreting.datatypes;

import interpreting.RuntimeResult;
import lexing.Position;

import java.util.List;

import static lexing.Token.T_WHITESPACE;

public class StringLiteral implements Value {

    private final String value;
    private Position posBegin = null;
    private Position posEnd = null;

    public StringLiteral(String value) {
        this.value = value;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        return null;
    }

    public void setName(String name) {}

    public StringLiteral setPosition(Position posBegin, Position posEnd) {
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
    public String toString() {
        return value;
    }
    public String getValue() { return value; }

    public StringLiteral concatenateWithSpace(StringLiteral other) {
        StringBuilder builtString = new StringBuilder();
        builtString.append(this.value);
        builtString.replace(builtString.length() - 1, builtString.length(), "");
        builtString.append(T_WHITESPACE);
        int otherStartIndex = builtString.length();
        builtString.append(other.getValue());
        builtString.replace(otherStartIndex, otherStartIndex + 1, "");
        return new StringLiteral(builtString.toString());
    }
}
