package nodes;

import lexing.Position;
import lexing.Token;

public class VarAssignmentNode implements Node {

    private final Token varNameToken;
    private final Node valueNode;
    private final Position posBegin;
    private final Position posEnd;

    public VarAssignmentNode(Token varNameToken, Node valueNode) {
        this.varNameToken = varNameToken;
        this.valueNode = valueNode;
        posBegin = varNameToken.getPosBegin();
        posEnd = valueNode.getPosEnd();
    }

    public Token getVarNameToken() { return varNameToken; }
    public Node getValueNode() { return valueNode; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String getTypeString() { return "nodes.VarAssignmentNode"; }
    public String toString() { return "VarAssignExp(" + getVarNameToken().getValue() + ", " + getValueNode().toString() + ")"; }
}
