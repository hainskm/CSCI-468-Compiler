package src;

import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

/*
 * @author Josh McCleary, Jonathan Johnson, and Kevin Hainsworth
 * @since 25042016-1755
 * CSCI 468 Compilers
 */

public class ExtractInterfaceListener extends LittleBaseListener {
	// STEP 3 VARIBALES
	LittleParser parser;
	int block_num = 1;
	ArrayList<HashMap<String, String>> tableStack = new ArrayList<>();
	private boolean error = false;
	ArrayList<String> errors = new ArrayList<>();

	// STEP 4 VARIBALES
	ArrayList<IRNode> listenerIRList = new ArrayList<>();
	ArrayList<IRNode> listenerIRGraph = new ArrayList<>();
	IRNode temp;
	String $T = "$T";
	int T = 1;
	String label = "label";
	int labelNum = 1;
	int endWhileNum = 0;

	public ExtractInterfaceListener(LittleParser parser) {
		this.parser = parser;
	}

	/** Listen to matches of variable declarations */
	@Override
	public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
	}

	@Override
	public void exitAssign_stmt(LittleParser.Assign_stmtContext ctx) {
		// STEP 4
		HashMap<String, String> currScope = peek();
		// System.out.println(currScope);

		String assignExp = ctx.getChild(0).getChild(2).getText();
		String id = ctx.getChild(0).getChild(0).getText();
		String type = currScope.get(id);
		// System.out.println(type);

		if (ctx.getChild(0) != null) {
			// this is where the c = 0 assignment
			if (assignExp.length() == 1) {
				String value = assignExp;

				// System.out.println(type + " " + id);
				// checking the type
				if (type == "INT") {
					temp = new IRNode("STOREI", value, null, $T.concat(String.valueOf(T)));
					listenerIRList.add(temp);
					temp = new IRNode("STOREI", $T.concat(String.valueOf(T)), null, id);
					listenerIRList.add(temp);
					T++;
				} else {
					temp = new IRNode("STOREF", value, null, $T.concat(String.valueOf(T)));
					listenerIRList.add(temp);
					temp = new IRNode("STOREF", $T.concat(String.valueOf(T)), null, id);
					listenerIRList.add(temp);
					T++;
				}
			} else if (assignExp.length() > 1) {
				// this is where the c = c (+ XOR *) 0 assignment
				String[] abc = new String[3];
				// System.out.println(stuff);
				abc = assignExp.split("");

				// checking what operation it has
				if (abc[1] == "+") {
					temp = new IRNode("ADDI", id, $T.concat(String.valueOf(T)), $T.concat(String.valueOf(T + 1)));
					// if (currScope.get(id) == "INT") {
					temp = new IRNode("STOREI", abc[2], null, $T.concat(String.valueOf(T)));
					listenerIRList.add(temp);
					listenerIRList.add(temp);
					T++;
					temp = new IRNode("STOREI", $T.concat(String.valueOf(T)), null, id);
					listenerIRList.add(temp);
					T++;
					// }
					/*
					 * else if (currScope.get(id) == "FLOAT") { temp = new
					 * IRNode("ADDF", var1, var2, $T.concat(String.valueOf(T)));
					 * listenerIRList.add(temp); temp = new IRNode("STOREF",
					 * $T.concat(String.valueOf(T)), null, id);
					 * listenerIRList.add(temp); T++; }
					 */
				} else if (abc[1] == "*") {
					temp = new IRNode("STOREI", abc[2], null, $T.concat(String.valueOf(T)));
					listenerIRList.add(temp);
					temp = new IRNode("MULTI", id, $T.concat(String.valueOf(T)), $T.concat(String.valueOf(T + 1)));
					listenerIRList.add(temp);
					T++;
					temp = new IRNode("STOREI", $T.concat(String.valueOf(T)), null, id);
					listenerIRList.add(temp);
					T++;
				}
			}
		}
	}

	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) {
		if (ctx.getChild(0) != null) {
			// System.out.println();
			// System.out.print("\nSymbol table BLOCK " + block_num);
			block_num++;
			HashMap<String, String> elsest = new HashMap<>();
			push(elsest);
		}

		// STEP 4
		// System.out.println(ctx.getChild(0).getText());
		if (ctx.getChild(0) != null) {
			temp = new IRNode("JUMP", null, null, label.concat(String.valueOf(labelNum + 1)));
			listenerIRList.add(temp);
			temp = new IRNode("LABEL", null, null, label.concat(String.valueOf(labelNum)));
			listenerIRList.add(temp);
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
		// System.out.println();
		// System.out.print("\nSymbol table " + ctx.getChild(2).getText());
		HashMap<String, String> fst = new HashMap<>();
		push(fst);

		// STEP 4
		String main = ctx.getChild(2).getText();
		if (ctx.getChild(2).getText() == "main") {
			temp = new IRNode("LABEL", null, null, main);
			listenerIRList.add(temp);
			temp = new IRNode("LINK", null, null, null);
			listenerIRList.add(temp);
		}
	}

	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx) {
		pop();
	}

	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
		// System.out.println();
		// System.out.print("\nSymbol table BLOCK " + block_num);
		block_num++;
		HashMap<String, String> ifst = new HashMap<>();
		push(ifst);

		// STEP 4
		// System.out.println(ctx.getChild(7).getText());
		String id = ctx.getChild(2).getChild(0).getText();
		String comparison = ctx.getChild(2).getChild(1).getText();
		String comparisonValue = ctx.getChild(2).getChild(2).getText();
		// IF
		if (ctx.getChild(0) != null) {
			// storing comparisonValue to temporary register
			temp = new IRNode("STOREI", comparisonValue, null, $T.concat(String.valueOf(T)));
			listenerIRList.add(temp);
			labelNum++;
			if (comparison == ">") {
				temp = new IRNode("LEI", id, $T.concat(String.valueOf(T)), label.concat(String.valueOf(labelNum)));
				listenerIRList.add(temp);
			} else if (comparison == "=") {
				temp = new IRNode("NEI", id, $T.concat(String.valueOf(T)), label.concat(String.valueOf(labelNum)));
				listenerIRList.add(temp);
			}
			T++;
		}
	}

	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
		pop();
	}

	@Override
	public void exitIf_stmt_end(LittleParser.If_stmt_endContext ctx) {
		// STEP 4
		// ENDIF label creation
		if (ctx.getChild(0).getText() == "ENDIF") {
			temp = new IRNode("LABEL", null, null, label.concat(String.valueOf(labelNum)));
			listenerIRList.add(temp);
		}
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

		// STEP 4
		System.out.println(";IR code");
	}

	@Override
	public void exitPgm_body(LittleParser.Pgm_bodyContext ctx) {
		pop();
	}

	@Override
	public void exitProgram_end(LittleParser.Program_endContext ctx) {
		// STEP 4
		temp = new IRNode("RET", null, null, null);
		listenerIRList.add(temp);
	}

	@Override
	public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
		// STEP 4
		String readInput = ctx.getChild(2).getText();
		String[] readArg = readInput.split(",");

		// reading only one input
		if (readInput.length() == 1) {
			String whatYoureReading = ctx.getChild(2).getText();
			if (ctx.getChild(0) != null) {
				temp = new IRNode("READI", null, null, whatYoureReading);
				listenerIRList.add(temp);
			}
		} else if (readInput.length() >= 2) {   // reading multiple inputs
			for (int n = 0; n < readArg.length; n++) {
				temp = new IRNode("READI", null, null, readArg[n]);
				listenerIRList.add(temp);
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
			// System.out.print("\nname " + id + " type " + type);
			// WHAT IS THIS FOR?
			/*
			 * ParseTree pt = ctx.getChild(1).getChild(1);
			 * System.out.println(pt.getText()); while (pt.getChild(0) != null)
			 * { String id2 = pt.getChild(1).getText(); if
			 * (currScope.containsKey(id2)) { error = true; errors.add(id2); //
			 * System.out.print("\nDECLARATION ERROR " + id); } else { // String
			 * type2 = ctx.getChild(0).getText(); currScope.put(id, type); //
			 * System.out.println(id + " " + type); // System.out.print(
			 * "\nname " + id2 + " type " + type2); pt = pt.getChild(2); } }
			 */
		}
	}

	@Override
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
		// System.out.println();
		// System.out.print("\nSymbol table BLOCK " + block_num);
		block_num++;
		HashMap<String, String> wst = new HashMap<>();
		push(wst);

		// STEP 4
		// System.out.println("This is where your at " +
		// ctx.getChild(6).getText());
		String loop = ctx.getChild(0).getText();
		if (ctx.getChild(0) != null) {
			// generating the LABEL and label#
			if (loop == "WHILE") {
				temp = new IRNode("LABEL", null, null, label.concat(String.valueOf(labelNum)));
				listenerIRList.add(temp);
				labelNum++;
				endWhileNum = labelNum;
			}
			// getting the conditional statement for the while loop
			String id = ctx.getChild(2).getChild(0).getText();
			String condition = ctx.getChild(2).getChild(1).getText();
			String conditionalValue = ctx.getChild(2).getChild(2).getText();
			// storing the ( variable comparison number ) stuff
			temp = new IRNode("STOREI", conditionalValue, null, $T.concat(String.valueOf(T)));
			listenerIRList.add(temp);
			// not increasing the $T# because we want the current state
			// conditional IRNodes for the stuff
			if (condition == "!=") {
				temp = new IRNode("EQI", id, $T.concat(String.valueOf(T)), label.concat(String.valueOf(labelNum)));
				listenerIRList.add(temp);
			} else if (condition == "<=") {
				temp = new IRNode("GTI", id, $T.concat(String.valueOf(T)), label.concat(String.valueOf(labelNum)));
				listenerIRList.add(temp);
			}
			// incrementing the T number
			T++;
		}
	}

	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
		pop();
	}

	@Override
	public void exitWhile_stmt_end(LittleParser.While_stmt_endContext ctx) {
		// STEP 4
		// ENDWHILE label creation
		if (ctx.getChild(0).getText() == "ENDWHILE") {
			temp = new IRNode("LABEL", null, null, label.concat(String.valueOf(endWhileNum)));
			listenerIRList.add(temp);
		}
	}

	@Override
	public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
		String writeInput = ctx.getChild(2).getText();
		String[] writeArg = writeInput.split(",");

		if (writeArg.length == 1) {
			String whatYourWritingAt = ctx.getChild(2).getChild(0).getText();
			String newLine = "newline";
			if (ctx.getChild(0) != null) {
				temp = new IRNode("WRITEI", null, null, whatYourWritingAt);
				listenerIRList.add(temp);
				temp = new IRNode("WRITES", null, null, newLine);
				listenerIRList.add(temp);
			}
		} else if (writeArg.length >= 2) {
			for (int i = 0; i < writeArg.length; i = i + 2) {
				temp = new IRNode("WRITEI", null, null, writeArg[i]);
				listenerIRList.add(temp);
				temp = new IRNode("WRITES", null, null, writeArg[i + 1]);
				listenerIRList.add(temp);
			}
		}
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

	// STEP 4 METHODS
	public ArrayList<IRNode> getIRList() {
		return listenerIRList;
	}
}
