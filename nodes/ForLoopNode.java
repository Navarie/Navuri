package nodes;

import lexing.Position;
import lexing.Token;

import static lexing.Token.T_EQUALS;
import static lexing.Token.T_WHITESPACE;

public class ForLoopNode implements Node {

    private final Position posBegin;
    private final Position posEnd;
    private final Token variableNameToken;
    private final Node startValueNode;
    private final Node endValueNode;
    private final Node stepValueNode;
    private final Node bodyNode;
    private final boolean shouldReturnNull;

    public ForLoopNode(Token variableNameToken, Node startValue, Node endValue, Node stepValue, Node body, boolean bool) {
        this.variableNameToken = variableNameToken;
        this.startValueNode = startValue;
        this.endValueNode = endValue;
        this.stepValueNode = stepValue;
        this.bodyNode = body;
        this.shouldReturnNull = bool;

        this.posBegin = variableNameToken.getPosBegin();
        this.posEnd = body.getPosEnd();
    }

    public boolean getShouldReturnNull() { return shouldReturnNull; }
    public Node getBodyNode() { return bodyNode; }
    public Node getEndValueNode() { return endValueNode; }
    public Node getStartValueNode() { return startValueNode; }
    public Node getStepValueNode() { return stepValueNode; }
    public Token getVariableNameToken() { return variableNameToken; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "ForLoopNode"; }

    public String toString() {
        StringBuilder builtString = new StringBuilder();
        builtString.append("ForLoopNode( for( ")
                .append(variableNameToken)
                .append(T_WHITESPACE).append(T_EQUALS).append(T_WHITESPACE)
                .append(getStartValueNode())
                .append(T_WHITESPACE).append("until ")
                .append(getEndValueNode().toString());
        if (stepValueNode != null) {
            builtString.append(T_WHITESPACE).append(getStepValueNode().toString())
                    .append(T_WHITESPACE);
        }
        builtString.append(") {\n\t").append(bodyNode)
                .append("\n}");

        return builtString.toString();
    }
}
