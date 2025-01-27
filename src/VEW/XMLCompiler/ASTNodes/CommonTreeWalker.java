package VEW.XMLCompiler.ASTNodes;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Token;

import VEW.Common.Pair;
import VEW.XMLCompiler.ANTLR.BACONParser;

//Or CommonEnt... At least according to Mike
/**
 * This class generates the custom built ASTNodes in VEW.XML.Compiler.ANTLR out of a CommonTree, generated by the ANTLR Parser/Lexer
 * 
 * @author David Coulden, Chris Bates
 *
 */
public class CommonTreeWalker {
	
	private ArrayList<BACONCompilerException> exceptions;
	
	private CommonTree antlrTree;
	
	/**
	 * 
	 * @param antlrTree the ANTLR CommonTree to be walked
	 */
	public CommonTreeWalker(CommonTree antlrTree) {
		this.antlrTree = antlrTree;
		exceptions = new ArrayList<BACONCompilerException>();
	}
	
	/**
	 * Walks through the CommonTree generating an exact copy but in ASTree form
	 * 
	 * @return the root node of the new ASTree
	 * @throws TreeWalkerException only occurs if the parser has let the incorrect input through
	 */
	public ConstructedASTree constructASTree() {
		RuleSequenceNode constructedTree = null;
		// needs to be removed if ASTrees are not constructed from a base
		if (!checkNode(antlrTree)) {
			return new ConstructedASTree(null, exceptions);
		}
		List<?> childRules = antlrTree.getChildren();
		RuleSequenceNode currentSeq = null;
		for (Object c : childRules) {
			CommonTree child = (CommonTree) c;
			if (checkNode(child)) {
				Token childToken = child.getToken();
				if (childToken.getType() != BACONParser.RULE) {
					// TODO - Sort out line/char pos
					exceptions.add(new TreeWalkerException("Expected a rule token",999,999));
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
		}
		return new ConstructedASTree(constructedTree, exceptions);
	}

	/*
	 * Each of these are individual rules for constructing different types of ASTreeNodes 
	 */
	private RuleSequenceNode constructRuleSeqNode(CommonTree tree) {
		if (checkNode(tree)) {
			if (tree.getChildCount() == 1) {
				RuleNode rule = constructRuleNode((CommonTree) tree.getChild(0));
				return new RuleSequenceNode(rule);
			}
			CommonTree ruleNameNode = (CommonTree) tree.getChild(0);
			String ruleName = ruleNameNode.getToken().getText();
			RuleNode rule = constructRuleNode((CommonTree) tree.getChild(1));
			return new RuleSequenceNode(ruleName, rule);
		}
		return null;
	}
	
	private RuleNode constructRuleNode(CommonTree tree) {
		RuleNode rule = null;
		if (checkNode(tree)) {
			Token token = tree.getToken();
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
					CommonTree firstChild = (CommonTree)tree.getChild(0);
					if (firstChild.getToken().getType() == BACONParser.VAR) {
						ExprNode propExpr;
						IdNode stage;
						if (tree.getChildCount() == 2) {
							CommonTree secondChild = (CommonTree)tree.getChild(1);
							propExpr = constructExprNode(secondChild);
							stage = constructIdNode(firstChild);
						}
						else {
							propExpr = new NumNode(1, token.getLine());
							stage = constructIdNode(firstChild);
						}
						Pair<BExprNode, IdNode> changeStat = new Pair<BExprNode, IdNode>(null, stage);
						ArrayList<Pair<BExprNode, IdNode>> changeStatements = new ArrayList<Pair<BExprNode, IdNode>>();
						changeStatements.add(changeStat);
						rule = new ChangeNode(propExpr, changeStatements);
					}
					else {
						ExprNode propExpr = constructExprNode(firstChild);
						Pair<BExprNode, IdNode> changeStat;
						ArrayList<Pair<BExprNode, IdNode>> changeStatements = new ArrayList<Pair<BExprNode, IdNode>>();
						for (int n = 1; n < tree.getChildCount(); n++) {
							changeStat = constructChangeStatement((CommonTree)tree.getChild(n));
							changeStatements.add(changeStat);
						}
						rule = new ChangeNode(propExpr, changeStatements);
					}
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
				/*case(BACONParser.PCHANGE) : {
					rule = constructBinFuncNode(BinaryFunction.PCHANGE, tree);
					break;
				}*/
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
					// TODO - sort out line
					exceptions.add(new TreeWalkerException("Unknown rule symbol found",999,999));
				}
										  
			}
		}
		return rule;
	}

