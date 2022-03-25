package interpreting.datatypes;

import interpreting.Interpreter;
import interpreting.RuntimeResult;
import lexing.Position;
import nodes.Node;
import program.Applet;

import java.util.List;

import static errors.CustomException.*;
import static errors.ErrorCode.INCORRECT_ARGUMENTS_NO;
import static utility.RuntimeUtility.registerNumericResult;

public class CoreFunction implements Value {

    private String name;
    private Position posBegin;
    private Position posEnd;
    private Node bodyNode;
    private List<String> argNames;

    public CoreFunction(String name) {
        this.name = name;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        Interpreter interpreter = new Interpreter();
        System.out.println("In interpreting.datatypes.CoreFunction...");

        if (closure.getArgNames() != null) {
            if (args.size() != closure.getArgNames().size()) {
                throw new NumberOfArguments(posBegin, "\nIncorrect number of arguments!", INCORRECT_ARGUMENTS_NO);
            }
            for (int i=0; i < args.size(); i++) {
                String argName = closure.getArgNames().get(i);
                Value argValue = args.get(i);
                Applet.getVarEnv().setValue(argName, Integer.parseInt(argValue.toString()));
            }
        }
        RuntimeResult storedResult = interpreter.traverse(closure.getBodyNode());
        String doubleValue = Double.toString(storedResult.getValue());
        if (storedResult.getStringValue() != null) {
            return runtimeResult.success(runtimeResult.registerString(storedResult));
        } else if (doubleValue.equals("-42069.0")) {
            return runtimeResult.success(runtimeResult.registerBoolean(storedResult));
        }

        return registerNumericResult(runtimeResult, storedResult, doubleValue);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBodyNode(Node bodyNode) { this.bodyNode = bodyNode; }
    public void setArgNames(List<String> argNames) { this.argNames = argNames; }

    public Value setPosition(Position posBegin, Position posEnd) {
        if (posBegin != null) this.posBegin = posBegin;
        if (posEnd != null) this.posEnd = posEnd;

        return this;
    }

    public RuntimeResult validateArguments(List<String> argNames, List<Value> arguments) {
        RuntimeResult runtimeResult = new RuntimeResult();
        if (argNames != null) {
            if (arguments.size() != argNames.size()) {
                return null;
            }
        }
        return runtimeResult.success(null);
    }

    public void populateArguments(List<String> argNames, List<Value> arguments) {
        for (int i=0; i < arguments.size(); i++) {
            String argName = argNames.get(i);
            Value argValue = arguments.get(i);
            Applet.getVarEnv().setValue(argName, argValue.getIntValue());
        }
    }

    public RuntimeResult validateAndPopulate(List<String> argNames, List<Value> arguments) {
        RuntimeResult runtimeResult = new RuntimeResult();
        runtimeResult.register(validateArguments(argNames, arguments));
        populateArguments(argNames, arguments);
        return runtimeResult.success(null);
    }

    public Position getPosBegin() { return posBegin; }
    public Object getIntValue() {
        return null;
    }
    public Position getPosEnd() { return posEnd; }
    public String getName() { return name; }
    public List<String> getArgNames() { return argNames; }
    public Node getBodyNode() { return bodyNode; }

    public String toString() {
        return "<function " + getName() + ">";
    }
}
