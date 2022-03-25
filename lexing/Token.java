package lexing;

import java.util.Map;

public class Token {

    // Special tokens
    public static final String T_EMPTY_STRING = "";
    public static final String T_WHITESPACE = " ";
    public static final String T_LINE_BREAK = System.lineSeparator();
    public static final String T_INDENT = "\t";
    public static final String T_KEYWORD = "Keyword";

    static final Map<String, String> H_ESCAPED_CHARS;
    static {
        H_ESCAPED_CHARS = Map.of(
                "n", "\n",
                "t", "\t" );
    }

    // Primitive symbols and keywords
    public static final String DIGITS = "0123456789";
    public static final String letters = "abcdefghijklmnopqrstuvwxyz";
    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String[] KEYWORDS = {
            "var",
            "&&",
            "||",
            "!",
            "if",
            "then",
            "elif",
            "else",
            "for",
            "until",
            "step",
            "while",
            "function",
            "end",
            "class",
            "true",
            "false",
            "log",
            "sqrt",
            "min",
            "max",
            "abs",
            "return"
    };
    public static final String[] COMPARISON_OPERATORS = {
            "==",
            "!=",
            "<",
            ">",
            "<=",
            ">="
    };

    // Types
    public static final String T_INT = "Int";
    public static final String T_FLOAT = "Float";
    public static final String T_STRING = "String";
    public static final String T_BOOLEAN = "Boolean";
    public static final String T_BIN_OP = "BinOp";
    public static final String T_UNARY_OP = "UnaryOp";
    public static final String T_SPECIAL_OP = "S";
    public static final String T_NEWLINE = "Newline";
    public static final String T_ID = "Identifier";
    public static final String T_COMP_OP = "CompOp";
    public static final String T_LOGICAL_OP = "LogicOp";

    // Operators
    public static final String T_PLUS = "PlusOp";
    public static final String T_MINUS = "MinusOp";
    public static final String T_MULTI = "MultiOp";
    public static final String T_DIV = "DivOp";
    public static final String T_POWER = "PowerOp";
    public static final String T_MODULO = "ModuloOp";
    public static final String T_LOGARITHM = "LOG2";
    public static final String T_SQUARE_ROOT = "SQRT";
    public static final String T_MINIMUM = "MinOp";
    public static final String T_MAXIMUM = "MaxOp";
    public static final String T_ABSOLUTE = "AbsOp";
    public static final String[] LOW_PRIORITY_OPS = {
            T_PLUS,
            T_MINUS,
            T_MAXIMUM,
            T_MINIMUM
    };
    public static final String[] MEDIUM_PRIORITY_OPS = {
            T_MULTI,
            T_DIV,
            T_MODULO
    };
    public static final String[] HIGH_PRIORITY_OPS = {
            T_POWER
    };

    // Comparison and logical operators
    public static final String T_D_EQUALS = "==";
    public static final String T_NOT_EQUALS = "!=";
    public static final String T_LESS_THAN = "<";
    public static final String T_GREATER_THAN = ">";
    public static final String T_LTE = "<=";
    public static final String T_GTE = ">=";

    // Symbols
    public static final String T_SEPARATOR = ".";
    public static final String T_EQUALS = "=";
    public static final String T_LPAREN = "(";
    public static final String T_RPAREN = ")";
    public static final String T_UNDERSCORE = "_";
    public static final String T_COMMA = ",";
    public static final String T_ARROW = "=>";
    public static final String T_QUOTE = "\"";
    public static final String T_BACKSLASH = "\\";
    public static final String T_LSQ_BRACKET = "[";
    public static final String T_RSQ_BRACKET = "]";
    public static final String T_L_BRACKET = "{";
    public static final String T_R_BRACKET = "}";
    public static final String T_SEMICOLON = ";";
    public static final String T_COMMENT = "#";

    private final String type;
    private final String value;
    private Position posBegin = null;
    private Position posEnd = null;

    public Token(String type, String value, Position posBegin, Position posEnd) {
        this.type = type;
        this.value = value;
        if (posBegin != null) {
            this.posBegin = posBegin.copyPosition();
            this.posEnd = posBegin.copyPosition();
            this.posEnd.advancePosition(this);
        }
        if (posEnd != null) {
            this.posEnd = posEnd;
        }
    }

    public String getType() { return type; }
    public String getValue() { return value; }
    public Position getPosBegin() { return posBegin; }
    public Position getPosEnd() { return posEnd; }

    public boolean compareTo(String type, String value) {
        return this.type.equals(type) && this.value.equals(value);
    }

    public String toString() {
        if (getType() != null) {
            switch (getType()) {
                case T_ID, T_KEYWORD, T_NEWLINE, T_SPECIAL_OP, T_BIN_OP, T_UNARY_OP -> {
                    return getValue();
                }
            }
        }
        if (getValue() != null) {
            switch (getValue()) {
                case T_LPAREN -> { return T_LPAREN; }
                case T_RPAREN -> { return T_RPAREN; }
                case T_EQUALS -> { return T_EQUALS; }
                case T_COMMA -> { return "|,|"; }
                case " true", " false" -> { return getType() + ":" + getValue(); }
                default -> {
                    return "" + getType() + ": " + getValue();
                }
            }
        }
        return T_EMPTY_STRING;
    }
}
