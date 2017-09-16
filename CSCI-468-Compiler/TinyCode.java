package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

/*
 * @author Jonathan Johnson, Josh McCleary, and Kevin Hainsworth
 * @since 27042016-1512
 * @update 29042016-1514
 * CSCI 468 Compilers
 */

public class TinyCode extends LittleBaseListener {
	// STEP 3 VARIBALES
	int block_num = 1;
	ArrayList<HashMap<String, String>> tableStack = new ArrayList<>();
	private boolean error = false;
	ArrayList<String> errors = new ArrayList<>();
		
	LittleParser parser;
	int labelNum = 1;
	int regisNum = 0;
	int whileEndNum = 0;
	String label = "label";
	String register = "r";
	List<String> endIfStmtList = new ArrayList<String>();

	public TinyCode(LittleParser parser) {
		this.parser = parser;
	}

	/*
	 * Order of Operations Rule 1: first perform any calculation inside
	 * parenthesis Rule 2: perform all mult and divi, working from left to right
	 * Rule 3: perform all addi and subt, working from left to right
	 */

	@Override
	public void exitAssign_stmt(LittleParser.Assign_stmtContext ctx) {
		ParseTree assignTree = ctx.getChild(0);
		List<String> assignList = new ArrayList<>();
		inOrderTraversal(assignTree, assignList);
		assignList = removeSpace(assignList);

		// ORDER OF OPERATION

		// CHECKING IF THE ASSIGNMENT HAS A LENGTH OF 3 WHICH MEANS a := digit
		if (assignList.size() == 3) {
			simpleAssign(assignList);
		} else {
			computeAssgnExpr(assignList);
		}

		// printArray(assignExprArray);
	}

	public void computeAssgnExpr(List<String> input) {
		List<String> stuff = new ArrayList<String>();

		stuff = paren(input);
		// stuff = mulDiv(stuff);
		// stuff = addSub(stuff);
		simpleAssign2(stuff);
	}

	public List<String> paren(List<String> input) {
		int index = 0;
		List<String> currentList = input;
		String ptrOP;

		for (int k = 0; k < currentList.size(); k++) {
			ptrOP = currentList.get(k);
			if (ptrOP.equals("(")) {
				index = k;
			} else if (ptrOP.equals(")")) {
				List<String> newList = new ArrayList<String>();
				for (int i = index + 1; i < k; i++) {
					newList.add(currentList.get(i));
				}
				currentList.remove(k);
				currentList.remove(index);
				k = k - 2;
				newList = paren(newList);
				for (int j = 0; j < newList.size(); j++) {
					currentList.add(j, newList.get(j));
				}
			}
		}
		currentList = mulDiv(currentList);
		currentList = addSub(currentList);

		return currentList;
	}

	public List<String> param(List<String> input) {
		int index = 0;
		int numOfParam = 0;
		List<String> currentList = input;
		List<String> exprOP = new ArrayList<>();
		String ptrOP;
		String opResult;

		for (int k = 0; k < currentList.size(); k++) {
			ptrOP = currentList.get(k);
			// COUNTING HOW MANY OPEN PARAM
			if (ptrOP.equals("(")) {
				if (numOfParam == 0) {
					index = k;
				}
				numOfParam++;

			} else if (ptrOP.equals(")")) {
				numOfParam--;
				if (numOfParam == 0) {
					List<String> newList = new ArrayList<String>();
					for (int i = index + 1; i < k; i++) {
						newList.add(currentList.get(i));
					}
					currentList.remove(index);
					currentList.remove(k);
					param(newList);
				}
			}
		}
		input = mulDiv(currentList);
		input = addSub(input);

		return input;
	}

	public List<String> mulDiv(List<String> input) {
		List<String> currentList = input;
		List<String> exprOP = new ArrayList<>();
		String ptrOP;
		String opResult;

		for (int k = 0; k < currentList.size(); k++) {
			ptrOP = currentList.get(k);
			// CHECKING FOR AN ASSIGN OPERATION
			if (ptrOP.equals("*")) {
				k--;
				// System.out.println(currentList.toString());
				exprOP.add(currentList.remove(k)); // first
				currentList.remove(k);
				exprOP.add(currentList.remove(k)); // second
				// System.out.println(currentList.toString());

				// System.out.println(currentList.toString());

				// System.out.println(currentList.toString());
				opResult = multOp(exprOP);
				exprOP = new ArrayList<>(); // reset
				currentList.add(k, opResult);
				// System.out.println(currentList.toString());
				// mulDiv(currentList);
			}
			if (ptrOP.equals("/")) {
				k--;
				exprOP.add(currentList.remove(k)); // first
				currentList.remove(k);
				exprOP.add(currentList.remove(k)); // second
				opResult = divOp(exprOP);
				exprOP = new ArrayList<>(); // reset
				currentList.add(k, opResult);
				// addSub(currentList);
			}
		}
		return currentList;
	}

