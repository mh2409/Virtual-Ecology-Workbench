package VEW.Planktonica2.Model;

import VEW.Common.XML.XMLTag;

public interface BuildFromXML {

	/**
	 * Called on various tags as we progress through the model to build the XML
	 * source code into an object oriented Model
	 * @param tag
	 * @return Model as spec'd in this package
	 */
	public BuildFromXML build(XMLTag tag);
	
}
