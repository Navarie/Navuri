package nodes;

import lexing.Position;

public interface Node {

    Position getPosBegin();
    Position getPosEnd();
    String getTypeString();

    String toString();
}