	public List<String> addSub(List<String> input) {
		List<String> currentList = input;
		List<String> exprOP = new ArrayList<>();
		String ptrOP;
		String opResult;

		for (int k = 0; k < currentList.size(); k++) {
			ptrOP = currentList.get(k);
			// CHECKING FOR AN ASSIGN OPERATION
			if (ptrOP.equals("+")) {
				k--;
				exprOP.add(currentList.remove(k)); // first
				currentList.remove(k);
				exprOP.add(currentList.remove(k)); // second

				opResult = addOp(exprOP);
				exprOP = new ArrayList<>(); // reset
				currentList.add(k, opResult);
				// addSub(currentList);
			}
			if (ptrOP.equals("-")) {
				k--;
				exprOP.add(currentList.remove(k)); // first
				currentList.remove(k);
				exprOP.add(currentList.remove(k)); // second
				opResult = addOp(exprOP);
				exprOP = new ArrayList<>(); // reset
				currentList.add(k, opResult);
				// addSub(currentList);
			}
		}
		return currentList;
	}

	public String multOp(List<String> input) {
		HashMap<String, String> currScope = peek();
		String type1 = currScope.get(input.get(0));
		String type2 = currScope.get(input.get(1));
		System.out.println("move " + input.get(0) + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("muli " + input.get(1) + " " + register.concat(String.valueOf(regisNum)));
		regisNum++;
		return register.concat(String.valueOf(regisNum - 1));
	}

	public String divOp(List<String> input) {
		System.out.println("move " + input.get(0) + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("divr " + input.get(1) + " " + register.concat(String.valueOf(regisNum)));
		regisNum++;
		return register.concat(String.valueOf(regisNum - 1));
	}

	public String addOp(List<String> input) {
		System.out.println("move " + input.get(0) + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("addi " + input.get(1) + " " + register.concat(String.valueOf(regisNum)));
		regisNum++;
		return register.concat(String.valueOf(regisNum - 1));
	}

	public String subOp(List<String> input) {
		System.out.println("move " + input.get(0) + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("subi " + input.get(1) + " " + register.concat(String.valueOf(regisNum)));
		regisNum++;
		return register.concat(String.valueOf(regisNum - 1));
	}

	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) {
		if (ctx.getChild(0) != null) {
			block_num++;
			HashMap<String, String> elsest = new HashMap<>();
			push(elsest);
			
			System.out.println("jmp " + label.concat(String.valueOf(labelNum)));
			System.out.println("label " + label.concat(String.valueOf(labelNum - 2)));
			labelNum++;
		}
	}
	
	@Override
	public void exitElse_part(LittleParser.Else_partContext ctx) {
		if (ctx.getChild(0) != null) {
			pop();
		}
	}

	@Override
	public void enterFunc_decl(LittleParser.Func_declContext ctx) {
		HashMap<String, String> fst = new HashMap<>();
		push(fst);
		
		String main = ctx.getChild(2).getText();
		if (ctx.getChild(2).getText().equals("main")) {
			System.out.println("label " + main);
		}
	}
	
	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx) {
		pop();
	}

	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
		block_num++;
		HashMap<String, String> ifst = new HashMap<>();
		push(ifst);
		
		String id = ctx.getChild(2).getChild(0).getText();
		String comparison = ctx.getChild(2).getChild(1).getText();
		String comparisonValue = ctx.getChild(2).getChild(2).getText();

		// IF (diff > (0.0 - tolerance))
		// System.out.println(ctx.getChild(2).getText().length());
		if (ctx.getChild(2).getText().length() >= 20) {
			// System.out.println("THING " +
			// ctx.getChild(2).getChild(2).getText());
			System.out.println("move 0.0 " + register.concat(String.valueOf(regisNum)));
			System.out.println("subr tolerance " + register.concat(String.valueOf(regisNum)));
			regisNum++;
			
			comparisonValue = register.concat(String.valueOf(regisNum - 1));

			System.out.println("cmpr " + id + " " + register.concat(String.valueOf(regisNum - 1)));
			regisNum++;
		}

		// this will print out the if statement boolean expression
		if (ctx.getChild(2).getText().length() < 20) {
			System.out.println("move " + comparisonValue + " " + register.concat(String.valueOf(regisNum)));
			System.out.println("cmpi " + id + " " + register.concat(String.valueOf(regisNum)));
			regisNum++;
		}
		if (comparison.equals(">")) {
			System.out.println("jle " + " " + label.concat(String.valueOf(labelNum)));
			endIfStmtList.add(label.concat(String.valueOf(labelNum)));
			labelNum++;
		} else if (comparison.equals("=")) {
			System.out.println("jne " + " " + label.concat(String.valueOf(labelNum)));
			endIfStmtList.add(label.concat(String.valueOf(labelNum)));
			labelNum++;
		} else if (comparison.equals("<")) {
			System.out.println("jge " + " " + label.concat(String.valueOf(labelNum)));
			endIfStmtList.add(label.concat(String.valueOf(labelNum)));
			labelNum++;
		}
	}
	
	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
		pop();
	}

