package nodes;

import lexing.Position;
import lexing.Token;

public class BooleanNode implements Node {

    private final Token token;
    private final Position posBegin;
    private final Position posEnd;

    public BooleanNode(Token token) {
        this.token = token;
        this.posBegin = this.token.getPosBegin();
        this.posEnd = this.token.getPosEnd();
    }

    public Token getToken() {
        return token;
    }
    public String getTypeString() {
        return "nodes.BooleanNode";
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String toString() {
        return "" + this.token;
    }
}
