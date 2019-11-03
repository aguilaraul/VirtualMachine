/**
 * @author  Raul Aguilar
 * @date    03 November 2019
 * Parser: Handles the parsing of a single .vm file, and encapsulates access to the input code.
 *  It reads VM commands, parses them, and provides convenient access to their components. In
 *  addition, it removes all white spaces and comments.
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

// TODO:
// Handle the parsing of goto, if-goto, label, call, function, and return
// Fix cleaning up lines to remove comment without removing white spaces

public class Parser {
    private Scanner inputFile;
    private String[] commands;
    private int lineNumber;
    private String rawCommand, cleanCommand;
    private Command commandType;
    private String arg1 = "";
    private int arg2;

    /**
     * Opens the input file and gets ready to parse it
     * @param fileName Name of the vm file
     */
    public void Parser(String fileName) {
        try {
            inputFile = new Scanner(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found. Exiting program.");
            System.exit(0);
        }
    }

    /**
     * Returns boolean if there are more commands in the file, if not closes
     * the file
     * @return True if there are more commands, otherwise false and closes stream
     */
    public boolean hasMoreCommands() {
        if(inputFile.hasNextLine()) {
            return true;
        } else {
            inputFile.close();
            return false;
        }
    }

    /**
     * Reads the next command from the input and makes it the current command. Should
     * be called only if hasMoreCommands() is true. Initially there is no current
     * command.
     */
    public void advance() {
        lineNumber++;
        rawCommand = inputFile.nextLine();
        cleanLine();
        parseCommand();
        parseCommandType();
        if(commandType != Command.NO_COMMAND) {
            parseArg1();
            if(commandType == Command.C_PUSH 
                || commandType == Command.C_POP
                || commandType == Command.C_FUNCTION
                || commandType == Command.C_CALL) {
                parseArg2();
            }
        }
    }

    /**
     * Reads command line from vm file and strips it of white spaces and comments
     */
    private void cleanLine() {
        int commentIndex;
        if(rawCommand == null) {
            cleanCommand = "";
        } else {
            commentIndex = rawCommand.indexOf("/");
            // @Incomplete: Remove comment without removing white spaces because that's how I parse
            //  commands
            if(commentIndex != -1) {
                cleanCommand = rawCommand.substring(0, commentIndex);
                cleanCommand = cleanCommand.replaceAll(" ", "");
                cleanCommand = cleanCommand.replaceAll("\t", "");
            } else {
                cleanCommand = rawCommand;
            }
        }
    }

    /**
     * Parse the cleaned up line into parts using a String array
     */
    private void parseCommand() {
        if(cleanCommand != null) {
            commands = cleanCommand.split(" ");
        }
    }

    /**
     * Guess the command type 
     */
    private void parseCommandType() {
        if(cleanCommand == null || cleanCommand.length() == 0) {
            commandType = Command.NO_COMMAND;
        }
        if(commands.length == 1) {
            commandType = Command.C_ARITHMETIC;
        }
        if(commands.length > 1) {
            switch(commands[0]) {
                case "pop":
                    commandType = Command.C_POP;
                    break;
                case "push":
                    commandType = Command.C_PUSH;
                    break;
                case "label":
                    // @Debug:
                    System.out.println("I'M INSIDE label");
                    commandType = Command.C_LABEL;
                    break;
                case "goto":
                    // @Debug
                    System.out.println("OVER IN goto");
                    commandType = Command.C_GOTO;
                    break;
                case "if-goto":
                    // @Debug:
                    System.out.println("HERE I AM!");
                    commandType = Command.C_IF;
                    break;
                case "function":
                    commandType = Command.C_FUNCTION;
                    break;
                case "call":
                    commandType = Command.C_CALL;
                    break;
                case "return":
                    commandType = Command.C_RETURN;
                    break;
            }
        }
    }

    /**
     * Parses the first argument of the vm command line
     */
    private void parseArg1() {
        switch(commandType) {
            case NO_COMMAND:
                arg1 = "";
                break;
            case C_ARITHMETIC:
                arg1 = commands[0];
                break;
            default:
                arg1 = commands[1];
                break;
        }
    }

    /**
     * Parses the second argument of the vm command line
     * Should only be called if command type is push, pop, function, or call
     */
    private void parseArg2() {
        arg2 = Integer.parseInt(commands[2]);
    }

    /**
     * Return the Command enum for the command type of the current line
     * @return Command type enum
     */
    public Command getCommandType() {
        return commandType;
    }

    /**
     * Return the line number of the current line
     * @return The line number of current line
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the first argument of the vm command line, if there is one. May
     * have already been initialized
     * @return the first argument of the vm command line
     */
    public String getArg1() {
        return arg1;
    }

    /**
     * Return the second argument of the vm command line, if there is one. May
     * have already been initialized
     * @return the second argument of the vm command
     */
    public int getArg2() {
        return arg2;
    }
}
