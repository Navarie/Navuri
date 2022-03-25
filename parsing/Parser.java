package parsing;

import interpreting.datatypes.IntegerNode;
import interpreting.datatypes.NumLiteral;
import lexing.Position;
import lexing.Token;
import nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static errors.CustomException.FileParsingException;
import static errors.CustomException.InvalidSyntaxError;
import static errors.ErrorCode.FILE_PARSING;
import static errors.ErrorCode.INVALID_SYNTAX;
import static lexing.Token.*;

public class Parser {

    private final List<Token> tokens;
    private Token currentToken;
    private int tokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.tokenIndex = -1;
        advance();
    }

    public void advance() {
        tokenIndex++;
        updateCurrentToken();
    }

    public void reverse(int amount) {
        tokenIndex = tokenIndex - amount;
        updateCurrentToken();
    }

    public void updateCurrentToken() {
        if (tokenIndex >= 0 && tokenIndex < tokens.size()) {
            currentToken = tokens.get(tokenIndex);
        }
    }

    public ParseResult parse() throws InvalidSyntaxError, FileParsingException {
        boolean tokenNullCheck = (currentToken != null);
        if (!tokenNullCheck) {
            throw new FileParsingException("Error parsing the file.", FILE_PARSING);
        }

        return statements();
    }

    public ParseResult statements() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        List<Node> statements = new ArrayList<>();
        Position posBegin = getCurrentToken().getPosBegin().copyPosition();
        while (getCurrentToken().getValue().equals(T_SEMICOLON) || getCurrentToken().getValue().equals(T_LINE_BREAK)) {
            recordAndAdvance(result);
        }
        Node statement = result.record(statement());
        statements.add(statement);

        boolean remainingStatements = true;
        while (true) {
            int newlineCount = 0;
            while (getCurrentToken().getValue().equals(T_SEMICOLON) || getCurrentToken().getValue().equals(T_LINE_BREAK)) {
                recordAndAdvance(result);
                newlineCount++;
            }

            if (newlineCount == 0) remainingStatements = false;
            if (!remainingStatements) {
                break;
            }

            statement = result.record(statement());
            if (statement == null) {
                reverse(result.getReverseCount());
                remainingStatements = false;
                continue;
            }
            statements.add(statement);
        }
        return result.success(new ListNode(statements, posBegin, getCurrentToken().getPosEnd().copyPosition()));
    }

    private void recordAndAdvance(ParseResult result) {
        result.recordAdvancement();
        advance();
    }

    public ParseResult statement() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Position posBegin = getCurrentToken().getPosBegin().copyPosition();

        if (getCurrentToken().getValue().equals(KEYWORDS[22])) {
            recordAndAdvance(result);
            Node expression = result.record(expression());

            return result.success(new ReturnNode(expression, posBegin, null));
        }
        Node expression = result.record(expression());
        return result.success(expression);
    }

    public ParseResult expression() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        boolean tokenNullCheck = (getCurrentToken() != null);
        if (tokenNullCheck && getCurrentToken().compareTo(T_KEYWORD, KEYWORDS[0])) {
            recordAndAdvance(result);

            if (!getCurrentToken().getType().equals(T_ID)) throwInvalidSyntax("identifier");
            Token variableName = getCurrentToken();
            recordAndAdvance(result);

            if (!getCurrentToken().getValue().equals(T_EQUALS)) throwInvalidSyntax(T_EQUALS);
            recordAndAdvance(result);
            Node expression = result.record(expression());

            return result.success(new VarAssignmentNode(variableName, expression));
        }
        Node node = result.record(binaryOperation(4, KEYWORDS, -1));

        return result.success(node);
    }

    public ParseResult comparisonExpression() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        boolean tokenNullCheck = (getCurrentToken() != null);
        if (tokenNullCheck && getCurrentToken().compareTo(T_KEYWORD, KEYWORDS[3])) {
            recordAndAdvance(result);
            Node comparisonExpression = result.record(comparisonExpression());

            return result.success(new UnaryOpNode(getCurrentToken(), comparisonExpression));
        }
        Node node = result.record(binaryOperation(5, COMPARISON_OPERATORS, -1));

        return result.success(node);
    }

    public ParseResult arithmeticExpression() throws InvalidSyntaxError {
        return binaryOperation(2, LOW_PRIORITY_OPS, -1);
    }

    private Token getCurrentToken() {
        return this.currentToken;
    }

    public ParseResult term() throws InvalidSyntaxError {
        return binaryOperation(1, MEDIUM_PRIORITY_OPS, -1);
    }

    public ParseResult factor() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Token token = this.currentToken;
        boolean tokenNullCheck = (this.currentToken != null);
        if (tokenNullCheck) {
            switch (token.getValue()) {
                case T_PLUS, T_MINUS, T_LOGARITHM, T_SQUARE_ROOT, T_ABSOLUTE -> {
                    recordAndAdvance(result);
                    Node factor = result.record(factor());
                    UnaryOpNode unaryOpNode = new UnaryOpNode(token, factor);
                    return result.success(unaryOpNode);
                }
            }
        }
        return power();
    }

    public ParseResult power() throws InvalidSyntaxError {
        return binaryOperation(6, HIGH_PRIORITY_OPS, 3);
    }

    public ParseResult atom() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Token token = this.currentToken;
        boolean tokenNullCheck = (token != null);
        if (tokenNullCheck) {

            switch (token.getValue()) {
                case T_LPAREN -> {
                    Node expression = result.record(expression());
                    return result.success(expression);
                }
                case T_RPAREN -> recordAndAdvance(result);
                case T_LSQ_BRACKET -> {
                    Node listExpression = result.record(listExpression());
                    return result.success(listExpression);
                }
            }
            switch (token.getType()) {
                case T_INT -> {
                    recordAndAdvance(result);
                    IntegerNode integerNode = new IntegerNode(token);
                    return result.success(integerNode);
                }
                case T_FLOAT -> {
                    recordAndAdvance(result);
                    FloatNode floatNode = new FloatNode(token);
                    return result.success(floatNode);
                }
                case T_STRING -> {
                    recordAndAdvance(result);
                    StringNode stringNode = new StringNode(token);
                    return result.success(stringNode);
                }
                case T_ID -> {
                    recordAndAdvance(result);
                    return result.success(new VarRetrievalNode(token));
                }
                case T_BOOLEAN -> {
                    recordAndAdvance(result);
                    BooleanNode booleanNode = new BooleanNode(token);
                    return result.success(booleanNode);
                }
            }
            if (token.compareTo(T_KEYWORD, "if")) {
                Node ifExpression = result.record(ifExpression());
                return result.success(ifExpression);
            } else if (token.getValue().equals("for")) {
                Node forExpression = result.record(forLoop());
                return result.success(forExpression);
            } else if (token.compareTo(T_KEYWORD, "while")) {
                Node whileExpression = result.record(whileLoop());
                return result.success(whileExpression);
            } else if (token.compareTo(T_KEYWORD, "function")) {
                Node functionExpression = result.record(functionExpression());
                return result.success(functionExpression);
            } else if (token.compareTo(T_KEYWORD, "class")) {
                Node classConstructor = result.record(classConstruction());
                return result.success(classConstructor);
            }
        }
        return result;
    }

    private ParseResult classConstruction() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Position posBegin = getCurrentToken().getPosBegin().copyPosition();

        if (!getCurrentToken().compareTo(T_KEYWORD, "class")) throwInvalidSyntax("class");
        recordAndAdvance(result);

        if (!getCurrentToken().getType().equals(T_ID)) throwInvalidSyntax("identifier");
        Token classNameToken = this.currentToken;
        recordAndAdvance(result);

        if (!getCurrentToken().getValue().equals(T_L_BRACKET)) throwInvalidSyntax(T_L_BRACKET);
        recordAndAdvance(result);

        if (!getCurrentToken().getType().equals(T_NEWLINE)) throwInvalidSyntax("newline");
        recordAndAdvance(result);
        Node body = result.record(statements());

        if (!getCurrentToken().getValue().equals(T_R_BRACKET)) throwInvalidSyntax(T_R_BRACKET);
        recordAndAdvance(result);

        return result.success(new ClassNode(posBegin, getCurrentToken().getPosEnd(), classNameToken, body));
    }

    private void throwInvalidSyntax(String expected) throws InvalidSyntaxError {
        throw new InvalidSyntaxError(getCurrentToken().getPosBegin(),
                "\nInvalid syntax! Expected " + expected + ", got: " + getCurrentToken().getValue(), INVALID_SYNTAX);
    }

    private ParseResult functionExpression() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();

        if (!getCurrentToken().compareTo(T_KEYWORD, "function")) throwInvalidSyntax("keyword function");
        recordAndAdvance(result);

        Token variableNameToken = null;
        if (getCurrentToken().getType().equals(T_ID)) {
            variableNameToken = getCurrentToken();
        }
        recordAndAdvance(result);

        if (!getCurrentToken().getValue().equals(T_LPAREN)) throwInvalidSyntax(T_LPAREN);
        recordAndAdvance(result);
        List<Token> argNameTokens = new ArrayList<>();

        if (getCurrentToken().getType().equals(T_ID)) {
            argNameTokens.add(getCurrentToken());
            recordAndAdvance(result);

            while (getCurrentToken().getValue().equals(T_COMMA)) {
                recordAndAdvance(result);

                if (!getCurrentToken().getType().equals(T_ID)) throwInvalidSyntax("identifier");
                argNameTokens.add(getCurrentToken());
                recordAndAdvance(result);
            }
        }
        if (!getCurrentToken().getValue().equals(T_RPAREN)) throwInvalidSyntax(T_RPAREN);
        recordAndAdvance(result);

        if (getCurrentToken().getValue().equals(T_ARROW)) {
            recordAndAdvance(result);
            Node returnedNode = result.record(statement());

            return result.success(new FunctionDefNode(variableNameToken, argNameTokens, returnedNode, new NumLiteral(0)));
        }

        if (!getCurrentToken().getType().equals(T_NEWLINE)) throwInvalidSyntax("newline");
        recordAndAdvance(result);
        Node returnedNode = result.record(statements());

        if (!getCurrentToken().compareTo(T_KEYWORD, "end")) throwInvalidSyntax("keyword end");
        recordAndAdvance(result);

        return result.success(new FunctionDefNode(variableNameToken, argNameTokens, returnedNode, new NumLiteral(1)));
    }

    private ParseResult functionCall() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Node atom = result.record(atom());

        if (getCurrentToken().getValue().equals(T_LPAREN)) {
            recordAndAdvance(result);
            List<Node> argNodes = new ArrayList<>();

            if (!getCurrentToken().getValue().equals(T_RPAREN)) {
                argNodes.add(result.record(comparisonExpression()));
                while (getCurrentToken().getValue().equals(T_COMMA)) {
                    recordAndAdvance(result);
                    argNodes.add(result.record(comparisonExpression()));
                }
            }
            recordAndAdvance(result);
            return result.success(new FunctionCallNode(atom, argNodes));
        }
        return result.success(atom);
    }

    private ParseResult listExpression() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        List<Node> elements = new ArrayList<>();
        Position posBegin = getCurrentToken().getPosBegin().copyPosition();

        if (!getCurrentToken().getValue().equals(T_LSQ_BRACKET)) throwInvalidSyntax(T_LSQ_BRACKET);
        recordAndAdvance(result);

        if (getCurrentToken().getValue().equals(T_RSQ_BRACKET)) {
            recordAndAdvance(result);
        } else {
            elements.add(result.record(expression()));
            while (getCurrentToken().getValue().equals(T_COMMA)) {
                recordAndAdvance(result);
                elements.add(result.record(expression()));
            }

            if (!getCurrentToken().getValue().equals(T_RSQ_BRACKET)) throwInvalidSyntax(T_RSQ_BRACKET);
            recordAndAdvance(result);
        }
        return result.success(new ListNode(elements, posBegin, getCurrentToken().getPosEnd().copyPosition()));
    }

    private ParseResult ifExpression() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        List<Node> conditions = new ArrayList<>();
        List<Node> expressions = new ArrayList<>();
        Node optElseCase = null;

        if (!getCurrentToken().compareTo(T_KEYWORD, "if")) throwInvalidSyntax("keyword if");
        recordAndAdvance(result);
        conditions.add(result.record(comparisonExpression()));

        if (!getCurrentToken().compareTo(T_KEYWORD, "then")) throwInvalidSyntax("keyword then");
        recordAndAdvance(result);
        retrieveExpressions(result, expressions);

        while (getCurrentToken().compareTo(T_KEYWORD, "elif")) {
            recordAndAdvance(result);
            conditions.add(result.record(comparisonExpression()));

            if (!getCurrentToken().compareTo(T_KEYWORD, "then")) throwInvalidSyntax("keyword then");
            retrieveExpressions(result, expressions);
        }

        if (getCurrentToken().getValue().equals("else")) {
            recordAndAdvance(result);
            if (getCurrentToken().getValue().equals(T_L_BRACKET)) {
                recordAndAdvance(result);
                optElseCase = result.record(statements());

                if (!getCurrentToken().getValue().equals(T_R_BRACKET)) throwInvalidSyntax(T_R_BRACKET);
                recordAndAdvance(result);
            } else {
                recordAndAdvance(result);
                optElseCase = result.record(statement());
            }
        }

        return result.success(new IfStatementNode(conditions, expressions, optElseCase));
    }

    private void retrieveExpressions(ParseResult result, List<Node> expressions) throws InvalidSyntaxError {
        if (getCurrentToken().getValue().equals(T_L_BRACKET)) {
            recordAndAdvance(result);
            expressions.add(result.record(statements()));

            if (!getCurrentToken().getValue().equals(T_R_BRACKET)) throwInvalidSyntax(T_R_BRACKET);
            recordAndAdvance(result);
        } else {
            recordAndAdvance(result);
            expressions.add(result.record(statement()));
        }
    }

    private ParseResult forLoop() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();

        if (!getCurrentToken().compareTo(T_KEYWORD, "for")) throwInvalidSyntax("keyword for");
        recordAndAdvance(result);

        if (!getCurrentToken().getType().equals(T_ID)) throwInvalidSyntax("identifier");
        Token variableName = getCurrentToken();
        recordAndAdvance(result);

        if (!getCurrentToken().getType().equals(T_EQUALS)) throwInvalidSyntax(T_EQUALS);
        recordAndAdvance(result);
        // TODO: could be expression
        Node startValue = result.record(comparisonExpression());

        if (!getCurrentToken().compareTo(T_KEYWORD, "until")) throwInvalidSyntax("keyword until");
        recordAndAdvance(result);
        Node endValue = result.record(comparisonExpression());

        Node stepValue = null;
        if (getCurrentToken().compareTo(T_KEYWORD, "step")) {
            recordAndAdvance(result);
            stepValue = result.record(expression());
        }

        if (getCurrentToken().getValue().equals(T_L_BRACKET)) {
            recordAndAdvance(result);
            Node body = result.record(statements());

            if (!getCurrentToken().getValue().equals(T_R_BRACKET)) throwInvalidSyntax(T_R_BRACKET);
            recordAndAdvance(result);

            return result.success(new ForLoopNode(variableName, startValue, endValue, stepValue, body, true));
        }
        Node body = result.record(statement());
        return result.success(new ForLoopNode(variableName, startValue, endValue, stepValue, body, false));
    }

    private ParseResult whileLoop() throws InvalidSyntaxError {
        ParseResult result = new ParseResult();

        if (!getCurrentToken().compareTo(T_KEYWORD, "while")) throwInvalidSyntax("keyword while");

        recordAndAdvance(result);
        Node condition = result.record(comparisonExpression());

        if (getCurrentToken().getValue().equals(T_L_BRACKET)) {
            recordAndAdvance(result);
            Node body;
            try {
                body = result.record(statements());
            } catch (NullPointerException e) {
                throw new InvalidSyntaxError(currentToken.getPosBegin(), "Expected while-body.\n\tGot: "
                        + getCurrentToken().getValue(), INVALID_SYNTAX);
            }

            if (!getCurrentToken().getValue().equals(T_R_BRACKET)) throwInvalidSyntax(T_R_BRACKET);
            recordAndAdvance(result);

            return result.success(new WhileLoopNode(condition, body, true));
        }

        Node body = result.record(statement());
        return result.success(new WhileLoopNode(condition, body, false));
    }

    public ParseResult binaryOperation(int code, String[] operators, int secondCode) throws InvalidSyntaxError {
        ParseResult result = new ParseResult();
        Node left = null;
        Node right = null;

        switch (code) {
            case 1 -> left = result.record(factor());
            case 2 -> left = result.record(term());
            case 3 -> left = result.record(atom());
            case 4 -> left = result.record(comparisonExpression());
            case 5 -> left = result.record(arithmeticExpression());
            case 6 -> left = result.record(functionCall());
        }


        boolean tokenNullCheck = (this.currentToken != null);
        while (tokenNullCheck && Arrays.asList(operators).contains(getCurrentToken().getValue())) {
            Token operatorToken = this.currentToken;

            recordAndAdvance(result);

            if (!operatorToken.getValue().equals(T_RPAREN)) {
                if (secondCode == 3) {
                    right = result.record(factor());
                } else if (secondCode == -1) {
                    switch (code) {
                        case 1 -> right = result.record(factor());
                        case 2 -> right = result.record(term());
                        case 4 -> right = result.record(comparisonExpression());
                        case 5 -> right = result.record(arithmeticExpression());
                    }
                }
            }
            left = new BinaryOpNode(left, operatorToken, right);
        }
        return result.success(left);
    }
}
