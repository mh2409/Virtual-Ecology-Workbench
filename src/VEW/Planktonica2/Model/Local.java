package VEW.Planktonica2.Model;

import VEW.Common.XML.XMLTag;


public class Local extends VariableType {
	public Local() {
		super();
	}
	
	public XMLTag buildToXML() throws XMLWriteBackException {
		XMLTag varTag = super.buildToXML();
		varTag.setName("local");
		return varTag;
	}
}
