package nodes;

import lexing.Position;

@SuppressWarnings("ClassCanBeRecord")
public class ReturnNode implements Node {

    private final Position posBegin;
    private final Position posEnd;
    private final Node returnedNode;

    public ReturnNode(Node returnedNode, Position posBegin, Position posEnd) {
        this.posBegin = posBegin;
        this.posEnd = posEnd;
        this.returnedNode = returnedNode;
    }

    public Node getReturnedNode() { return returnedNode; }
    public String getTypeString() {
        return "ReturnNode";
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String toString() {
        return getReturnedNode().toString();
    }
}
