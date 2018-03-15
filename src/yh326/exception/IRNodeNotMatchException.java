package yh326.exception;

import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class IRNodeNotMatchException extends Exception {
	private static final long serialVersionUID = 1L;

	public IRNodeNotMatchException(IRNode e) {
		super(e.toString());
	}
}