	private Pair<BExprNode, IdNode> constructChangeStatement(CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		CommonTree bexpr = (CommonTree) tree.getChild(0);
		BExprNode bExprNode = null;
		if (bexpr.getToken().getType() != BACONParser.OTHERWISE) {
			bExprNode = constructBExprNode((CommonTree)tree.getChild(0));
		}
		return new Pair<BExprNode, IdNode>(bExprNode, 
					constructIdNode((CommonTree)tree.getChild(1)));
	}

	private RuleNode constructSubRuleNode(CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		return constructRuleNode((CommonTree)tree.getChild(0));
	}
	
	private AssignNode constructAssignNode(CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		IdNode id = constructIdNode((CommonTree)tree.getChild(0));
		ExprNode expr = constructExprNode((CommonTree)tree.getChild(1));
		return new AssignNode(id, expr,tree.getLine());
	}

	private AssignListNode constructAssignListNode(CommonTree tree) {
		if (tree == null || !checkNode(tree)) {
			return null;
		}
		List<?> children = tree.getChildren();
		AssignListNode assigns = new AssignListNode();
		for(Object child : children) {
			assigns.addAssign(constructAssignNode((CommonTree) child));
		}
		return assigns;
	}
	
	private BinaryFunctionNode constructBinFuncNode(BinaryFunction binFunc, CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		IdNode id = constructIdNode((CommonTree)tree.getChild(0));
		ExprNode expr = constructExprNode((CommonTree)tree.getChild(1));
		return new BinaryFunctionNode(binFunc, id, expr,tree.getLine());
	}

