package nodes;

import lexing.Position;

import java.util.List;

public class IfStatementNode implements Node {

    private final List<Node> conditions;
    private final List<Node> expressions;
    private final Node optElseCase;
    private final Position posBegin;
    private final Position posEnd;

    public IfStatementNode(List<Node> conditions, List<Node> expressions, Node optElseCase) {
        this.conditions = conditions;
        this.expressions = expressions;
        this.optElseCase = optElseCase;
        if (expressions.size() > 0) {
            this.posBegin = expressions.get(0).getPosBegin();
        } else {
            this.posBegin = null;
        }
        if (optElseCase != null) {
            this.posEnd = optElseCase.getPosEnd();
        } else if (expressions.size() > 0) {
            this.posEnd = expressions.get(0).getPosEnd();
        } else {
            this.posEnd = null;
        }

    }

    public List<Node> getConditions() { return conditions; }
    public List<Node> getExpressions() { return expressions; }
    public Node getOptElseCase() { return optElseCase; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }
    public String getTypeString() {
        return "IfStatement";
    }

    public String toString() {
        StringBuilder builtString = new StringBuilder();
        builtString.append("IfStatement(");
        if (conditions.size() > 0 && expressions.size() > 0) {
            builtString.append("if(")
                    .append(conditions.get(0).toString())
                    .append(") then {\n\t")
                    .append(expressions.get(0).toString())
                    .append("\n} ");
        }

        for (int i=1; i < conditions.size(); i++) {
            builtString.append("elif(")
                    .append(conditions.get(0).toString())
                    .append(") then {\n\t")
                    .append(expressions.get(0).toString())
                    .append("\n}");
        }

        if (optElseCase != null) {
            builtString.append("else {\n\t")
                    .append(optElseCase)
                    .append("\n}");
        }

        return builtString.toString();
    }
}
