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
            //TODO: handle exception
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

    /**
     * Helper method to write assembly code for add and sub arithmetic
     * depending on the given command
     * @param command The arithmetic command
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
     * Helper method to write assembly code for push constants
     * @param index RAM location
     */
    private void writePushCont(int index) {
        if(index > 1) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            outputFile.println("@SP");
            outputFile.println("M = M + 1");
            outputFile.println("A = M - 1");
            outputFile.println("M = D");
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