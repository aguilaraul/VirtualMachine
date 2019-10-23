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
        //TODO: Parse command
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

    private void parseCommandType() {
        commands = cleanCommand.split(" ");
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

    public Command getCommandType() {
        return commandType;
    }
}