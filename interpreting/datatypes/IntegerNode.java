package interpreting.datatypes;

import lexing.Position;
import lexing.Token;
import nodes.Node;

public class IntegerNode implements Node {

    private final Token token;
    private final Position posBegin;
    private final Position posEnd;

    public IntegerNode(Token token) {
        this.token = token;
        this.posBegin = this.token.getPosBegin();
        this.posEnd = this.token.getPosEnd();
    }

    public Token getToken() {
        return token;
    }
    public String getTypeString() {
        return "interpreting.datatypes.IntegerNode";
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String toString() {
        return "" + this.token;
    }
}
