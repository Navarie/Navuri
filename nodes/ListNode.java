package nodes;

import lexing.Position;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class ListNode implements Node {

    private final List<Node> elements;
    private final Position posBegin;
    private final Position posEnd;

    public ListNode(List<Node> elements, Position posBegin, Position posEnd) {
        this.elements = elements;
        this.posBegin = posBegin;
        this.posEnd = posEnd;
    }

    public List<Node> getElements() {
        return elements;
    }
    public String getTypeString() {
        return "ListNode";
    }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public String toString() {
        return getElements().toString();
    }
}
