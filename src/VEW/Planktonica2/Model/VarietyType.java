package VEW.Planktonica2.Model;


/**
 * Types for Variety based variables
 * @author Chris Bates & Michael Hinstridge
 *
 */
public class VarietyType extends Type {
	
	private Type elementType;
	private VarietyConcentration link;
	
	public VarietyType(String _name, Type _elementType) {
		super(_name);
		elementType = _elementType;
		link = null;
	}
	
	public VarietyType(String _name, Type _elementType, VarietyConcentration link) {
		super(_name);
		elementType = _elementType;
		this.link = link;
	}
	
	public Type getElementType() {
		return elementType;
	}

	public VarietyConcentration getLink() {
		return link;
	}

	public void setLink(VarietyConcentration link) {
		this.link = link;
	}
	
	/**
	 * Ingestion is a special case
	 * @param vType
	 * @return If vType is in the variety conc list
	 */
	public boolean checkLinkCompatible(VarietyType vType) {
		String linkName = link.getName();
		String vTypeLinkName = vType.getLink().getName();
		return linkName.equals(vTypeLinkName) || linkName.equals("Ingestion") 
							|| vTypeLinkName.equals("Ingestion");
	}

}
