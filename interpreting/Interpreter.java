package interpreting;

import interpreting.datatypes.*;
import lexing.Position;
import lexing.Token;
import nodes.*;
import program.Applet;
import utility.RuntimeUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static errors.CustomException.*;
import static errors.ErrorCode.VARIABLE_UNINITIALISED;
import static errors.ErrorMessage.*;
import static lexing.Token.*;
import static utility.FloatUtility.getSeparatorIndex;

public class Interpreter {

    public RuntimeResult traverse(Node node) throws VariableNotInitialised, NumberOfArguments {
        if (node != null) {
            String nodeTypeString = node.getTypeString();
            switch (nodeTypeString) {
                case "interpreting.datatypes.IntegerNode" -> { return visitIntegerNode((IntegerNode) node); }
                case "FloatNode" -> { return visitFloatNode((FloatNode) node); }
                case "StringNode" -> { return visitStringNode((StringNode) node); }
                case "nodes.BooleanNode" -> { return visitBooleanNode((BooleanNode) node); }
                case "UnaryOpNode" -> { return visitUnaryOpNode((UnaryOpNode) node); }
                case "nodes.VarAssignmentNode" -> { return visitVarAssignmentNode((VarAssignmentNode) node); }
                case "VarRetrievalNode" -> { return visitVarRetrievalNode((VarRetrievalNode) node); }
                case "nodes.BinaryOpNode" -> { return traverseBinaryOpNode((BinaryOpNode) node); }
                case "ListNode" -> { return traverseListNode((ListNode) node); }
                case "IfStatement" -> { return traverseIfStatement((IfStatementNode) node); }
                case "ForLoopNode" -> { return traverseForLoop((ForLoopNode) node); }
                case "nodes.WhileLoopNode" -> { return traverseWhileLoop((WhileLoopNode) node); }
                case "nodes.FunctionDefNode" -> { return traverseFunctionDef((FunctionDefNode) node); }
                case "nodes.FunctionCallNode" -> { return traverseFunctionCall((FunctionCallNode) node); }
                case "ReturnNode" -> { return traverseReturnNode((ReturnNode) node); }
                default -> {
                    System.out.println("nodes.Node type not found. Look harder!");
                    System.out.println("Type-string: " + nodeTypeString);
                }
            }
        }
        return null;
    }

