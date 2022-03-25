package nodes;

import lexing.Position;
import lexing.Token;

public class StringNode implements Node {

    private final Token token;
    private final Position posBegin;
    private final Position posEnd;

    public StringNode(Token token) {
        this.token = token;
        this.posBegin = this.token.getPosBegin();
        this.posEnd = this.token.getPosEnd();
    }

    public Token getToken() {
        return token;
    }
    public String getTypeString() {
        return "StringNode";
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String toString() {
        return "" + this.token;
    }
}
