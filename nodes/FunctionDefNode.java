package nodes;

import interpreting.datatypes.NumLiteral;
import lexing.Position;
import lexing.Token;

import java.util.ArrayList;
import java.util.List;

import static lexing.Token.*;

public class FunctionDefNode implements Node {

    private final Position posBegin;
    private final Position posEnd;
    private final Node bodyNode;
    private final Token variableNameToken;
    private final List<Token> argNameTokens;
    private final NumLiteral shouldReturnNull;

    public FunctionDefNode(Token variableNameToken, List<Token> argNameTokens, Node bodyNode, NumLiteral bool) {
        this.variableNameToken = variableNameToken;
        this.argNameTokens = new ArrayList<>(argNameTokens);
        this.bodyNode = bodyNode;
        this.shouldReturnNull = bool;

        if (variableNameToken != null) {
            this.posBegin = variableNameToken.getPosBegin();
        } else if (argNameTokens.size() > 0) {
            this.posBegin = argNameTokens.get(0).getPosBegin();
        } else {
            this.posBegin = bodyNode.getPosBegin();
        }

        this.posEnd = bodyNode.getPosEnd();
    }

    public Node getBodyNode() { return bodyNode; }
    public List<Token> getArgNameTokens() { return argNameTokens; }
    public Token getVariableNameToken() { return variableNameToken; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() { return "nodes.FunctionDefNode"; }

    public String toString() {
        StringBuilder builtString = new StringBuilder();

        builtString.append("FunctionDecl( ")
                .append("function ")
                .append(getVariableNameToken().toString())
                .append("(");
        for (Token token : getArgNameTokens()) {
            builtString.append(token.toString()).append(T_COMMA).append(T_WHITESPACE);
        }
        if (getArgNameTokens().size() > 0) {
            builtString.replace(builtString.length() - 2, builtString.length(), T_EMPTY_STRING);
        }
        builtString.append(")")
                .append(T_WHITESPACE)
                .append(T_ARROW)
                .append(T_WHITESPACE).append("{").append("\n\t")
                .append(getBodyNode().toString())
                .append("\n}");

        return builtString.toString();
    }
}
