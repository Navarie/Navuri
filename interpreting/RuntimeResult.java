package interpreting;

import interpreting.datatypes.CoreFunction;
import interpreting.datatypes.ListLiteral;
import nodes.Node;

public class RuntimeResult {

    private int value;
    private double floatValue;
    private String stringValue;
    private boolean booleanValue;
    private ListLiteral listValue;
    private CoreFunction closure;
    private Node returnValue;

    public RuntimeResult() {
        this.value = -42069;
        this.floatValue = -420.69;
        this.stringValue = null;
    }

    public int register(RuntimeResult result) {
        if (result != null) {
            return (int) result.getValue();
        } else {
            return (int) getValue();
        }
    }
    public double registerDouble(RuntimeResult result) {
        if (result != null) {
            return result.getValue();
        } else {
            return getValue();
        }
    }
    public String registerString(RuntimeResult result) {
        if (result != null) {
            return result.getStringValue();
        } else {
            return getStringValue();
        }
    }
    public ListLiteral registerList(RuntimeResult result) {
        if (result != null) {
            return result.getListValue();
        } else {
            return getListValue();
        }
    }
    public boolean registerBoolean(RuntimeResult result) {
        if (result != null) {
            return result.getBooleanValue();
        } else {
            return getBooleanValue();
        }
    }

    public <T> RuntimeResult success(T value) {
        if (value instanceof Integer) {
            this.value = (Integer) value;
        } else if (value instanceof String) {
            this.stringValue = (String) value;
        } else if (value instanceof ListLiteral) {
            this.listValue = (ListLiteral) value;
        } else if (value instanceof CoreFunction) {
            this.closure = (CoreFunction) value;
        } else if (value instanceof Double) {
            this.floatValue = (Double) value;
        } else if (value instanceof Boolean) {
            this.booleanValue = (Boolean) value;
        } else if (value instanceof Node) {
            this.returnValue = (Node) value;
        }
        return this;
    }

    public ListLiteral getListValue() { return listValue; }
    public String getStringValue() { return stringValue; }
    public boolean getBooleanValue() { return booleanValue; }
    public <T> double getValue() {
        if (floatValue != -420.69) return floatValue;
            else return value;
    }
    public Node getReturnValue() { return returnValue; }
}
