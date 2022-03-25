package nodes;

import lexing.Position;
import lexing.Token;

public class VarRetrievalNode implements Node {

    private final Token varNameToken;
    private final Position posBegin;
    private final Position posEnd;
    private final Node child;

    public VarRetrievalNode(Token varNameToken) {
        this.varNameToken = varNameToken;
        this.posBegin = this.varNameToken.getPosBegin();
        this.posEnd = this.varNameToken.getPosEnd();
        this.child = null;
    }

    public Node getChild() { return child; }
    public Token getVarNameToken() {
        return varNameToken;
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "VarRetrievalNode"; }

    public String toString() { return "VarExp(" + varNameToken.getValue() + ")"; }
}
