package nodes;

import lexing.Position;

import java.util.ArrayList;
import java.util.List;

import static lexing.Token.T_RPAREN;

public class FunctionCallNode implements Node {

    private final Position posBegin;
    private final Position posEnd;
    private final Node calledNode;
    private final List<Node> argNodes;

    public FunctionCallNode(Node calledNode, List<Node> argNodes) {
        this.calledNode = calledNode;
        this.argNodes = new ArrayList<>(argNodes);
        this.posBegin = calledNode.getPosBegin();

        if (argNodes.size() > 0) {
            this.posEnd = argNodes.get(argNodes.size() - 1).getPosEnd();
        } else {
            this.posEnd = calledNode.getPosEnd();
        }
    }

    public List<Node> getArgNodes() { return argNodes; }
    public Node getCalledNode() { return calledNode; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "nodes.FunctionCallNode"; }

    public String toString() {

        return "nodes.FunctionCallNode( " +
                getCalledNode().toString().subSequence(7, getCalledNode().toString().length() - 1) +
                T_RPAREN +
                getArgNodes().toString() +
                " )";
    }
}