	private BExprNode constructBExprNode(CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
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
				exceptions.add(new TreeWalkerException("Unknown boolean operator found",999,999));
			}
		}
		return bexpr;
	}
	
	private BooleanComparitorNode constructBooleanCompNode(ComparisonOperator comp, CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
		ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
		return new BooleanComparitorNode(comp, lExpr, rExpr,tree.getLine());
	}
	
	private BooleanBinOpNode constructBooleanBinOpNode(BooleanBinOperator binOp, CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		BExprNode lBExpr = constructBExprNode((CommonTree)tree.getChild(0));
		BExprNode rBExpr = constructBExprNode((CommonTree)tree.getChild(1));
		return new BooleanBinOpNode(binOp, lBExpr, rBExpr,tree.getLine());
	}
	
	private VBOpNode constructVBOpNode(VBoolOperator vBOp, CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		BExprNode bExpr = constructBExprNode((CommonTree)tree.getChild(0));
		return new VBOpNode(vBOp, bExpr,tree.getLine());
	}

	private ExprNode constructExprNode(CommonTree tree) {
		if (!checkNode(tree)) {
			return null;
		}
		Token token = tree.getToken();
		ExprNode expr = null;
		int tokenType = token.getType();
		
		UnaryPrimitive uPrim = SymbolSet.getUnaryPrimitiveOp(tokenType);
		if (uPrim != null) {
			ExprNode primExpr = constructExprNode((CommonTree)tree.getChild(0));
			return new UnaryPrimNode(uPrim, primExpr,tree.getLine());
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
			case(BACONParser.NEG) : {
				ExprNode negExpr = constructExprNode((CommonTree)tree.getChild(0));
				expr = new NegNode(negExpr,tree.getLine());
				break;
			}
			case(BACONParser.IF) : {
				BExprNode ifCondNode = constructBExprNode((CommonTree)tree.getChild(0));
				ExprNode thenExpr = constructExprNode((CommonTree)tree.getChild(1));
				ExprNode elseExpr = constructExprNode((CommonTree)tree.getChild(2));
				expr = new IfExprNode(ifCondNode, thenExpr, elseExpr,tree.getLine());
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
				expr = new VarHistNode(id, histExpr,tree.getLine());
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
				exceptions.add(new TreeWalkerException("Unknown expression token in bagging area",999,999));
			}
		}
		return expr;
	}
	
	private BinOpNode constructBinOpNode(MathematicalOperator op, CommonTree tree) {
		if (checkNode(tree)) {
			ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
			ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
			return new BinOpNode(op, lExpr, rExpr,tree.getLine());
		}
		return null;
	}
	
	private BinaryPrimitiveNode constructBinPrimNode(BinaryPrimitive prim, CommonTree tree) {
		if (checkNode(tree)) {
			ExprNode lExpr = constructExprNode((CommonTree)tree.getChild(0));
			ExprNode rExpr = constructExprNode((CommonTree)tree.getChild(1));
			return new BinaryPrimitiveNode(prim, lExpr, rExpr,tree.getLine());
		}
		return null;
	}
	
	private VOpNode constructVBOpNode(VOperator vOp, CommonTree tree) {
		if (checkNode(tree)) {
			ExprNode expr = constructExprNode((CommonTree)tree.getChild(0));
			return new VOpNode(vOp, expr,tree.getLine());
		}
		return null;
	}

	private IdNode constructIdNode(CommonTree tree) {
		if (checkNode(tree)) {
			return new IdNode(tree.getToken().getText(),tree.getLine());
		}
		return null;
	}
	
	private NumNode constructNumNode(CommonTree tree) {
		if (checkNode(tree)) {
			String numString = tree.getToken().getText();
			try {
				return new NumNode(Float.parseFloat(numString),tree.getLine());
			}
			catch (NumberFormatException n) {
				exceptions.add(new TreeWalkerException(n.getMessage(),0,0));
			}
		}
		return null;
	}
	
	private boolean checkNode(CommonTree node) {
		if (node instanceof CommonErrorNode) {
			CommonErrorNode cen = (CommonErrorNode) node;
			if (cen.trappedException instanceof MismatchedTokenException) {
				MismatchedTokenException m = (MismatchedTokenException) cen.trappedException;
				String expecting = BACONParser.getTokenFromType(m.expecting);
				String error_message = "";
				if (m.token.getText().equals("<EOF>"))
					error_message = "Unexpected end of input on line " + cen.trappedException.line + 
					". Expecting " + expecting + ".";
				else if (m.token.getText().contains("\n")) {
					error_message = "Unexpected newline on line " + cen.trappedException.line + 
					". Expecting " + expecting + ".";
				}
				else
					error_message = "Unexpected '" + m.token.getText() + "' on line " +
					cen.trappedException.line + ". Expecting " + expecting + ".";
				exceptions.add(
					new TreeWalkerException(error_message,
							cen.trappedException.line,cen.trappedException.charPositionInLine));
			} else if (cen.trappedException instanceof NoViableAltException) {
				NoViableAltException m = (NoViableAltException) cen.trappedException;
					String error_message = "";
					if (m.token.getText().equals("<EOF>"))
						error_message = "Unexpected end of input on line " + cen.trappedException.line + ".";
					else
						error_message = "Unexpected '" + m.token.getText() + "' found on line " +
						cen.trappedException.line + ".";
					exceptions.add(
						new TreeWalkerException(error_message,
								cen.trappedException.line,cen.trappedException.charPositionInLine));
			} else {
				exceptions.add(
						new TreeWalkerException("Unexpected error encountered",0,0));
			}		
			return false;
		}
		return true;
	}
	
	public void add_exception(BACONCompilerException e) {
		exceptions.add(e);
	}

}
