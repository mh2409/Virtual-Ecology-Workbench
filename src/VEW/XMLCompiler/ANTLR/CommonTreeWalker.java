package VEW.XMLCompiler.ANTLR;

import java.util.List;

import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;

import org.antlr.runtime.Token;

import VEW.XMLCompiler.ANTLR.output.BACONParser;

//Or CommonEnt... At least according to Mike
/**
 * This class generates the custom built ASTNodes in VEW.XML.Compiler.ANTLR out of a CommonTree, generated by the ANTLR Parser/Lexer
 * 
 * @author David Coulden, Chris Bates
 *
 */
public class CommonTreeWalker {

	private CommonTree antlrTree;
	
	/**
	 * 
	 * @param antlrTree the ANTLR CommonTree to be walked
	 */
	public CommonTreeWalker(CommonTree antlrTree) {
		this.antlrTree = antlrTree;
	}
	
	/**
	 * Walks through the CommonTree generating an exact copy but in ASTree form
	 * 
	 * @return the root node of the new ASTree
	 * @throws TreeWalkerException only occurs if the parser has let the incorrect input through
	 */
	public ASTree constructASTree() throws TreeWalkerException{
		RuleSequenceNode constructedTree = null;
		// needs to be removed if ASTrees are not constructed from a base
		List<?> childRules = antlrTree.getChildren();
		RuleSequenceNode currentSeq = null;
		for (Object c : childRules) {
			CommonTree child = (CommonTree) c;
			checkNode(child);
			Token childToken = child.getToken();
			if (childToken.getType() != BACONParser.RULE) {
				throw new TreeWalkerException("Expected a rule token");
			}
			RuleSequenceNode ruleSeq = constructRuleSeqNode(child);
			if (currentSeq != null) {
				currentSeq.setRuleSequence(ruleSeq);
				currentSeq = ruleSeq;
			}
			else {
				currentSeq = ruleSeq;
				constructedTree = ruleSeq;
			}
		}
		return constructedTree;
	}

