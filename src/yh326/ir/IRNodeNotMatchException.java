package yh326.ir;

import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class IRNodeNotMatchException extends Exception {
	public IRNodeNotMatchException(IRNode e) {
		super(e.toString());
	}
}