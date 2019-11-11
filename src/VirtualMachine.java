/**
 * @author  Raul Aguilar
 * @date    11 November 2019
 * VirtutalMachine: Should construct a Parser to parse the VM input file and a CodeWriter to
 * generate code into the corresponding output file. It should march through the VM commands in the
 * input file and generate assembly code for each one of them.
 */
import java.util.Scanner;

public class VirtualMachine {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Parser parser = new Parser();
        CodeWriter codeWriter = new CodeWriter();
        String inputFileName;

        // Open file from console
        System.out.println("Please enter the .vm file name you would like to assemble.");
        System.out.println("Don't forget the .vm extension: ");
        inputFileName = keyboard.nextLine();
        keyboard.close();

        // Open input file and output file
        parser.Parser(inputFileName);
        codeWriter.setFileName(inputFileName);

        // Initialize file
        codeWriter.writeInit();
        // Begin parsing vm file
        while(parser.hasMoreCommands()) {
            parser.advance();
            switch(parser.getCommandType()) {
                case C_ARITHMETIC:
                    codeWriter.writeArithmetic(parser.getArg1());
                    break;
                case C_PUSH: case C_POP:
                    codeWriter.writePushPop(parser.getCommandType(), parser.getArg1(), parser.getArg2());
                    break;
                case C_LABEL: case C_GOTO: case C_IF:
					codeWriter.writeBranch(parser.getCommandType(), parser.getArg1());
					break;
                case C_FUNCTION: case C_CALL: case C_RETURN:
					codeWriter.writeFunctions(parser.getCommandType(), parser.getArg1(), parser.getArg2());
					break;
            }
        }
        codeWriter.writeInfiniteLoop();
        codeWriter.close();
        System.out.println("Finished assembling. Program exiting.");
    }
}
