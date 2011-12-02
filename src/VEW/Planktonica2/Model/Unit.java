package VEW.Planktonica2.Model;

public class Unit {

	private int size;
	private int exponent;
	private String name;
	
	public Unit (int size, String name, int exponent) {
		this.size = size;
		this.name = name;
		this.exponent = exponent;
	}

	
	/**
	 * 
	 * @return size + exponent
	 */
	public int getActualSize() {
		return this.size + this.exponent;
	}
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getExponent() {
		return exponent;
	}

	public void setExponent(int exponent) {
		this.exponent = exponent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return size + "," + name + "," + exponent;
	}
	
	public String format() {
		if (this.name.equals("dimensionless") || this.exponent == 0)
			return "-";
		else if (this.name.equals("null"))
			return "null";
		return name + "^" + (exponent); 
	}
	
	public boolean equals(Unit u) {
		return (name.equals(u.getName()) && exponent == u.getExponent());
	}


	public boolean same_unit(Unit u) {
		return name.equals(u.getName());
	}
	
}