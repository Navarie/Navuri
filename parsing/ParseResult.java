package parsing;

import nodes.Node;

public class ParseResult {

    private Node node;
    private int advanceCount;
    private final int reverseCount;

    public ParseResult() {
        this.node = null;
        this.advanceCount = 0;
        this.reverseCount = 0;
    }

    public <T> T getNode() {
        //noinspection unchecked
        return (T) node;
    }

    public Node record(ParseResult result) {
        advanceCount += result.getAdvanceCount();
        return result.getNode();

    }

    private int getAdvanceCount() {
        return advanceCount;
    }
    int getReverseCount() { return reverseCount; }

    public void recordAdvancement() {
        advanceCount++;
    }

    public ParseResult success(Node node) {
        this.node = node;
        return this;
    }
}
