/**
 * @author  Raul Aguilar
 * @date    October 21, 2019
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
        }
    }

    void writePushPop(Command command, String segment, int index) {
        if(command == Command.C_PUSH) {
            if(segment.equals("constant")) {
                writePushCont(index);
            }
        }
    }

    /* WRITE ARITHMETIC AND LOGICAL COMMANDS */
    // TODO: Understand what negate, and/or, and not does and then write assembly
    // code for it

    /**
     * Helper method to write assembly code for add and sub arithmetic
     * depending on the given command
     * @param command   The arithmetic command
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
    // TODO: Finish writing assmebly code for each memory segment
    // Figure out how to get to a specific memory segment first

    /**
     * Helper method to push a value to stack
     */
    private void writePushD() {
        outputFile.println("@SP");
        outputFile.println("M = M + 1");
        outputFile.println("A = M - 1");
        outputFile.println("M = D");
    }

    /**
     * Helper method for pop commands to move the SP back by 1 and take value
     */
    private void writeMoveSPBack() {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
    }

    private void writePopLocal(int index) {
        writeMoveSPBack();
        if(index > 2) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            outputFile.println("@LCL");
            outputFile.println("A = D + M");
        } else {
            outputFile.println("@LCL");
            switch(index) {
                case 2:
                    outputFile.println("A = M + 1");
                    outputFile.println("A = A + 1");
                    break;
                case 1:
                    outputFile.println("A = M + 1");
            }
        }
        outputFile.println("M = D");
    }

    /**
     * Helper method to write assembly code for push constants
     * @param index RAM location
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