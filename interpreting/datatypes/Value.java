package interpreting.datatypes;

import errors.CustomException;
import interpreting.RuntimeResult;
import lexing.Position;

import java.util.List;

public interface Value {

    RuntimeResult execute(List<Value> args, CoreFunction closure) throws CustomException.VariableNotInitialised, CustomException.NumberOfArguments;
    void setName(String name);
    Value setPosition(Position posBegin, Position posEnd);
    Position getPosEnd();
    Position getPosBegin();

    Object getIntValue();
}
