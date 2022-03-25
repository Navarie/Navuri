package program;

import interpreting.*;
import interpreting.environments.ClassEnvironment;
import interpreting.environments.FunctionEnvironment;
import interpreting.environments.VariableEnvironment;
import lexing.Lexer;
import lexing.Token;
import parsing.ParseResult;
import parsing.Parser;

import java.util.List;
import java.util.Scanner;

import static errors.CustomException.*;
import static program.Options.*;

public class Applet {

    static final VariableEnvironment varEnv = new VariableEnvironment();
    static final FunctionEnvironment funEnv = new FunctionEnvironment();
    static final ClassEnvironment classEnv = new ClassEnvironment();

    public static FunctionEnvironment getFunEnv() {
        return funEnv;
    }
    public static VariableEnvironment getVarEnv() {
        return varEnv;
    }
    @SuppressWarnings("unused")
    public static ClassEnvironment getClassEnv() { return classEnv; }

    public static void main(String[] args) throws InvalidSyntaxError, IllegalTokenError, CustomRuntimeError,
            FileParsingException, VariableNotInitialised, NumberOfArguments {

        varEnv.populateEnvironment();
        funEnv.populateEnvironment();
        classEnv.populateEnvironment();

        Scanner keyboard = new Scanner(System.in);
        boolean runContinually = true;
        while (runContinually) {
            System.out.println("nav <<");
            String input = keyboard.nextLine();

                // Run the program continuously from command-line
            runApplet("<System.in>", input);
            if (runOnce) runContinually = false;
        }
    }

    public static void runApplet(String program, String input)
            throws InvalidSyntaxError, IllegalTokenError, CustomRuntimeError, FileParsingException,
            VariableNotInitialised, NumberOfArguments {
        Lexer lexer = new Lexer(program, input);
        lexer.createTokens();
        List<Token> tokens = lexer.getTokens();
        if (printTokens) {
            System.out.println(">> Tokens:\n" + tokens);
        }

        Parser parser = new Parser(tokens);
        ParseResult ast = parser.parse();
        if (printAST && !input.equals("run()")) {
            System.out.println("\n>> Abstract Syntax Tree:\n" + ast.getNode());
        }

        Interpreter interpreter = new Interpreter();
        RuntimeResult result = interpreter.traverse(ast.getNode());
        if (interpret && result != null) {
            if (!result.getListValue().toString().equals("")) {
                System.out.println("\n>> Output: " + result.getListValue());
            } else if (!input.equals("run()")) {
                System.out.println("\n>> No (non-boolean) return value found.");
            }

            if (!getVarEnv().toString().equals("{}") && !input.equals("run()")) {
                System.out.println("Variables: " + getVarEnv());
            }
        }
    }
}
