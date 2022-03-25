package interpreting.datatypes;

import interpreting.RuntimeResult;
import lexing.Position;

import java.util.List;

import static lexing.Token.*;

public class ListLiteral implements Value {

    private final List<Object> elements;
    private Position posBegin = null;
    private Position posEnd = null;

    public ListLiteral(List<Object> elements) {
        this.elements = elements;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        return null;
    }
    public void setName(String name) {}

    public ListLiteral setPosition(Position posBegin, Position posEnd) {
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

    public List<Object> getElements() {
        return elements;
    }

    public <T> ListLiteral concatenate(T other) {
        if (other instanceof ListLiteral) {
            ListLiteral newList = copyList();
            newList.getElements().add(other);
            return newList;
        } else {
            return null;
        }
    }

    public ListLiteral copyList() {
        ListLiteral copy = new ListLiteral(getElements());
        copy.setPosition(getPosBegin(), getPosEnd());

        return copy;
    }

    public String toString() {
        StringBuilder builtString = new StringBuilder();

        for (int i=0; i < getElements().size(); i++) {
            if (!getElements().get(i).toString().equals("-42069")) {
                builtString.append(getElements().get(i)).append(T_COMMA).append(T_WHITESPACE);
            }
        }
        if (builtString.length() > 1) {
            builtString.replace(builtString.length() - 2, builtString.length(), T_EMPTY_STRING);
        }

        return builtString.toString();
    }
}
