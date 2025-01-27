package VEW.Planktonica2.Model;

/**
 * Enum for all the different XML tags that appear in VEW
 * @author Chris Bates & Michael Hinstridge
 *
 */
public enum XMLTagEnum {
	FUNCTIONAL_GROUP("functionalgroup"), CHEMICAL("chemical"), NAME("name"), INVISIBLE("invisible"),
	STAGE("stage"), FUNCTION("function"), CALLED_IN("calledin"), EQUATION("equation"), EQ("eq"), 
	AUTHOR("author"), COMMENT("comment"), ARCHIVE_NAME("archivename"), PARAMETER("parameter"), 
	DESCRIPTION("desc"), VALUE("value"), HIST("hist"), VARIETY_LINK("link"), LOCAL("local"), 
	STATE_VARIABLE("variable"), VARIETY_CONCENTRATION("varietyconcentration"), VARIETY_VARIABLE("varietyvariable"),
	VARIETY_LOCAL("varietylocal"), VARIETY_PARAM("varietyparameter"), PIGMENT("pigment"), SPECTRUM("spectrum"), 
	GRAPH_VAL("eq"), UNIT("unit"), PREDATOR("predator"), CODE_NAME("codename"), SIZE_VAR("sizevar");
	
	
	private final String tag;
	
	private XMLTagEnum (String xmlTag) {
		this.tag = xmlTag;
	}
	
	public String xmlTag() {
		return this.tag;
	}
}
