package lexing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static errors.CustomException.CustomRuntimeError;
import static errors.CustomException.IllegalTokenError;
import static errors.ErrorCode.*;
import static lexing.Token.*;

public class Lexer {

    private final String text;
    private final List<Token> tokens;
    private final Position pos;

    private Token currentToken;
    private int tokenSize;

    public Lexer(String fileName, String text) {
        tokens = new ArrayList<>();
        this.text = text;
        this.pos = new Position(-1, 0, -1, fileName, text);
        this.pos.advancePosition(null);
    }

    public Token getCurrentToken() {
        return currentToken;
    }
    public String getText() {
        return text;
    }

    public void adjustPosition(int index) {
        this.currentToken = new Token(null, Character.toString(this.text.charAt(index)), this.pos, null);
    }

    public void advance() {
        this.pos.advancePosition(this.currentToken);
        if (this.pos.getIndex() < this.text.length()) {
            this.currentToken = new Token(
                    null, Character.toString(this.text.charAt(this.pos.getIndex())), this.pos, null);
        } else {
            this.currentToken = null;
        }
    }

    public void createTokens() throws CustomRuntimeError, IllegalTokenError {
        int adjustedTokenSize = 0;
        while (adjustedTokenSize < getText().length()) {

            this.currentToken = new Token(null, Character.toString(getText().charAt(adjustedTokenSize)), this.pos, null);
            switch (this.currentToken.getValue()) {
                case T_WHITESPACE, T_INDENT, "\n" -> {
                    setTokenSize(0);
                    advance();
                }
                case T_COMMENT -> createComment(adjustedTokenSize);
                case T_SEMICOLON-> addTokenAndAdvance(T_NEWLINE, "\r\n", this.pos);
                case T_L_BRACKET, T_R_BRACKET -> addTokenAndAdvance(T_SPECIAL_OP, this.currentToken.getValue(), this.pos);
                case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> tokens.add(createNumber(adjustedTokenSize));
                case "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
                        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                        "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                        T_UNDERSCORE -> createIdentifier(adjustedTokenSize);
                case "+" -> addTokenAndAdvance(T_BIN_OP, T_PLUS, this.pos);
                case "-" -> addTokenAndAdvance(T_BIN_OP, T_MINUS, this.pos);
                case "*" -> addTokenAndAdvance(T_BIN_OP, T_MULTI, this.pos);
                case "/" -> addTokenAndAdvance(T_BIN_OP, T_DIV, this.pos);
                case "%" -> addTokenAndAdvance(T_BIN_OP, T_MODULO, this.pos);
                case "^" -> addTokenAndAdvance(T_BIN_OP, T_POWER, this.pos);
                case "(" -> addTokenAndAdvance(T_SPECIAL_OP, T_LPAREN, this.pos);
                case ")" -> addTokenAndAdvance(T_SPECIAL_OP, T_RPAREN, this.pos);
                case "!", "<", ">", "=" -> determineTrailingEqualsToken();
                case "," -> addTokenAndAdvance(T_SPECIAL_OP, T_COMMA, this.pos);
                case "[" -> addTokenAndAdvance(T_SPECIAL_OP, T_LSQ_BRACKET, this.pos);
                case "]" -> addTokenAndAdvance(T_SPECIAL_OP, T_RSQ_BRACKET, this.pos);
                case "\"" -> createString(adjustedTokenSize);
                // TODO: remaining cases!
                default -> throw new IllegalTokenError(this.pos,
                        "\nUnrecognised token: " + getCurrentToken().getValue(), ILLEGAL_TOKEN);
            }
            adjustedTokenSize += 1 + getTokenSize();
        }
    }

    private void addTokenAndAdvance(String type, String value, Position begin) {
        tokens.add(new Token(type, value, begin, null));
        setTokenSize(0);
        advance();
    }

    @SuppressWarnings("ConstantConditions")
    private void createComment(int index) {
        this.pos.copyPosition();
        StringBuilder builtString = new StringBuilder();
        adjustPosition(index);
        advance();

        boolean tokenNotNull = (this.currentToken != null);
        if (tokenNotNull && this.currentToken.getValue().equals(T_COMMENT)) {
            builtString.append(getCurrentToken().getValue());
            advance();

            if (tokenNotNull && this.currentToken.getValue().equals(T_COMMENT)) {
                builtString.append(getCurrentToken().getValue());
                advance();

                while (tokenNotNull && !this.currentToken.getValue().equals(T_SEPARATOR)) {
                    builtString.append(getCurrentToken().getValue());
                    advance();
                }
                builtString.append(T_SEPARATOR);
            }
        }
        setTokenSize(builtString.length());
        advance();
    }