    private RuntimeResult traverseReturnNode(ReturnNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();

        if (node.getReturnedNode() != null) {
            RuntimeResult storedResult = traverse(node.getReturnedNode());
            String doubleValue = Double.toString(storedResult.getValue());
            if (storedResult.getStringValue() != null) {
                return runtimeResult.success(runtimeResult.registerString(storedResult));
            } else if (storedResult.getValue() == -42069) {
                return runtimeResult.success(runtimeResult.registerBoolean(storedResult));
            }
            if (doubleValue.endsWith(".0")) {
                return runtimeResult.success(runtimeResult.register(storedResult));
            } else if (Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) - 1))
                    && Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) + 1))) {
                return runtimeResult.success(runtimeResult.registerDouble(storedResult));
            }
        }

        return runtimeResult.success(-42069);
    }

    private RuntimeResult visitBooleanNode(BooleanNode node) {
        RuntimeResult runtimeResult = new RuntimeResult();
        BoolLiteral boolLiteral = new BoolLiteral(node.getToken().getValue())
                .setPosition(node.getPosBegin(), node.getPosEnd());

        return runtimeResult.success(boolLiteral.getBooleanValue());
    }

    private RuntimeResult traverseListNode(ListNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        List<Object> elements = new ArrayList<>();
        Value addedElement;

        for (Node element : node.getElements()) {
            switch (element.getTypeString()) {
                case "StringNode" -> addedElement = new StringLiteral(runtimeResult.registerString(traverse(element)));
                case "ListNode" -> addedElement = new ListLiteral(
                        Collections.singletonList(runtimeResult.registerList(traverse(element)).toString()));
                case "nodes.BooleanNode" -> addedElement = new StringLiteral(
                        Boolean.toString(runtimeResult.registerBoolean(traverse(element))));
                case "FloatNode" -> {
                    double value = new NumLiteral(runtimeResult.registerDouble(traverse(element))).getDoubleValue();
                    elements.add(value);
                    continue;
                }
                case "nodes.VarAssignmentNode" -> {
                    determineAddedElement(elements, element, false);
                    continue;
                }
                default -> {
                    determineAddedElement(elements, element, true);
                    continue;
                }
            }
            elements.add(addedElement);
        }

        return runtimeResult.success(new ListLiteral(elements)
                .setPosition(node.getPosBegin(), node.getPosEnd()));
    }

    private void determineAddedElement(List<Object> elements, Node element, boolean cleanTrash) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult storedResult = traverse(element);
        String doubleValue = Double.toString(storedResult.getValue());
        if (storedResult.getStringValue() != null) {
            elements.add(storedResult.getStringValue());
            return;
        } else if (doubleValue.equals("-42069.0") && !cleanTrash) {
            elements.add(storedResult.getBooleanValue());
            return;
        }
        if (doubleValue.endsWith(".0")) {
            String fixedString = doubleValue.substring(0, doubleValue.length() - 2);
            elements.add(Integer.parseInt(fixedString));

        } else if (Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) - 1))
                && Character.isDigit(doubleValue.charAt(getSeparatorIndex(doubleValue) + 1))) {
            elements.add(Double.parseDouble(doubleValue));
        }
    }

    private RuntimeResult visitVarAssignmentNode(VarAssignmentNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        String variableName = node.getVarNameToken().getValue();
        RuntimeResult storedResult = traverse(node.getValueNode());
        String floatValue = Double.toString(runtimeResult.registerDouble(storedResult));
        int separatorIndex = getSeparatorIndex(floatValue);
        if (storedResult.getStringValue() != null) {
            Applet.getVarEnv().setValue(variableName, runtimeResult.registerString(storedResult));
            return runtimeResult.success(runtimeResult.registerString(storedResult));
        } else if (storedResult.getValue() == -42069) {
            Applet.getVarEnv().setValue(variableName, runtimeResult.registerBoolean(storedResult));
            return runtimeResult.success(runtimeResult.registerBoolean(storedResult));
        }

        if (floatValue.endsWith(".0")) {
            Applet.getVarEnv().setValue(variableName, runtimeResult.register(storedResult));
            return runtimeResult.success(runtimeResult.register(storedResult));
        } else if (Character.isDigit(floatValue.charAt(separatorIndex - 1))
                && Character.isDigit(floatValue.charAt(separatorIndex + 1))) {
            Applet.getVarEnv().setValue(variableName, runtimeResult.registerDouble(storedResult));
            return runtimeResult.success(runtimeResult.registerDouble(storedResult));
        }
        return null;
    }

    private RuntimeResult visitVarRetrievalNode(VarRetrievalNode node) throws VariableNotInitialised {
        RuntimeResult runtimeResult = new RuntimeResult();
        String variableName = node.getVarNameToken().getValue();

        if (Applet.getVarEnv().getValue(variableName) == null) {
            throw new VariableNotInitialised(node.getPosBegin(), ERROR_RETRIEVING_VAR, VARIABLE_UNINITIALISED);
        } else {
            return runtimeResult.success(Applet.getVarEnv().getValue(variableName));
        }
    }

    private RuntimeResult visitIntegerNode(IntegerNode node) {
        NumLiteral returnedValue = new NumLiteral(Integer.parseInt(node.getToken().getValue()))
                .setPosition(node.getPosBegin(), node.getPosEnd());

        return new RuntimeResult().success(returnedValue.getIntegerValue());
    }

    private RuntimeResult visitFloatNode(FloatNode node) {
        NumLiteral returnedValue = new NumLiteral(Double.parseDouble(node.getToken().getValue()))
                .setPosition(node.getPosBegin(), node.getPosEnd());

        return new RuntimeResult().success(returnedValue.getDoubleValue());
    }
    private RuntimeResult visitStringNode(StringNode node) {
        StringLiteral returnedValue = new StringLiteral(
                node.getToken().getValue())
                .setPosition(node.getPosBegin(), node.getPosEnd());

        return new RuntimeResult().success(returnedValue.toString());
    }

    private RuntimeResult traverseBinaryOpNode(BinaryOpNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        RuntimeResult leftStoredResult = traverse(node.getLeftNode());
        RuntimeResult rightStoredResult = traverse(node.getRightNode());
        NumLiteral result = null;
        NumLiteral leftNodeValue;
        String leftFloatValue = Double.toString(runtimeResult.registerDouble(leftStoredResult));
         if (leftFloatValue.endsWith(".0")) {
            leftNodeValue = new NumLiteral(runtimeResult.register(leftStoredResult));
        } else {
            leftNodeValue = new NumLiteral(runtimeResult.registerDouble(leftStoredResult));
        }

        NumLiteral rightNodeValue;
        String rightFloatValue = Double.toString(runtimeResult.registerDouble(rightStoredResult));
        if (rightFloatValue.endsWith(".0")) {
            rightNodeValue = new NumLiteral(runtimeResult.register(rightStoredResult));
        } else {
            rightNodeValue = new NumLiteral(runtimeResult.registerDouble(rightStoredResult));
        }

        StringLiteral leftStringValue;
        StringLiteral rightStringValue;
        String operatorType = node.getOperatorToken().getValue();
        if (leftStoredResult.getStringValue() != null && rightStoredResult.getStringValue() != null
                && operatorType.equals(T_PLUS)) {
            leftStringValue = new StringLiteral(runtimeResult.registerString(leftStoredResult));
            rightStringValue = new StringLiteral(runtimeResult.registerString(rightStoredResult));
            String stringResult = leftStringValue.concatenateWithSpace(rightStringValue).getValue();
            return runtimeResult.success(stringResult);
        }

        switch (operatorType) {
            case T_PLUS -> result = leftNodeValue.add(rightNodeValue);
            case T_MINUS -> result = leftNodeValue.subtract(rightNodeValue);
            case T_MULTI -> result = leftNodeValue.multiply(rightNodeValue);
            case T_DIV -> result = leftNodeValue.divide(rightNodeValue);
            case T_POWER -> result = leftNodeValue.raiseToPower(rightNodeValue);
            case T_MODULO -> result = leftNodeValue.applyModulo(rightNodeValue);
            case T_MINIMUM -> result = leftNodeValue.minimumOf(rightNodeValue);
            case T_MAXIMUM -> result = leftNodeValue.maximumOf(rightNodeValue);
            case T_D_EQUALS -> result = leftNodeValue.isEqualTo(rightNodeValue);
            case T_NOT_EQUALS -> result = leftNodeValue.isNotEqualTo(rightNodeValue);
            case T_LESS_THAN -> result = leftNodeValue.isLessThan(rightNodeValue);
            case T_GREATER_THAN -> result = leftNodeValue.isGreaterThan(rightNodeValue);
            case T_LTE -> result = leftNodeValue.isLessOrEqualTo(rightNodeValue);
            case T_GTE -> result = leftNodeValue.isGreaterOrEqualTo(rightNodeValue);
            case "then", "var", "else", "while", "for", "return" -> {}
            default -> {
                System.out.println("What kinda tool are you using??");
                System.out.println(operatorType);
            }
        }

        if (node.getOperatorToken().compareTo(T_KEYWORD, KEYWORDS[1])) {
            result = leftNodeValue.applyAND(rightNodeValue);
        } else if (node.getOperatorToken().compareTo(T_KEYWORD, KEYWORDS[2])) {
            result = leftNodeValue.applyOR(rightNodeValue);
        }

        return getRuntimeResult(runtimeResult, result, node.getPosBegin(), node.getPosEnd());
    }

    private RuntimeResult getRuntimeResult(RuntimeResult runtimeResult, NumLiteral result, Position posBegin, Position posEnd) {
        if (result != null) {
            String resultDoubleValue = Double.toString(result.getDoubleValue());
            if (resultDoubleValue.endsWith(".0")) {
                return runtimeResult.success(
                        result.setPosition(posBegin, posEnd).getIntegerValue());
            } else {
                return runtimeResult.success(
                        result.setPosition(posBegin, posEnd).getDoubleValue());
            }
        } else {
            return null;
        }
    }

    private RuntimeResult visitUnaryOpNode(UnaryOpNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        NumLiteral nodeValue;
        String doubleNodeValue = Double.toString(runtimeResult.registerDouble(traverse(node.getNode())));
        if (doubleNodeValue.endsWith(".0")) {
            nodeValue = new NumLiteral(runtimeResult.register(traverse(node.getNode())));
        } else {
            nodeValue = new NumLiteral(runtimeResult.registerDouble(traverse(node.getNode())));
        }
        NumLiteral result = null;

        if (node.getOperatorToken().getValue().equals(T_MINUS)) {
            result = nodeValue.multiply(new NumLiteral(- 1));
        } else if (node.getOperatorToken().compareTo(T_KEYWORD, KEYWORDS[3])) {
            result = nodeValue.applyNOT();
        } else if (node.getOperatorToken().compareTo(T_UNARY_OP, T_LOGARITHM)) {
            result = nodeValue.applyLogarithmBase2();
        } else if (node.getOperatorToken().compareTo(T_UNARY_OP, T_SQUARE_ROOT)) {
            result = nodeValue.applySquareRoot();
        } else if (node.getOperatorToken().compareTo(T_UNARY_OP, T_ABSOLUTE)) {
            result = nodeValue.absoluteValue();
        }

        return getRuntimeResult(runtimeResult, result, node.getPosBegin(), node.getPosEnd());
    }

    @SuppressWarnings({"unused", "CommentedOutCode"})
    private RuntimeResult visitClassNode(ClassNode node) {
//        interpreting.RuntimeResult runtimeResult = new interpreting.RuntimeResult();

//
//        runtimeResult.register(visit(node.getBodyNodes()));
//        interpreting.datatypes.Klass klass = new interpreting.datatypes.Klass(node.getClassNameToken().getValue(),
//                node.getPosBegin(), node.getPosEnd(), Applet.getClassEnv());
//        klass..setPosition(node.getPosBegin(), node.getPosEnd());
//        Applet.getClassEnv().setValue(node.getClassNameToken().getValue(), klass);
//
//        return runtimeResult.success(klass);

        return null;
    }

    private RuntimeResult traverseIfStatement(IfStatementNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        NumLiteral conditionValue;
        NumLiteral expressionValue;
        NumLiteral optElseCaseValue;

        for (int i=0; i < node.getConditions().size(); i++) {
            conditionValue = new NumLiteral(runtimeResult.register(traverse(node.getConditions().get(i))));

            if (conditionValue.isTrue()) {
                if (node.getExpressions().size() > 0) {
                    expressionValue = new NumLiteral(runtimeResult.register(traverse(node.getExpressions().get(i))));
                    return runtimeResult.success(expressionValue
                            .setPosition(node.getPosBegin(), node.getPosEnd())
                            .getIntegerValue());
                }
            }
        }
        if (node.getOptElseCase() != null) {
            optElseCaseValue = new NumLiteral(runtimeResult.register(traverse(node.getOptElseCase())));
            return runtimeResult.success(optElseCaseValue.getIntegerValue());
        }

        return runtimeResult.success(-42069);
    }

    private RuntimeResult traverseForLoop(ForLoopNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        List<Object> elements = new ArrayList<>();
        List<Value> addedElements = new ArrayList<>();
        NumLiteral startValue = new NumLiteral(runtimeResult.register(traverse(node.getStartValueNode())));
        NumLiteral endValue = new NumLiteral(runtimeResult.register(traverse(node.getEndValueNode())));
        NumLiteral stepValue;
        if (node.getStepValueNode() != null) {
            stepValue = new NumLiteral(runtimeResult.register(traverse(node.getStepValueNode())));
        } else { stepValue = new NumLiteral(1); }

        boolean runLoop = true;
        int loopIndex = startValue.getIntegerValue();
        while (runLoop) {
            Applet.getVarEnv().setValue(node.getVariableNameToken().getValue(), new NumLiteral(loopIndex).getIntValue());

            loopIndex = loopIndex + stepValue.getIntegerValue();
            if (loopIndex >= 0) {
                runLoop = (loopIndex < endValue.getIntegerValue());
            } else {
                runLoop = (loopIndex > endValue.getIntegerValue());
            }

            if (executeLoopBody(runtimeResult, elements, addedElements,
                    node.getBodyNode(), node.getShouldReturnNull(),
                    node.getPosBegin(), node.getPosEnd())) {
                return null;
            }
        }
        return runtimeResult.success(addedElements);
    }

    private RuntimeResult traverseWhileLoop(WhileLoopNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        List<Object> elements = new ArrayList<>();
        List<Value> addedElements = new ArrayList<>();
        NumLiteral condition;
        while (true) {
            condition = new NumLiteral(runtimeResult.register(traverse(node.getConditionNode())));
            if (!condition.isTrue()) {
                break;
            }

            if (executeLoopBody(runtimeResult, elements, addedElements,
                    node.getBodyNode(), node.getShouldReturnNull(), node.getPosBegin(), node.getPosEnd())) {
                return null;
            }
        }
        return runtimeResult.success(addedElements);
    }

    private boolean executeLoopBody(RuntimeResult runtimeResult, List<Object> elements,
                                    List<Value> addedElements, Node bodyNode, boolean returnNull,
                                    Position posBegin, Position posEnd) throws VariableNotInitialised, NumberOfArguments {
        NumLiteral body;
        body = new NumLiteral(runtimeResult.register(traverse(bodyNode)));
        if (returnNull) {
            addedElements.add(new ListLiteral(elements)
                    .setPosition(posBegin, posEnd));
        } else {
            return true;
        }
        elements.add(body);
        return false;
    }

    private RuntimeResult traverseFunctionDef(FunctionDefNode node) {
        RuntimeResult runtimeResult = new RuntimeResult();
        String functionName = "";
        if (node.getVariableNameToken() != null) {
            functionName = node.getVariableNameToken().getValue();
        }
        Node bodyNode = node.getBodyNode();
        List<String> argNames = new ArrayList<>();
        for (Token argName : node.getArgNameTokens()) argNames.add(argName.getValue());

        Value closure = new FunctionClosure(functionName, bodyNode, argNames)
                .setPosition(node.getPosBegin(), node.getPosEnd());

        if (node.getVariableNameToken() != null) {
            Applet.getFunEnv().setValue(functionName, (FunctionClosure) closure);
        }

        return runtimeResult.success(closure);
    }

    private RuntimeResult traverseFunctionCall(FunctionCallNode node) throws VariableNotInitialised, NumberOfArguments {
        RuntimeResult runtimeResult = new RuntimeResult();
        List<Value> arguments = new ArrayList<>();

        for (Node argNode : node.getArgNodes()) {
            if (argNode.getTypeString().equals("interpreting.datatypes.IntegerNode")) {
                arguments.add(new NumLiteral(runtimeResult.register(traverse(argNode))));
            } else if (argNode.getTypeString().equals("FloatNode")) {
                arguments.add(new NumLiteral(runtimeResult.registerDouble(traverse(argNode))));
            } else if (argNode.getTypeString().equals("StringNode")) {
                runtimeResult.register(traverse(argNode));
                arguments.add(new StringLiteral(argNode.toString()));
            } else if (argNode.getTypeString().equals("nodes.BooleanNode")) {
                runtimeResult.register(traverse(argNode));
                arguments.add(new BoolLiteral(argNode.toString()));
            }
        }

        String functionName = node.getCalledNode().toString().subSequence(7, node.getCalledNode().toString().length() - 1).toString();
        Value closure = Applet.getFunEnv().getValue(functionName);
        RuntimeResult storedResult = closure.execute(arguments, Applet.getFunEnv().getValue(functionName));
        String doubleValue = Double.toString(storedResult.getValue());

        if (storedResult.getStringValue() != null) {
            return runtimeResult.success(runtimeResult.registerString(storedResult));
        } else if (doubleValue.equals("-42069.0")) {
            return runtimeResult.success(runtimeResult.registerBoolean(storedResult));
        }

        return RuntimeUtility.registerNumericResult(runtimeResult, storedResult, doubleValue);
    }
}
