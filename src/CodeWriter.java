/**
 * @author  Raul Aguilar
 * @date    October 23, 2019
 * CodeWriter: Translates VM commands into Hack assembly code
 */

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

    void writeArithmetic(String command) {
        switch(command) {
            case "add": case "sub":
                writeAddSub(command);
                break;
            case "neg":
                writeNegate(command);
                break;
            case "eq": case "lt": case "gt":
                writeEqualities(command);
                break;
            case "and": case "or":
                writeAndOr(command);
                break;
            case "not":
                writeNot(command);
                break;
        }
    }

    void writePushPop(Command command, String segment, int index) {
        if(command == Command.C_PUSH) {
            if(segment.equals("constant")) {
                writePushCont(index);
            }
        }
        if(command == Command.C_POP) {
            writePop(segment, index);
        }
    }

    /* WRITE ARITHMETIC AND LOGICAL COMMANDS */
    // TODO:
    // Understand what negate, and/or, and not does and then write assembly code for it

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

    private void writeNegate(String command) {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("M = !M");
    }

    private void writeAndOr(String command) {
        // return boolean
    }

    private void writeNot(String command) {
        // return boolean
    }

    private void writeEqualities(String command) {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("A = A - 1");
        outputFile.println("D = M");
        outputFile.println("D = D - M");
        outputFile.println("@_"+ labelCounter++);
        outputFile.println("D;J" + command.toUpperCase());
        outputFile.println("@_"+ labelCounter++);
        outputFile.println("D = 0");
        outputFile.println("0; JMP");
        outputFile.println("(_" + (labelCounter-2) + ")");
        outputFile.println("D = -1");
        outputFile.println("(_" + (labelCounter-1) + ")");
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("M = D");
    }

    /* WRITE TO MEMORY SEGMENTS */
    // TODO:
    // Finish writing assmebly code for each memory segment
    // Figure out how to get to a specific memory segment first

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
    private void writePop(String segment, int index) {
        // decide which segment to use in assembly code
        String seg = "";
        switch(segment) {
            case "local":
                seg = "LCL";
                break;
            case "argument":
                seg = "ARG";
                break;
            case "this": case "that": case "temp":
                seg = segment.toUpperCase();
        }

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
}