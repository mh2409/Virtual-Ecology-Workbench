package VEW.XMLCompiler.ANTLR;

public class BinaryFunctionNode extends ASTree implements RuleNode {

	private BinaryFunction binFunc;
	private IdNode idArg;
	private ExprNode expArg;
	
	public BinaryFunctionNode(String funcName, IdNode idArg, ExprNode expArg) {
		this.binFunc = BinaryFunction.valueOf(funcName);
		this.idArg = idArg;
		this.expArg = expArg;
	}
	
	@Override
	public void check() {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateXML() {
		// TODO Auto-generated method stub
		return null;
	}

}