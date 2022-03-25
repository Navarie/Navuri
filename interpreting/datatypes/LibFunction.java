package interpreting.datatypes;

import errors.CustomException;
import interpreting.RuntimeResult;
import lexing.Position;
import program.Applet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LibFunction extends CoreFunction implements Value {

    private final String name;

    public LibFunction(String name) {
        super(name);
        this.name = name;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        RuntimeResult runtimeResult = new RuntimeResult();
        int value = 0;
        String executeString = getName();
        runtimeResult.register(super.validateAndPopulate(getArgNames(executeString), args));

        System.out.println("Arguments are... " + args);
        switch (executeString) {
            case "Print" -> value = runtimeResult.register(executePrint(String.valueOf(args.get(0))));
            case "Run" -> value = runtimeResult.register(executeRun());
        }

        return runtimeResult.success(value);
    }

    private List<String> getArgNames(String executeString) {
        switch (executeString) {
            case "Print" -> {
                return Collections.singletonList("valueToPrint"); }
            case "Run" -> {
                return Collections.singletonList("fileToRun"); }
            default -> {
                return Collections.singletonList("No arguments found!");
            }
        }
    }

    private RuntimeResult executePrint(String executeString) {
        System.out.println(">> Printing... ");
        System.out.println(executeString);
        return new RuntimeResult().success(null);
    }

    private RuntimeResult executeRun() {
        try {
            File file = new File("C:\\Users\\frede\\IdeaProjects\\Navuri\\src\\program\\example.txt");
            Scanner reader = new Scanner(file);
            StringBuilder fileContents = new StringBuilder();
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                fileContents.append(data);
            }
            String fileContent = fileContents.toString();
            reader.close();

            Applet.runApplet(file.getName(), fileContent);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading the file.");
            e.printStackTrace();
        } catch (CustomException.CustomRuntimeError | CustomException.InvalidSyntaxError | CustomException.IllegalTokenError
                | CustomException.FileParsingException | CustomException.VariableNotInitialised | CustomException.NumberOfArguments e) {
            e.printStackTrace();
        }

        return new RuntimeResult().success(null);
    }

    public void setName(String name) {}
    public Value setPosition(Position posBegin, Position posEnd) { return this; }
    public Position getPosEnd() { return null; }
    public Position getPosBegin() { return null; }
    public String getName() { return name; }

    public String toString() {
        return "<Lib-function " + super.getName() + ">";
    }
}
