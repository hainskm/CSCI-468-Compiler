package src;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

/*
 * @author Josh McCleary, Jonathan Johnson, and Kevin Hainsworth
 * CSCI 468 Lab 1 Scanner
 */

public class Main extends LittleBaseListener {

	public static void main(String[] args) throws Exception {

		StringBuilder file = new StringBuilder();

		try (BufferedReader br = new BufferedReader(
				new FileReader("step4_testcase.micro"/* args[0] */))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				file.append(sCurrentLine + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String stuff = file.toString();

		// generating antlr code
		ANTLRInputStream in = new ANTLRInputStream(stuff);
		LittleLexer lexer = new LittleLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LittleParser parser = new LittleParser(tokens);
		ParseTree tree = parser.start();

		// printing the text input
		//PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		//System.setOut(out); // setting the standard out to the output file
		
		ParseTreeWalker walker = new ParseTreeWalker();
		ExtractInterfaceListener extractor = new ExtractInterfaceListener(parser);
		walker.walk(extractor, tree);
		//out.close();
		// resetting the standard out
		//System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		
		/*
		if (extractor.error() == true) {
			// PrintStream out2 = new PrintStream(new
			// FileOutputStream("output.txt"));
			// out2.print("DECLARATION ERROR " + extractor.errors.get(0));
			// out2.close();
			System.out.println("DECLATATION ERROR " + extractor.errors.get(0));
		} else {
			BufferedReader br = new BufferedReader(new FileReader("output.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		}
		*/
	}
}
