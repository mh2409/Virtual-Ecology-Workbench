package VEW.Planktonica2.Model;

import java.util.ArrayList;

/**
 * Unit type system
 * @author Andrew West
 *
 */
public class UnitBaseForm {

	private ArrayList<Unit> baseform;
	private float scalefactor;
	
	public UnitBaseForm(ArrayList<Unit> base, float scale) {
		this.baseform = base;
		this.scalefactor = scale;
	}
	
	public void setBaseform(ArrayList<Unit> baseform) {
		this.baseform = baseform;
	}
	
	public ArrayList<Unit> getBaseform() {
		return baseform;
	}
	
	public void setScalefactor(float scalefactor) {
		this.scalefactor = scalefactor;
	}
	
	public float getScalefactor() {
		return scalefactor;
	}
	
	
	
}