	/*
	 * Each of these are individual rules for constructing different types of ASTreeNodes 
	 */
	private RuleSequenceNode constructRuleSeqNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		if (tree.getChildCount() == 1) {
			RuleNode rule = constructRuleNode((CommonTree) tree.getChild(0));
			return new RuleSequenceNode(rule);
		}
		CommonTree ruleNameNode = (CommonTree) tree.getChild(0);
		String ruleName = ruleNameNode.getToken().getText();
		RuleNode rule = constructRuleNode((CommonTree) tree.getChild(1));
		return new RuleSequenceNode(ruleName, rule);
	}
	
	private RuleNode constructRuleNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		Token token = tree.getToken();
		RuleNode rule = null;
		int tokenType = token.getType();
		switch (tokenType) {
			case(BACONParser.IF) : {
				BExprNode bexpr = constructBExprNode((CommonTree)tree.getChild(0));
				RuleNode thenRule = constructSubRuleNode((CommonTree)tree.getChild(1));
				rule = new IfRuleNode(bexpr, thenRule);
				break;
			}
			case(BACONParser.ASSIGN) : {
				rule = constructAssignNode(tree);
				break;
			}
			case(BACONParser.DIVIDE) : {
				ExprNode expr = constructExprNode((CommonTree)tree.getChild(0));
		        rule = new UnaryFunctionExprNode(UnaryExprFunction.DIVIDE, expr);
				break;
			}
			case(BACONParser.CHANGE) : {
				IdNode id = constructIdNode((CommonTree)tree.getChild(0));
				rule = new UnaryFunctionRuleNode(UnaryRuleFunction.CHANGE, id);
				break;
			}
			case(BACONParser.UPTAKE) : {
				rule = constructBinFuncNode(BinaryFunction.UPTAKE, tree);
				break;
			}
			case(BACONParser.RELEASE) : {
				rule = constructBinFuncNode(BinaryFunction.RELEASE, tree);
				break;
			}
			case(BACONParser.PCHANGE) : {
				rule = constructBinFuncNode(BinaryFunction.PCHANGE, tree);
				break;
			}
			case(BACONParser.INGEST) : {
				IdNode var = constructIdNode((CommonTree)tree.getChild(0));
				ExprNode thresholdExpr = constructExprNode((CommonTree)tree.getChild(1));
				ExprNode rateExpr = constructExprNode((CommonTree)tree.getChild(2));
				rule = new IngestNode(var, thresholdExpr, rateExpr);
				break;
			}
			case(BACONParser.CREATE) : {
				IdNode id = constructIdNode((CommonTree)tree.getChild(0));
				ExprNode expr = constructExprNode((CommonTree)tree.getChild(1));
				AssignListNode assList = constructAssignListNode((CommonTree)tree.getChild(2));
				rule = new CreateNode(id, expr, assList);
				break;
			}
			default : {
				throw new TreeWalkerException("Unknown rule symbol found");
			}
									  
		}
		return rule;
	}
	
	private RuleNode constructSubRuleNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		return constructRuleNode((CommonTree)tree.getChild(0));
	}
	
	private AssignNode constructAssignNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		IdNode id = constructIdNode((CommonTree)tree.getChild(0));
		ExprNode expr = constructExprNode((CommonTree)tree.getChild(1));
		return new AssignNode(id, expr);
	}

	private AssignListNode constructAssignListNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		if (tree == null) {
			return null;
		}
		List<?> children = tree.getChildren();
		AssignListNode assigns = new AssignListNode();
		for(Object child : children) {
			assigns.addAssign(constructAssignNode((CommonTree) child));
		}
		return assigns;
	}
	
	private BinaryFunctionNode constructBinFuncNode(BinaryFunction binFunc, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		IdNode id = constructIdNode((CommonTree)tree.getChild(0));
		ExprNode expr = constructExprNode((CommonTree)tree.getChild(1));
		return new BinaryFunctionNode(binFunc, id, expr);
	}

	private BExprNode constructBExprNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		Token token = tree.getToken();
		BExprNode bexpr = null;
		int tokenType = token.getType();
		switch (tokenType) {
			case(BACONParser.EQUALS) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.EQUALS, tree);
				break;
			}
			case(BACONParser.NEQUALS) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.NEQUALS, tree);
				break;
			}
			case(BACONParser.GREATEREQUALS) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.GREATEREQUALS, tree);
				break;
			}
			case(BACONParser.LESSTHAN) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.LESSTHAN, tree);
				break;
			}
			case(BACONParser.GREATERTHAN) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.GREATERTHAN, tree);
				break;
			}
			case(BACONParser.LESSEQUALS) : {
				bexpr = constructBooleanCompNode(ComparisonOperator.LESSEQUALS, tree);
				break;
			}
			case(BACONParser.NOT) : {
				BExprNode notBExpr = constructBExprNode((CommonTree)tree.getChild(0));
				bexpr = new BooleanNotOpNode(notBExpr);
				break;
			}
			case(BACONParser.AND) : {
				bexpr = constructBooleanBinOpNode(BooleanBinOperator.AND, tree);
				break;
			}
			case(BACONParser.OR) : {
				bexpr = constructBooleanBinOpNode(BooleanBinOperator.OR, tree);
				break;
			}
			case(BACONParser.ALL) : {
				bexpr = constructVBOpNode(VBoolOperator.ALL, tree);
				break;
			}
			case(BACONParser.NONE) : {
				bexpr = constructVBOpNode(VBoolOperator.NONE, tree);
				break;
			}
			case(BACONParser.SOME) : {
				bexpr = constructVBOpNode(VBoolOperator.SOME, tree);
				break;
			}
			default : {
				throw new TreeWalkerException("Unknown boolean operator found");
			}
		}
		return bexpr;
	}
	
	private BooleanComparitorNode constructBooleanCompNode(ComparisonOperator comp, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
		ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
		return new BooleanComparitorNode(comp, lExpr, rExpr);
	}
	
	private BooleanBinOpNode constructBooleanBinOpNode(BooleanBinOperator binOp, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		BExprNode lBExpr = constructBExprNode((CommonTree)tree.getChild(0));
		BExprNode rBExpr = constructBExprNode((CommonTree)tree.getChild(1));
		return new BooleanBinOpNode(binOp, lBExpr, rBExpr);
	}
	
	private VBOpNode constructVBOpNode(VBoolOperator vBOp, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		BExprNode bExpr = constructBExprNode((CommonTree)tree.getChild(0));
		return new VBOpNode(vBOp, bExpr);
	}

	private ExprNode constructExprNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		Token token = tree.getToken();
		ExprNode expr = null;
		int tokenType = token.getType();
		
		UnaryPrimitive uPrim = SymbolSet.getUnaryPrimitiveOp(tokenType);
		if (uPrim != null) {
			ExprNode primExpr = constructExprNode((CommonTree)tree.getChild(0));
			return new UnaryPrimNode(uPrim, primExpr);
		}
		
		switch (tokenType) {
			case(BACONParser.PLUS) : {
				expr = constructBinOpNode(MathematicalOperator.PLUS, tree);
				break;
			}
			case(BACONParser.MINUS) : {
				expr = constructBinOpNode(MathematicalOperator.MINUS, tree);
				break;
			}
			case(BACONParser.MUL) : {
				expr = constructBinOpNode(MathematicalOperator.MULTIPLY, tree);
				break;
			}
			case(BACONParser.DIV) : {
				expr = constructBinOpNode(MathematicalOperator.DIVIDE, tree);
				break;
			}
			case(BACONParser.POW) : {
				expr = constructBinOpNode(MathematicalOperator.POWER, tree);
				break;
			}
			case(BACONParser.IF) : {
				BExprNode ifCondNode = constructBExprNode((CommonTree)tree.getChild(0));
				ExprNode thenExpr = constructExprNode((CommonTree)tree.getChild(1));
				ExprNode elseExpr = constructExprNode((CommonTree)tree.getChild(2));
				expr = new IfExprNode(ifCondNode, thenExpr, elseExpr);
				break;
			}
			case(BACONParser.VAR) : {
				expr = constructIdNode(tree);
				break;
			}
			case(BACONParser.FLOAT) : {
				expr = constructNumNode(tree);
				break;
			}
			case(BACONParser.MAX) : {
				expr = constructBinPrimNode(BinaryPrimitive.MAX, tree);
				break;
			}
			case(BACONParser.MIN) : {
				expr = constructBinPrimNode(BinaryPrimitive.MIN, tree);
				break;
			}
			case(BACONParser.VARHIST) : {
				IdNode id = constructIdNode((CommonTree)tree.getChild(0));
				ExprNode histExpr = constructExprNode((CommonTree)tree.getChild(1));
				expr = new VarHistNode(id, histExpr);
				break;
			}
			case(BACONParser.VAVERAGE) : {
				expr = constructVBOpNode(VOperator.AVERAGE, tree);
				break;
			}
			case(BACONParser.VPRODUCT) : {
				expr = constructVBOpNode(VOperator.PRODUCT, tree);
				break;
			}
			case(BACONParser.VSUM) : {
				expr = constructVBOpNode(VOperator.SUM, tree);
				break;
			}
			default : {
				throw new TreeWalkerException("Unknown expression token in bagging area");
			}
		}
		return expr;
	}
	
	private BinOpNode constructBinOpNode(MathematicalOperator op, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
		ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
		return new BinOpNode(op, lExpr, rExpr);
	}
	
	private BinaryPrimitiveNode constructBinPrimNode(BinaryPrimitive prim, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
		ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
		return new BinaryPrimitiveNode(prim, lExpr, rExpr);
	}
	
	private VOpNode constructVBOpNode(VOperator vOp, CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		ExprNode expr = constructExprNode((CommonTree)tree.getChild(0));
		return new VOpNode(vOp, expr);
	}

	private IdNode constructIdNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		return new IdNode(tree.getToken().getText());
	}
	
	private NumNode constructNumNode(CommonTree tree) throws TreeWalkerException {
		checkNode(tree);
		String numString = tree.getToken().getText();
		try {
			return new NumNode(Float.parseFloat(numString));
		}
		catch (NumberFormatException n) {
			throw new TreeWalkerException(n.getMessage());
		}
	}
	
	private void checkNode(CommonTree node) throws TreeWalkerException {
		if (node instanceof CommonErrorNode) {
			throw new TreeWalkerException(((CommonErrorNode)node).trappedException.getMessage());
		}
	}
}
