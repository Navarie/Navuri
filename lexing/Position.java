package lexing;

import static lexing.Token.*;

public class Position {

    private int index;
    private int line;
    private int column;
    private final String fileName;
    private final String fileText;

    public Position(int index, int line, int column, String fileName, String fileText) {
        this.index = index;
        this.line = line;
        this.column = column;
        this.fileName = fileName;
        this.fileText = fileText;
    }

    public int getColumn() { return column; }
    public int getIndex() { return index; }
    public int getLine() { return line; }
    public String getFileName() { return fileName; }

    public void advancePosition(Token currentToken) {
        this.index++;
        this.column++;

        boolean tokenNullCheck = (currentToken != null);
        if (tokenNullCheck && (currentToken.getValue().equals(T_SEMICOLON)
            || currentToken.getValue().equals(T_L_BRACKET))) {
            this.line++;
            this.column = 0;
        }
    }

    public Position copyPosition() {
        return new Position(this.index, this.line, this.column, this.fileName, this.fileText);
    }
}
