/*
	@author	Raul Aguilar
	@date	October 23, 2019
*/
import java.util.Scanner;

public class VirtualMachine {
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		Parser parser = new Parser();
		CodeWriter codeWriter = new CodeWriter();
		String inputFileName, outputFileName;
		

		// Open file from command line or console
		if(args.length == 1) {
			System.out.println("command line arg = " + args[0]);
			inputFileName = args[0];
		} else {
			System.out.println("Please enter assembly file name you would like to assemble.");
			System.out.println("Don't forget the .vm extension: ");
			inputFileName = keyboard.nextLine();
			keyboard.close();
		}

		outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".asm";
		codeWriter.CodeWriter(outputFileName);

		// Driver
		parser.Parser(inputFileName);
		while(parser.hasMoreCommands()) {
			parser.advance();

			// write to file
			if(parser.getCommandType() == Command.C_ARITHMETIC) {
				codeWriter.writeArithmetic(parser.getArg1());
			} else if (parser.getCommandType() == Command.C_PUSH
						|| parser.getCommandType() == Command.C_POP) {
				codeWriter.writePushPop(parser.getCommandType(), parser.getArg1(), parser.getArg2());
		}

		}
		codeWriter.close();
		System.out.println("Finished assemblng. Program exiting.");
	}
}