package VEW.XMLCompiler.ASTNodes;

import java.util.ArrayList;
import java.util.List;

import VEW.Common.XML.XMLTag;
import VEW.Planktonica2.Model.Catagory;
import VEW.XMLCompiler.DependencyChecker.HasDependency;

public class ConstructedASTree implements HasDependency {
	
	private ASTree tree;
	private ArrayList<String> warnings;
	private ArrayList<BACONCompilerException> exceptions;
	private List<XMLTag> tagList;
	public ConstructedASTree(ASTree _tree, ArrayList<BACONCompilerException> _excep) {
		tree = _tree;
		warnings = new ArrayList<String>();
		exceptions = new ArrayList<BACONCompilerException>(_excep);
	}
	
	public ConstructedASTree(ASTree _tree) {
		tree = _tree;
		warnings = new ArrayList<String>();
		exceptions = new ArrayList<BACONCompilerException>();
	}
	
	public ASTree getTree() {
		return tree;
	}
	
	public List<BACONCompilerException> getExceptions() {
		return exceptions;
	}
	
	public ArrayList<String> getWarnings() {
		return warnings;
	}
	
	public void addSemanticException(BACONCompilerException ex) {
		exceptions.add(ex);
	}
	
	public void addWarning(String warning) {
		warnings.add(warning);
	}
	
	public boolean hasExceptions() {
		return !exceptions.isEmpty();
	}
	
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	public void checkTree(Catagory parent) {
		tree.check(parent, this);
	}
	
	public List<XMLTag> compileTree() {
		String xml = tree.generateXML();
		tagList = constructXMLTag(xml);
		return tagList;
	}
	
	private List<XMLTag> constructXMLTag(String xmlString) {
		List<XMLTag> tagList = new ArrayList<XMLTag>();
		String[] eqStrings = xmlString.split(";");
		for (String eqString:eqStrings) {
			XMLTag eqTag = new XMLTag("equation");
			String[] eqParts = eqString.split(":");
			eqTag.addTag(new XMLTag("name", eqParts[0]));
			eqTag.addTag(new XMLTag("eq", eqParts[1]));
			tagList.add(eqTag);
		}
		return tagList;
	}

	public List<XMLTag> getTagList() {
		return tagList;
	}
	
	public void checkASTree(ASTreeVisitor v) {
		this.getTree().acceptDependencyCheckVisitor(v);
	}
}
