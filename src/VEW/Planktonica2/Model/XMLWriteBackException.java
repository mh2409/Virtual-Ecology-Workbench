package VEW.Planktonica2.Model;

import java.util.ArrayList;
import java.util.List;

import VEW.XMLCompiler.ANTLR.CompilerException;

/**
 * Container for compiler exceptions - enables multiple errors
 * @author David Coulden
 *
 */
public class XMLWriteBackException extends Exception {
	
	private static final long serialVersionUID = -8772919072822349882L;
	
	private List<CompilerException> compilerExceptions;
	
	// constructors for list
	public XMLWriteBackException(CompilerException ex) {
		compilerExceptions = new ArrayList<CompilerException>();
		compilerExceptions.add(ex);
	}
	
	public XMLWriteBackException(List<CompilerException> compilerExceptions) {
		this.compilerExceptions = compilerExceptions;
	}
	
	public XMLWriteBackException() {
		compilerExceptions = new ArrayList<CompilerException>();
	}

	// add functions
	public void addCompilerException(CompilerException ex) {
		compilerExceptions.add(ex);
	}
	
	public void addCompilerException(List<CompilerException> compilerExceptions, String categoryName) {
		for (CompilerException c : compilerExceptions) {
			c.setErrorCategoryName(categoryName);
		}
		this.compilerExceptions.addAll(compilerExceptions);
	}
	
	public List<CompilerException> getCompilerExceptions() {
		return compilerExceptions;
	}
	
	public boolean hasExceptions() {
		return !compilerExceptions.isEmpty();
	}
}
