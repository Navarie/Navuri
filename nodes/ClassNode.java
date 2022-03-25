package nodes;

import lexing.Position;
import lexing.Token;

@SuppressWarnings("ClassCanBeRecord")
public class ClassNode implements Node {

    private final Position posBegin;
    private final Position posEnd;
    private final Token classNameToken;
    private final Node bodyNode;

    public ClassNode(Position posBegin, Position posEnd, Token classNameToken, Node bodyNode) {
        this.posBegin = posBegin;
        this.posEnd = posEnd;
        this.classNameToken = classNameToken;
        this.bodyNode = bodyNode;

        // TODO: this.child = null;
    }

    public Node getBodyNodes() { return bodyNode; }
    public Token getClassNameToken() { return classNameToken; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "ClassNode"; }

    public String toString() {
        return "";
    }
}
