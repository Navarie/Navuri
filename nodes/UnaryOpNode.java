package nodes;

import lexing.Position;
import lexing.Token;

public class UnaryOpNode implements Node {

    private final Token operatorToken;
    private final Node node;
    private final Position posBegin;
    private final Position posEnd;

    public UnaryOpNode(Token operatorToken, Node node) {
        this.operatorToken = operatorToken;
        this.node = node;
        this.posBegin = this.operatorToken.getPosBegin();
        this.posEnd = node.getPosEnd();
    }

    public Token getOperatorToken() { return operatorToken; }
    public Node getNode() {
        return node;
    }
    public String getTypeString() { return "UnaryOpNode"; }
    public Position getPosBegin() {
        return posBegin;
    }
    public Position getPosEnd() {
        return posEnd;
    }

    public String toString() {
        return "UnaryOpExp(" + operatorToken + ", " + node + ")";
    }
}
