/**
 * @author  Raul Aguilar
 * @date    October 21, 2019
 * Parser: Handles the parsing of a single .vm file, and encapsulates access to the input code.
 *  It reads VM commands, parses them, and provides convenient access to their components. In
 *  addition, it removes all white spaces and comments.
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

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
    }

    /**
     * Reads command line from vm file and strips it of whitespaces and comments
     */
    private void cleanLine() {
        int commentIndex;
        if(rawCommand == null) {
            cleanCommand = "";
        } else {
            commentIndex = rawCommand.indexOf("/");
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
        } else if(commands.length == 1) {
            commandType = Command.C_ARITHMETIC;
        } else if(commands[0].equals("pop")) {
            commandType = Command.C_POP;
        } else if(commands[0].equals("push")) {
            commandType = Command.C_PUSH;
        }
    }

    /**
     * Parses the first argument of the vm command line
     */
    private void arg1() {
        switch(commandType) {
            case NO_COMMAND:
                arg1 = "";
            case C_ARITHMETIC:
                arg1 = commands[0];
            default:
                arg1 = commands[1];
        }
    }

    /**
     * Parses the second argument of the vm command line
     * Should only be called if command type is push or pop
     */
    private void arg2() {
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
     * have already been initilized
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