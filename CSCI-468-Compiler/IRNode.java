package src;

public class IRNode {
	String opCode;
	String firstOp;
	String secondOp;
	String result;
	IRNode next = null;
	IRNode prev = null;
	
	public IRNode (String a, String b, String c, String d){
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
}
