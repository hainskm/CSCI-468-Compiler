package src;

/*
 * @author Josh McCleary and Jonathan Johnson
 * CSCI 468 Compilers
 */

public class IRNode {
	String opCode;
	String firstOp;
	String secondOp;
	String result;
	IRNode next = null;
	IRNode prev = null;

	public IRNode(String a, String b, String c, String d) {
		this.opCode = a;
		this.firstOp = b;
		this.secondOp = c;
		this.result = d;
	}

	public void setNext(IRNode input) {
		this.next = input;
	}

	public void setPrev(IRNode input) {
		this.prev = input;
	}

	public void print() {
		if (this.opCode.equals("LINK")) {
			System.out.println(";" + opCode);
		} else if (this.opCode.equals("RET")) {
			System.out.println(";" + opCode);
			System.out.println(";tiny code");
		} else {
			System.out.print(";" + opCode + " ");
			if (this.firstOp != null) {
				System.out.print(firstOp + " ");
			}
			if (this.secondOp != null) {
				System.out.print(secondOp + " ");
			}
			System.out.print(result + "\n");
		}
	}

}
