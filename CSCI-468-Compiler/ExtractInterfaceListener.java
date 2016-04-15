package src;

import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;

public class ExtractInterfaceListener extends LittleBaseListener {
	LittleParser parser;

	public ExtractInterfaceListener(LittleParser parser) {
		this.parser = parser;
	}

	/*
	 * This is where the semantic stuff happens
	 */
	// SEMANTIC OPERATION VARIABLES
	ArrayList<IRNode> listenerIRList = new ArrayList<>();
	ArrayList<IRNode> listenerIRGraph = new ArrayList<>();
	IRNode temp;

	@Override
	public void exitAssign_stmt(LittleParser.Assign_stmtContext ctx) {
		if (ctx.getChild(0) != null) {
			//System.out.println("assignment statement number " + ctx.getChildCount());
			//System.out.println(ctx.getChild(0).getText());
			//String id = ctx.getChild(0).getChild(0).getText();
			//System.out.println(id);
			
			//String opp = ctx.getChild(0).getChild(2).getChild(0).getText();
			//System.out.println(opp);
			if (ctx.getChild(0).getChild(2).getChild(0).getChild(0) == null) {
				System.out.println("Stuff");		//this is where the c = 0 assignment
			}
			//if (ctx.getChild(0))
		}
	}

	public ArrayList<IRNode> getIRList() {
		return listenerIRList;
	}

}