    private void createString(int index) {
        Position posBegin = this.pos.copyPosition();
        StringBuilder builtString = new StringBuilder();
        builtString.append(T_QUOTE);
        boolean escapingCharacter = false;
        adjustPosition(index);
        advance();

        setTokenSize(1);
        while (getCurrentToken() != null && (!getCurrentToken().getValue().equals(T_QUOTE)
                                      || escapingCharacter)) {
            if (escapingCharacter) {
                builtString.append(H_ESCAPED_CHARS.get(getCurrentToken().getValue()));
                escapingCharacter = false;
            } else {
                if (getCurrentToken().getValue().equals(T_BACKSLASH)) {
                    escapingCharacter = true;
                } else {
                    builtString.append(getCurrentToken().getValue());
                }
            }
            advance();
        }
        builtString.append(T_QUOTE);
        addToken(T_STRING, builtString.toString(), posBegin, this.pos);
        setTokenSize(builtString.length() - 1);
        advance();
    }

    private void determineTrailingEqualsToken() {
        Position posBegin = this.pos.copyPosition();
        StringBuilder builtString = new StringBuilder();
        Token precedingToken = this.currentToken;
        builtString.append(precedingToken.getValue());
        advance();

        setTokenSize(1);
        if (getCurrentToken() != null && getCurrentToken().getValue().equals(T_EQUALS)) {
            advance();
            builtString.append("=");
            addToken(T_COMP_OP, builtString.toString(), posBegin, this.pos);
            setTokenSize(builtString.length());
        } else if (getCurrentToken() != null && getCurrentToken().getValue().equals(T_GREATER_THAN)) {
            advance();
            builtString.append(">");
            addToken(T_SPECIAL_OP, builtString.toString(), posBegin, this.pos);
            setTokenSize(builtString.length());
        }
        if (builtString.length() == 1) {
            switch (precedingToken.getValue()) {
                case T_EQUALS -> addToken(T_EQUALS, T_EQUALS, posBegin, this.pos);
                case T_LESS_THAN -> addToken(T_COMP_OP, COMPARISON_OPERATORS[2], posBegin, this.pos);
                case T_GREATER_THAN -> addToken(T_COMP_OP, COMPARISON_OPERATORS[3], posBegin, this.pos);
            }
        }
        advance();
    }

    private void addToken(String type, String value, Position begin, Position end) {
        tokens.add(new Token(type, value, begin, end));
    }

    private Token createNumber(int index) throws CustomRuntimeError {
        Position posBegin = this.pos.copyPosition();
        StringBuilder builtString = new StringBuilder();
        String tokenType = T_INT;
        int separatorCount = 0;
        adjustPosition(index);

        setTokenSize(1);
        while (this.currentToken != null && (T_SEPARATOR + DIGITS).contains(this.currentToken.getValue())) {
            switch (this.currentToken.getValue()) {
                case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> builtString.append(this.currentToken.getValue());
                case "." -> {
                    tokenType = T_FLOAT;
                    if (separatorCount == 0) separatorCount++;
                    builtString.append(T_SEPARATOR);
                }
            }
            advance();
        }

        if (separatorCount > 1) {
            throw new CustomRuntimeError(posBegin,
                    "Too many separators! Check numbers formats.", FLOAT_FORMATTING);
        }

        setTokenSize(builtString.length() - 1);

        return new Token(tokenType, builtString.toString(), posBegin, this.pos);
    }

    private void createIdentifier(int index) throws IllegalTokenError {
        Position posBegin = this.pos.copyPosition();
        StringBuilder builtString = new StringBuilder();
        adjustPosition(index);
        String tokenType = T_ID;

        if (getCurrentToken().getValue().equals(T_UNDERSCORE)) {
            throw new IllegalTokenError(posBegin,
                    "\nUnderscore may not be placed in beginning of identifier.", ILLEGAL_NAME);
        }
        while (getCurrentToken() != null && (LETTERS + letters).contains(this.currentToken.getValue())) {
            builtString.append(this.currentToken.getValue());
            advance();
        }
        for (String keyword : KEYWORDS) {
            if (Objects.equals(keyword, builtString.toString())) {
                tokenType = T_KEYWORD;
                if (keyword.equals("true") || keyword.equals("false")) {
                    tokenType = T_BOOLEAN;
                    addToken(tokenType, T_WHITESPACE + builtString, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                } else if (keyword.equals(KEYWORDS[17])) {
                    tokenType = T_UNARY_OP;
                    addToken(tokenType, T_LOGARITHM, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                } else if (keyword.equals(KEYWORDS[18])) {
                    tokenType = T_UNARY_OP;
                    addToken(tokenType, T_SQUARE_ROOT, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                } else if (keyword.equals(KEYWORDS[19])) {
                    tokenType = T_BIN_OP;
                    addToken(tokenType, T_MINIMUM, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                } else if (keyword.equals(KEYWORDS[20])) {
                    tokenType = T_BIN_OP;
                    addToken(tokenType, T_MAXIMUM, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                } else if (keyword.equals(KEYWORDS[21])) {
                    tokenType = T_UNARY_OP;
                    addToken(tokenType, T_ABSOLUTE, posBegin, this.pos);
                    setTokenSize(builtString.length() - 1);
                    return;
                }
            }
        }
        addToken(tokenType, builtString.toString(), posBegin, this.pos);
        setTokenSize(builtString.length() - 1);
    }

    public List<Token> getTokens() { return tokens; }
    private int getTokenSize() { return tokenSize; }
    private void setTokenSize(int tokenSize) { this.tokenSize = tokenSize; }
}
