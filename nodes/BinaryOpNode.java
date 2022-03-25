package nodes;

import lexing.Position;
import lexing.Token;

import static lexing.Token.*;

public class BinaryOpNode implements Node {

    private final Node leftNode;
    private final Node rightNode;
    private final Token operatorToken;
    private Position posBegin = null;
    private Position posEnd = null;

    public BinaryOpNode(Node leftNode, Token operatorToken, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.operatorToken = operatorToken;
        if (rightNode != null) {
            this.posEnd = this.rightNode.getPosEnd();
        }
        if (leftNode != null) {
            this.posBegin = this.leftNode.getPosBegin();
        }
    }

    public String getTypeString() { return "nodes.BinaryOpNode"; }
    public Node getLeftNode() {
        return leftNode;
    }
    public Node getRightNode() {
        return rightNode;
    }
    public Token getOperatorToken() { return operatorToken; }
    public Position getPosEnd() { return posEnd; }
    public Position getPosBegin() { return posBegin; }

    public String toString() {
        String adjustedOperatorToken;
        if (getOperatorToken().getType().equals(T_COMP_OP)) {
            adjustedOperatorToken = getOperatorToken().getValue();
            return "ComparisonExp(" + this.leftNode.toString() + T_WHITESPACE + adjustedOperatorToken + T_WHITESPACE + rightNode.toString() + ")";
        }
        return "BinOpExp(" + this.leftNode + ", " + operatorToken + ", " + rightNode + ")";
    }

}
