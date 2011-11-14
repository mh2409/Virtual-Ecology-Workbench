package VEW.XMLCompiler.ASTNodes;

public class IngestNode extends RuleNode {
	
	private IdNode identifier;
	private ExprNode threshold;
	private ExprNode rate;
	
	public IngestNode(IdNode _identifier, ExprNode _threshold, ExprNode _rate) {
		this.identifier = _identifier;
		this.threshold = _threshold;
		this.rate = _rate;
	}
	
	@Override
	public void check() throws SemanticCheckException {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateXML() {
		return "\\ingest{" + identifier.generateXML() + "," + threshold.generateXML() + ","
		  	+ rate.generateXML() + "}";
	}
	
	public String generateLatex() {
		String id = "???";
		if (identifier != null)
			id = identifier.generateLatex();
		String thresh = "???";
		if (threshold != null)
			thresh = threshold.generateLatex();
		String rp = "???";
		if (rate != null)
			rp = rate.generateLatex();
		return " ingest(" + id + "," + thresh + " , " + rp + ")";
	}

}
