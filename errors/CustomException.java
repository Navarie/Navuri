package errors;

import lexing.Position;

import static lexing.Token.T_LINE_BREAK;

public class CustomException extends Exception {

    private final Position posBegin;
    private final String details;
    private final int code;

    @SuppressWarnings("unused")
    public CustomException(Position posBegin, Position posEnd, String details, Throwable cause, int code) {
        this.posBegin = posBegin;
        this.details = details;
        this.code = code;
    }

    public CustomException(Position posBegin, String details, int code) {
        this.posBegin = posBegin;
        this.details = details;
        this.code = code;
    }
    public CustomException(String details, int code) {
        this.posBegin = null;
        this.details = details;
        this.code = code;
    }

    public String toString() {
        String fileName = "";
        int lineNumber = 0;
        int columnNumber = 0;
        if (posBegin != null) {
            fileName = posBegin.getFileName();
            lineNumber = posBegin.getLine() + 1;
            columnNumber = posBegin.getColumn();

            return details + "\nFile: " + fileName + ", line " + lineNumber +
                    " column " + columnNumber
                    + "\nCode: " + code;
        } else {
            return details + T_LINE_BREAK + "File: <System.in>" + T_LINE_BREAK + "Code: " + code;        }
    }

    public static class IllegalTokenError extends CustomException {
        public IllegalTokenError(Position posBegin, String details, int code) {
            super(posBegin, details, code);
        }
    }

    public static class VariableNotInitialised extends CustomException {
        public VariableNotInitialised(Position posBegin, String details, int code) {
            super(posBegin, details, code);
        }
    }

    public static class InvalidSyntaxError extends CustomException {
        public InvalidSyntaxError(Position posBegin, String details, int code) {
            super(posBegin, details, code);
        }
    }

    public static class NumberOfArguments extends CustomException {
        public NumberOfArguments(Position posBegin, String details, int code) {
            super(posBegin, details, code);
        }
    }

    public static class CustomRuntimeError extends CustomException {
        public CustomRuntimeError(Position posBegin, String details, int code) {
            super(posBegin, details, code);
        }
    }

    public static class FileParsingException extends CustomException {
        public FileParsingException(String details, int code) {
            super(details, code);
        }
    }
}
