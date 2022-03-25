package interpreting.datatypes;

import interpreting.Interpreter;
import interpreting.RuntimeResult;
import lexing.Position;
import nodes.Node;

import java.util.ArrayList;
import java.util.List;

import static errors.CustomException.*;
import static utility.RuntimeUtility.registerNumericResult;

public class FunctionClosure extends CoreFunction implements Value {

    private final String name;
    private final Node bodyNode;
    private List<String> argNames;

    public FunctionClosure(String name, Node bodyNode, List<String> argNames) {
        super(name);
        super.setBodyNode(bodyNode);
        super.setArgNames(argNames);
        this.name = name;
        this.bodyNode = bodyNode;

        if (argNames != null) {
            this.argNames = new ArrayList<>(argNames);
        }
    }

    public RuntimeResult execute(List<Value> args, FunctionClosure closure) throws VariableNotInitialised,
            NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        Interpreter interpreter = new Interpreter();
        System.out.println("In interpreting.datatypes.FunctionClosure...");

        runtimeResult.register(super.validateAndPopulate(getArgNames(), args));

        RuntimeResult storedResult = interpreter.traverse(closure.getBodyNode());

        if (storedResult.getStringValue() != null) {
            return runtimeResult.success(runtimeResult.registerString(storedResult));
        } else if (storedResult.getValue() == -42069) {
            return runtimeResult.success(runtimeResult.registerBoolean(storedResult));
        }

        System.out.println("Determining return value... ");
        String doubleValue = Double.toString(storedResult.getValue());
        return registerNumericResult(runtimeResult, storedResult, doubleValue);
    }

    public void setName(String name) {}
    public Value setPosition(Position posBegin, Position posEnd) { return this; }

    public Node getBodyNode() { return bodyNode; }
    public Position getPosBegin() { return super.getPosBegin(); }
    public Position getPosEnd() { return super.getPosEnd(); }
    public List<String> getArgNames() { return argNames; }
    public String getName() { return name; }

    public String toString() {
        return "<function " + getName() + ": args " + getArgNames() + " {\n\t" + getBodyNode() + ">";
    }
}
