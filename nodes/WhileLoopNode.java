package nodes;

import lexing.Position;

public class WhileLoopNode implements Node {

    private final Node conditionNode;
    private final Node bodyNode;
    private final Position posBegin;
    private final Position posEnd;
    private final boolean shouldReturnNull;

    public WhileLoopNode(Node condition, Node body, boolean bool) {
        this.conditionNode = condition;
        this.bodyNode = body;
        this.shouldReturnNull = bool;

        this.posBegin = condition.getPosBegin();
        this.posEnd = body.getPosEnd();
    }

    public boolean getShouldReturnNull() { return shouldReturnNull; }
    public Node getBodyNode() { return bodyNode; }
    public Node getConditionNode() { return conditionNode; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "nodes.WhileLoopNode"; }

    public String toString() {

        return "nodes.WhileLoopNode(" +
                getConditionNode().toString() +
                ") {\n\t" +
                getBodyNode().toString() +
                "\n}";
    }
}
