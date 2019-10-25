/**
 * @author  Raul Aguilar
 * @date    October 24, 2019
 * CodeWriter: Translates VM commands into Hack assembly code
 */

// TODO:
// Write to static - get the file name, concanidate an '@' and '.index'

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CodeWriter {
    PrintWriter outputFile = null;
    private int labelCounter = 1;
    
    /**
     * Opens the output file and gets ready to write into it
     * @param fileName Name of the output file
     */
    public void CodeWriter(String fileName) {
        try {
            outputFile = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Could not open output file " + fileName);
            System.err.println("Run program again, make sure you have write permissions, etc.");
            System.err.println("Program exiting.");
            System.exit(0);
        }
    }
    
    /**
     * Closes the output file
     */
    public void close() {
        outputFile.close();
    }

    public void writeLine() {
        outputFile.println("=================");
    }

    public void writeInfiniteLoop() {
        outputFile.println("(END)");
        outputFile.println("@END");
        outputFile.println("0;JMP");
    }

    /**
     * Writes the assembly code that is the translation of the given
     * arithmetic command
     * @param command   The arithmetic command given
     */
    public void writeArithmetic(String command) {
        switch(command) {
            case "add": case "sub":
                writeAddSub(command);
                break;
            case "neg": case "not":
                writeNegateNot(command);
                break;
            case "eq": case "lt": case "gt":
                writeEqualities(command);
                break;
            case "and": case "or":
                writeAndOr(command);
                break;
        }
    }

    /**
     * Writes the assembly code that is the translation of the given command,
     * where command is either C_PUSH or C_POP
     * @param command   Push or Pop command
     * @param segment   Memory segment to manipulate
     * @param index     Memory address to go to
     */
    public void writePushPop(Command command, String segment, int index) {
        String seg = "";
        switch(segment) {
            case "local":
                seg = "LCL";
                break;
            case "argument":
                seg = "ARG";
                break;
            case "this": case "that":
                seg = segment.toUpperCase();
                break;
            case "pointer":
                if(index == 0) {
                    seg = "THIS";
                    break;
                }
                if(index == 1) {
                    seg = "THAT";
                    break;
                }
        }

        if(command == Command.C_PUSH) {
            if(segment.equals("constant")) {
                writePushCont(index);
            } else {
                writePush(seg, index);
            }
        }
        if(command == Command.C_POP) {
            writePop(seg, index);
        }
    }

    /* WRITE ARITHMETIC AND LOGICAL COMMANDS */

    /**
     * Helper method to write assembly code for add and sub arithmetic
     * depending on the given command
     * @param command   The arithmetic command to perform
     */
    private void writeAddSub(String command) {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
        outputFile.println("A = A - 1");
        if(command.equals("add")) {
            outputFile.println("M = M + D");
        } else if(command.equals("sub")) {
            outputFile.println("M = M - D");
        }
    }

    /**
     * Helper method to write assembly code to negate
     */
    private void writeNegateNot(String command) {
        outputFile.println("@SP");
        outputFile.println("A = M - 1");
        if(command.equals("neg")) {
            outputFile.println("M = -M");
        } else if(command.equals("not")) {
            outputFile.println("M = !M");
        }
    }

    /**
     * Helper method to write assembly code for 'and' and 'or' depending on
     * the given arithmetic command
     * @param command   The arithmetic command to perform
     */
    private void writeAndOr(String command) {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
        outputFile.println("A = A - 1");
        switch(command) {
            case "and":
                outputFile.println("M = D&M");
                break;
            case "or":
                outputFile.println("M = D|M");
                break;
        }
    }

    /**
     * Helper method to write assembly code for equality comparison
     * @param command The equality command
     */
    private void writeEqualities(String command) {
        if(command.equals("lt")) {
            command = "gt";
        } else if(command.equals("gt")) {
            command = "lt";
        }
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
        outputFile.println("A = A - 1");
        outputFile.println("D = D - M");
        outputFile.println("@_"+ labelCounter++);
        outputFile.println("D;J" + command.toUpperCase());
        outputFile.println("@_"+ labelCounter++);
        outputFile.println("D = 0");
        outputFile.println("0;JMP");
        outputFile.println("(_" + (labelCounter-2) + ")");
        outputFile.println("D = -1");
        outputFile.println("(_" + (labelCounter-1) + ")");
        outputFile.println("@SP");
        outputFile.println("A = M - 1");
        outputFile.println("M = D");
    }

    /* WRITE TO MEMORY SEGMENTS */

    /**
     * Helper method to push a value to stack
     * Moves SP forward by 1 and stores value in previous location
     */
    private void writePushD() {
        outputFile.println("@SP");
        outputFile.println("M = M + 1");
        outputFile.println("A = M - 1");
        outputFile.println("M = D");
    }

    /**
     * Helper method for pop commands
     * Moves the SP back by 1 and stores the value
     */
    private void writePopD() {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
    }

    /**
     * Writes assembly code for the vm 'pop' command
     * Takes the memory segment from the vm code and translates it into its
     * corresponding predefined symbol
     * Uses the index to find the correct memory address
     * @param segment   Memory segment to pop to
     * @param index     Index of the memory segment address
     */
    private void writePop(String seg, int index) {
        // write to file
        writePopD();
        if(index > 2) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            outputFile.println("@"+seg);
            outputFile.println("A = D + M");
        } else {
            outputFile.println("@"+seg);
            switch(index) {
                case 2:
                    outputFile.println("A = M + 1");
                    outputFile.println("A = A + 1");
                    break;
                case 1:
                    outputFile.println("A = M + 1");
                    break;
            }
        }
        outputFile.println("M = D");
    }

    /**
     * Helper method to write assembly code for push constants
     * @param index RAM location / constant
     */
    private void writePushCont(int index) {
        if(index > 1) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            writePushD();
        } else if(index == 1) {
            outputFile.println("@SP");
            outputFile.println("M = M + 1");
            outputFile.println("A = M - 1");
            outputFile.println("M = 1");
        } else {
            System.out.println("Index is not a positive number.");
        }
    }

    private void writePush(String seg, int index) {
        // Get data from segment and index
        if(index > 2) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            outputFile.println("@"+seg);
            outputFile.println("A = D + A");
        } else {
            outputFile.println("@"+seg);
            switch(index) {
                case 2:
                    outputFile.println("A = M + 1");
                    outputFile.println("A = A + 1");
                    break;
                case 1:
                    outputFile.println("A = M + 1");
                    break;
            }
        }
        outputFile.println("D = M");
        // push it to the stack
        writePushD();
    }
}