	@Override
	public void exitIf_stmt_end(LittleParser.If_stmt_endContext ctx) {
		int labelSize = endIfStmtList.size() - 1;
		String prevLabel = endIfStmtList.remove(labelSize);
		System.out.println("label " + prevLabel);
	}
	
	@Override
	public void enterParam_decl(LittleParser.Param_declContext ctx) {
		HashMap<String, String> currScope = peek();
		String id = ctx.getChild(1).getText();
		if (currScope.containsKey(id)) {
			error = true;
			errors.add(id);
			// System.out.print("\nDECLARATION ERROR " + id);
		} else {
			String type = ctx.getChild(0).getText();
			currScope.put(id, type);
			// System.out.print("\nname " + id + " type " + type);
		}
	}
	
	@Override
	public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
		// System.out.print("Symbol table GLOBAL");
		HashMap<String, String> gst = new HashMap<>();
		push(gst);
	}
	
	@Override
	public void exitPgm_body(LittleParser.Pgm_bodyContext ctx) {
		pop();
	}

	@Override
	public void exitProgram_end(LittleParser.Program_endContext ctx) {
		System.out.println("sys halt");
	}

	@Override
	public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
		// STEP 4
		String readInput = ctx.getChild(2).getText();
		String[] readArg = readInput.split(",");

		// reading only one input
		if (readArg.length == 1) {
			if (ctx.getChild(0) != null) {
				System.out.println("sys readi " + readArg[0]);
			}
		}

		// reading multiple inputs
		if (readArg.length >= 2) {
			for (int n = 0; n < readArg.length; n++) {
				System.out.println("sys readi " + readArg[n]);
			}
		}

	}

	@Override
	public void exitString_decl(LittleParser.String_declContext ctx) {
		HashMap<String, String> currScope = peek();
		String id = ctx.getChild(1).getText();
		if (currScope.containsKey(id)) {
			error = true;
			errors.add(id);
			// System.out.print("\nDECLARATION ERROR " + id);
		} else {
			String val = ctx.getChild(3).getText();
			currScope.put(id, "STRING");
			// System.out.print("\nname " + id + " type STRING value " + val);
		}
		
		String strDecl = ctx.getChild(1).getText();
		String[] varDeclaration = strDecl.split(",");
		String value = ctx.getChild(3).getText();

		// single variable declaration
		if (varDeclaration.length == 1) {
			System.out.println("str " + strDecl + " " + value);
		}
		// multiple variable declarations
		if (varDeclaration.length >= 2) {
			for (int k = 0; k <= varDeclaration.length - 1; k++) {
				System.out.println("str " + varDeclaration[k] + " " + value);
			}
		}
	}

	@Override
	public void exitVar_decl(LittleParser.Var_declContext ctx) {
		HashMap<String, String> currScope = peek();
		String id = ctx.getChild(1).getText();
		// System.out.println(ctx.getChild(1).getText());
		if (currScope.containsKey(id)) { // checking for duplicate
			error = true;
			errors.add(id);
			// System.out.print("\nDECLARATION ERROR " + id);
		} else {
			String type = ctx.getChild(0).getText();
			String[] mulVar = id.split(",");
			if (mulVar.length >= 1) { // multiple variable declaration
				for (int i = 0; i < mulVar.length; i++) {
					currScope.put(mulVar[i], type);
					// System.out.println(mulVar[i] + " " + type);
				}
			} else { // only a single variable declaration
				currScope.put(id, type);
			}
		}
		
		String varDecl = ctx.getChild(1).getText();
		String[] varDeclaration = varDecl.split(",");

		// single variable declaration
		if (varDeclaration.length == 1) {
			System.out.println("var " + varDecl);
		}
		// multiple variable declarations
		if (varDeclaration.length >= 2) {
			for (int k = 0; k <= varDeclaration.length - 1; k++) {
				System.out.println("var " + varDeclaration[k]);
			}
		}
	}

	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
		block_num++;
		HashMap<String, String> wst = new HashMap<>();
		push(wst);
		
		System.out.println("label " + label.concat(String.valueOf(labelNum)));
		labelNum++;

		String id = ctx.getChild(2).getChild(0).getText();
		String condition = ctx.getChild(2).getChild(1).getText();
		String value = ctx.getChild(2).getChild(2).getText();

		System.out.println("move " + value + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("cmpri " + id + " " + register.concat(String.valueOf(regisNum)));
		regisNum++;

		if (condition.equals("!=")) {
			System.out.println("jeq " + label.concat(String.valueOf(labelNum)));
			whileEndNum = labelNum;
			labelNum++;
		} else if (condition.equals("<=")) {
			System.out.println("jgt " + label.concat(String.valueOf(labelNum)));
			whileEndNum = labelNum;
			labelNum++;
		}
	}
	
	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
		pop();
	}

	@Override
	public void exitWhile_stmt_end(LittleParser.While_stmt_endContext ctx) {
		System.out.println("jmp " + label.concat(String.valueOf(whileEndNum - 1)));
		System.out.println("label " + label.concat(String.valueOf(whileEndNum)));
	}

	@Override
	public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
		String writeInput = ctx.getChild(2).getText();
		String[] writeArg = writeInput.split(",");

		// single write argument
		if (writeArg.length == 1) {
			System.out.println("sys writei " + writeArg[0]);
		}
		// multiple write argument
		if (writeArg.length >= 2) {
			for (int i = 0; i < writeArg.length; i = i + 2) {
				System.out.println("sys writei " + writeArg[i]);
				System.out.println("sys writes " + writeArg[i + 1]);
			}
		}
	}

	// MISALENIOUS CODES
	public void printArray(String[] input) {
		for (int k = 0; k <= input.length - 1; k++) {
			System.out.println(input[k]);
		}
	}

	public List<String> removeSpace(List<String> input) {
		int i = 0;
		List<String> temp = new ArrayList<>();
		while (i <= input.size() - 1) {
			if (!input.get(i).isEmpty()) {
				temp.add(input.get(i));
				i++;
			} else {
				i++;
			}
		}
		return temp;
	}

	public void inOrderTraversal(ParseTree inputTree, List<String> inputArray) {
		// Do I have children?
		int i = 0;
		while (inputTree.getChild(i) != null) {
			// yes
			// do they have children?
			inOrderTraversal(inputTree.getChild(i), inputArray);
			i++; // how many more?
		}
		if (i == 0) {
			// no
			// print myself
			inputArray.add(inputTree.getText());
		}
	}

	public void simpleAssign(List<String> input) {
		// System.out.println(assignList.toString());
		System.out.println("move " + input.get(2) + " " + register.concat(String.valueOf(regisNum)));
		System.out.println("move " + register.concat(String.valueOf(regisNum)) + " " + input.get(0));
		regisNum++;
	}

	public void simpleAssign2(List<String> input) {
		System.out.println("move " + input.get(2) + " " + input.get(0));
	}
	
	// STEP 3 METHODS
	public void push(HashMap<String, String> s) {
		tableStack.add(s);
	}

	public void pop() {
		int size = tableStack.size();
		tableStack.remove(size - 1);
	}

	public HashMap<String, String> peek() {
		int size = tableStack.size();
		HashMap<String, String> curr = tableStack.get(size - 1);
		return curr;
	}

	public boolean error() {
		return error;
	}
}
