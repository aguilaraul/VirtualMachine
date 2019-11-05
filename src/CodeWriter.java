/**
 * @author  Raul Aguilar
 * @date    04 November 2019
 * CodeWriter: Translates VM commands into Hack assembly code
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// TODO:
// Separate constructor and setFileName into separate methods, so that parsing multiple files is
//  possible
// Add additional functionality: setFileName, writeInit, writeFunction, writeCall, writeReturn
// Write public function method like writePushPop
// Figure out how function and call work

public class CodeWriter {
    PrintWriter outputFile = null;
    private int labelCounter = 1;
    private String file = "";

    /**
     * Opens the output file and gets ready to write into it
     * @param fileName  Name of the output file
     */
    public void CodeWriter(String fileName) {
        try {
            outputFile = new PrintWriter(fileName);
            file = fileName.substring(0, fileName.lastIndexOf('.'));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open output file " + fileName);
            System.err.println("Run program again, make sure you have write permissions, etc.");
            System.err.println("Program exiting.");
            System.exit(0);
        }
    }

    /**
     * Informs the code writer that the translation of a new VM file is started
     * @param fileName
     */
    private void setFileName(String fileName) {
        // @Incomplete: Find out with this means and how/why it's different from the constructor
    }

    /**
     * Closes the output file
     */
    public void close() {
        outputFile.close();
    }

    /**
     * Writes assembly code that effects the VM initialization. This code must be placed at the
     * beginning of the output file.
     */
    private void writeInit() {
        // @Incomplete: Figure out and write bootstrap code
    }

    /**
     * Writes an infinite loop to prevent NOP slide at the end of translation
     */
    public void writeInfiniteLoop() {
        outputFile.println("(END)");
        outputFile.println("@END");
        outputFile.println("0;JMP");
    }

    /**
     * Writes the assembly code for the given arithmetic command
     * @param command   The arithmetic command to perform
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
                    segment = "THIS";
                    break;
                }
                if(index == 1) {
                    segment = "THAT";
                    break;
                }
            case "static":
                seg = file;
                break;
        }

        if(command == Command.C_PUSH) {
            if(segment.equals("constant")) {
                writePushConstant(index);
            } else if(segment.equals("THIS") || segment.equals("THAT")) {
                writePushPointer(segment);
            } else if(segment.equals("temp")) {
                writePushTemp(index);
            } else if(seg.equals(file)) {
                writePushStatic(file, index);
            } else {
                writePush(seg, index);
            }
        }
        if(command == Command.C_POP) {
            if(segment.equals("THIS") || segment.equals("THAT") ) {
                writePopPointer(segment);
            } else if(segment.equals("temp")) {
                writePopTemp(index);
            } else if(seg.equals(file)) {
                writePopStatic(file, index);
            } else {
                writePop(seg, index);
            }
        }
    }

    /**
     * Writes assembly code for branching commands depending on the current lines command type
     * @param command   Which branch command to perform
     * @param label     Label to use when writing assembly code
     */
    public void writeBranch(Command command, String label) {
        if(command == Command.C_LABEL) {
            writeLabel(label);
        }
        if(command == Command.C_GOTO) {
            writeGoto(label);
        }
        if(command == Command.C_IF) {
            writeIf(label);
        }
    }

    /* ARITHMETIC AND LOGICAL COMMANDS */

    /**
     * Write to file assembly code for 'add' and 'sub' arithmetic
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
     * Write to file assembly code for negate or not arithmetic command
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
     * Write to file assembly code for 'and' and 'or' depending on
     * the given arithmetic command
     * @param command   The arithmetic command to perform
     */
    private void writeAndOr(String command) {
        outputFile.println("@SP");
        outputFile.println("AM = M - 1");
        outputFile.println("D = M");
        outputFile.println("A = A - 1");
        if(command.equals("and")) {
            outputFile.println("M = D&M");
        } else if (command.equals("or")) {
            outputFile.println("M = D|M");
        }
    }

    /**
     * Write to file the assembly code for equality comparison
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
        outputFile.println("D = 0");        // D != -1 (false)
        outputFile.println("0;JMP");
        outputFile.println("(_" + (labelCounter-2) + ")");
        outputFile.println("D = -1");       // D = -1  (true)
        outputFile.println("(_" + (labelCounter-1) + ")");
        outputFile.println("@SP");
        outputFile.println("A = M - 1");
        outputFile.println("M = D");
    }

    /* MEMORY ACCESS COMMANDS */

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
     * @param seg   Memory segment to pop to
     * @param index     Index of the memory segment address
     */
    private void writePop(String seg, int index) {
        // write to file
        writePopD();
        outputFile.println("@"+seg);
        if(index > 0) {
            outputFile.println("A = M + 1");
            for(int i = 1; i < index; i++) {
                outputFile.println("A = A + 1");
            }
        } else {
            outputFile.println("A = M");
        }
        outputFile.println("M = D");
    }

    /**
     * Writes assembly code to pop to THIS or THAT ram locations
     * @param segment   THIS or THAT depending on given pointer index
     */
    private void writePopPointer(String segment) {
        writePopD();
        outputFile.println("@"+segment);
        outputFile.println("M = D");
    }

    /**
     * Writes assembly code to pop into static memory segment
     */
    private void writePopStatic(String file, int index) {
        writePopD();
        outputFile.println("@"+file+"."+index);
        outputFile.println("M = D");
    }

    /**
     * Writes assembly code to pop into the temp memory segment
     * @param index Index of temp location to access
     */
    private void writePopTemp(int index) {
        writePopD();
        outputFile.println("@"+(5+index));
        outputFile.println("M = D");
    }

    /**
     * Writes assembly code to push from memory segment to the stack
     * @param seg   Memory segment of stack
     * @param index Location in the memory segment
     */
    private void writePush(String seg, int index) {
        // Get data from segment and index
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
                case 0:
                    outputFile.println("A = M");
            }
        }
        outputFile.println("D = M");
        // push it to the stack
        writePushD();
    }

    /**
     * Helper method to write assembly code for push constants
     * @param index RAM location / constant
     */
    private void writePushConstant(int index) {
        if(index > 1) {
            outputFile.println("@"+index);
            outputFile.println("D = A");
            writePushD();
        } else if(index == 1 || index == 0) {
            outputFile.println("@SP");
            outputFile.println("M = M + 1");
            outputFile.println("A = M - 1");
            if(index == 1) {
                outputFile.println("M = 1");
            } else if(index == 0) {
                outputFile.println("M = 0");
            }
        } else {
            System.err.println("Index is not a positive number.");
        }
    }

    /**
     * Pushes data to the stack from the temp memory segment
     * @param index Index of the memory location to access
     */
    private void writePushTemp(int index) {
        outputFile.println("@"+(5+index));
        outputFile.println("D = M");
        writePushD();
    }

    /**
     * Pushes data to stack from static memory
     * @param file  Name of the file
     * @param index Index of memory location to access
     */
    private void writePushStatic(String file, int index) {
        outputFile.println("@"+file+'.'+index);
        outputFile.println("D = M");
        writePushD();
    }

    /**
     * Pushes from THIS or THAT ram locations depending on given pointer index
     * @param segment   THIS or THAT
     */
    private void writePushPointer(String segment) {
        outputFile.println("@"+segment);
        outputFile.println("D = M");
        writePushD();
    }

    /*  BRANCHING COMMANDS */

    /**
     * Writes assembly code for label command
     * @param label Name of the label
     */
    private void writeLabel(String label) {
        outputFile.println("(" + label + ")");
    }

    /**
     * Writes assembly code for the goto command
     * An unconditional jump to the label
     * @param label Name of the label to jump to
     */
    private void writeGoto(String label) {
        outputFile.println("@"+label);
        outputFile.println("0;JMP");
    }

    /**
     * Writes assembly code for if-goto command
     * The stack's topmost value is popped; if the value is not zero, execution continues
     * from the location marked by the label; otherwise, execution continues from the
     * next command in the program.
     * @param label Name of the label to jump to
     */
    private void writeIf(String label) {
        // (BasicLoop.vm)
		// If counter > 0, goto LOOP_START

        // If-go to compares to zero/true
        // if D = 0, fall through
        // if D != 0, go to loop label

        // D = !M   true -> false   false -> true
        //           -1  ->   0       0   ->  -1
        //           15  ->  -16    -15   ->  14

        // @Incomplete: Figure this out. It compares to zero, which works with true/false but doesn't
        // work with counters, or does it? This currently passes BasicLoop.tst
        writePopD();
        outputFile.println("@"+label);
        outputFile.println("D;JGT");        // if D > 0 true, jump to label - else fall through
	  }

    /*  FUNCTION COMMANDS */
    // TODO: Figure out how function command methods work

    private void writeFunction(String functionName, int numLocals) {
        // @Incomplete: Figure out how function works

        // Here starts the code of a function named f that has n local variables

        // (f)                      // Declare a label for the function entry
        // repeat k times           // k = number of local variables
        // PUSH 0                   // Initialize all of them to 0

    }

    private void writeCall(String functionName, int numArgs) {
        // @Incomplete: Figure out how call works

        // Call function, stating that m arguments have already been pushed onto the stack
        //  by the caller

        // push return-address      // (Using the label declared below)
        // push LCL                 // Save LCL of the calling function
        outputFile.println("@LCL");
        outputFile.println("D = M");
        writePushD();
        // push ARG                 // Save ARG of the calling function
        outputFile.println("@ARG");
        outputFile.println("D = M");
        writePushD();
        // push THIS                // Save THIS of the calling function
        outputFile.println("@THIS");
        outputFile.println("D = M");
        writePushD();
        // push THAT                // Save THAT of the calling function
        outputFile.println("@THAT");
        outputFile.println("D = M");
        writePushD();
        // ARG = SP-n-5             // Reposition ARG (n=number of args)
        outputFile.println("@SP");          // A = 0    RAM[0] = 275
        outputFile.println("@D = M");       // D = 275
        outputFile.println("@5");           // A = 5
        outputFile.println("D = D - A");    // D = 275 - 5
        outputFile.println("@"+numArgs);    // A = numArgs
        outputFile.println("D = D - A");    // D = 270 - numArgs
        outputFile.println("@ARG");         // A = 2    RAM[2]
        outputFile.println("M = D");        // RAM[2] = D = 270 - numArgs
        // LCL = SP                 // Reposition LCL
        outputFile.println("@SP");
        outputFile.println("D = M");
        outputFile.println("@LCL");
        outputFile.println("M = D");
        // goto f                   // Transfer control
        writeGoto(functionName);
        // (return-address)         // Declare a label for the return-address
    }

    private void writeReturn() {
        // @Incomplete: Figure out how return works

        // Return to the calling function

        // FRAME = LCL              // FRAME is a temporary variable
        // RET = *(FRAME-5)         // Put the return-address in a temp var.
        // *ARG = pop()             // Reposition the return value for the caller
        // SP = ARG+1               // Restore SP of the caller
        // THAT = *(FRAME-1)        // Restore THAT of the caller
        // THIS = *(FRAME-2)        // Restore THIS of the caller
        // ARG  = *(FRAME-3)        // Restore ARG of the caller
        // LCL  = *(FRAME-4)        // Restore LCL of the caller
        // goto RET                 // Goto return-address (in the caller's code)
    }

}