package VEW.XMLCompiler.ASTNodes;

import VEW.Planktonica2.Model.Catagory;
import VEW.Planktonica2.Model.Type;
import VEW.Planktonica2.Model.VarietyType;


public class IfRuleNode extends RuleNode {
	
	private BExprNode conditionExpr;
	private RuleNode rule;
	
	public IfRuleNode(BExprNode conditionExpr, RuleNode rule) {
		this.conditionExpr = conditionExpr;
		this.rule = rule;
	}
	
	@Override
	public void check(Catagory enclosingCategory, ConstructedASTree enclosingTree) {
		conditionExpr.check(enclosingCategory, enclosingTree);
		Type condType = conditionExpr.getBExprType();
		if (condType instanceof VarietyType) {
			enclosingTree.addSemanticException(
					new SemanticCheckException("The condition must evaluate to a boolean",line_number));
		}
		rule.setInsideConditional(true);
		rule.check(enclosingCategory, enclosingTree);
	}

	@Override
	public String generateXML() {
		return "\\ifthen{" + conditionExpr.generateXML() + "," + rule.generateXML() + "}";
	}

	@Override
	public String generateLatex() {
		String condition = "???";
		if (conditionExpr != null)
			condition = conditionExpr.generateLatex();
		String result = "???";
		if (rule != null)
			result = rule.generateLatex();
		return "if\\;(" + condition + ")\\;then\\;(" + result + ")";
	}

	
	@Override
	public void acceptDependencyCheckVisitor(ASTreeVisitor visitor) {
		super.acceptDependencyCheckVisitor(visitor);
		
		
		conditionExpr.acceptDependencyCheckVisitor(visitor);
		rule.acceptDependencyCheckVisitor(visitor);
		visitor.visit(this);
		
	}
	
}
