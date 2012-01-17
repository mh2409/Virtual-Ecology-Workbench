package VEW.Planktonica2.Model;

import VEW.Common.XML.XMLTag;

public interface BuildToXML {

	/**
	 * Called to build a model back into XML for saving as the Model.XML file
	 * @return XML as XMLTag(s)
	 * @throws XMLWriteBackException
	 */
	public XMLTag buildToXML () throws XMLWriteBackException;
	
}
