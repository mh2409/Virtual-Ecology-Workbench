package VEW.XMLCompiler.ASTNodes;

import VEW.Planktonica2.Model.GlobalVariable;
import VEW.Planktonica2.Model.Type;
import VEW.Planktonica2.Model.Unit;

import java.util.ArrayList;
import java.util.Collection;

public class AmbientVariableTables {

	private static AmbientVariableTables tables;
	private SymbolTable<Type> typeTable;
	private SymbolTable<GlobalVariable> physicsVarTable;
	private SymbolTable<GlobalVariable> waterColumnVarTable;
	private SymbolTable<GlobalVariable> chemicalTable;
	private SymbolTable<GlobalVariable> systemVarTable;
	
	private AmbientVariableTables() {
		initialiseTypeTable();
		initialisePhysicsVarTable();
		initialiseWaterColumnVarTable();
		initialiseSystemVarTable();
		initialiseChemicalTable();
	}

	private void initialiseChemicalTable() {
		chemicalTable = new SymbolTable<GlobalVariable>();
		
	}

	private void initialiseTypeTable() {
		typeTable = new SymbolTable<Type>();
		typeTable.put("$float", new Type("float"));
		typeTable.put("$foodSet", new Type("foodSet"));
		typeTable.put("$vector", new Type("vector"));
		typeTable.put("$boolean", new Type("boolean"));		
	}

	private void initialiseSystemVarTable() {
		Type floatType = (Type) typeTable.get("$float"); 
		systemVarTable = new SymbolTable<GlobalVariable>();
		Collection<Unit> units = new ArrayList<Unit>();
		units.add(new Unit(0, "d", 1));
		systemVarTable.put("d_leap", 
				new GlobalVariable( "d_leap", "Extra days due to leap year (1 or 0)", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "d", 1));
		systemVarTable.put("d_year",
				new GlobalVariable("d_year", "Days this year since 1st January", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "dimensionless", 1));
		systemVarTable.put("PI",
				new GlobalVariable( "PI", "PI", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "h", 1));
		systemVarTable.put("TimeStep",
				new GlobalVariable( "TimeStep", "Time step size", floatType, units, null, null, false));
	}

	private void initialiseWaterColumnVarTable() {
		Type floatType = (Type) typeTable.get("$float");
		waterColumnVarTable = new SymbolTable<GlobalVariable>();
		Collection<Unit> units = new ArrayList<Unit>();
		units.add(new Unit(0, "m", 1));
		waterColumnVarTable.put("Turbocline",
				new GlobalVariable("Turbocline", "Depth of Turbocline", floatType, units, null, null, false));
	}

	private void initialisePhysicsVarTable() {
		Type floatType = (Type) typeTable.get("$float");
		physicsVarTable = new SymbolTable<GlobalVariable>();
		Collection<Unit> units = new ArrayList<Unit>();
		units.add(new Unit(0, "kg", 1));
		units.add(new Unit(0, "m", -3));
		physicsVarTable.put("Density",
				new GlobalVariable("Density", "Density", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "W", 1));
		units.add(new Unit(0, "m", -2));
		physicsVarTable.put("FullIrradiance",
				new GlobalVariable("FullIrradiance", "Full Irradiance", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "dimensionless", 1));
		physicsVarTable.put("Salinity",
				new GlobalVariable("Salinity", "Salinity", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "C", 1));
		physicsVarTable.put("Temperature",
				new GlobalVariable("Temperature", "Temperature", floatType, units, null, null, false));
		units = new ArrayList<Unit>();
		units.add(new Unit(0, "W", 1));
		units.add(new Unit(0, "m", -2));
		physicsVarTable.put("VisibleIrradiance",
				new GlobalVariable("VisibleIrradiance", "Visible Irradiance", floatType, units, null, null, false));		
	}
	
	
	public Object checkAllTables(String key) {
		Object obj = checkTypeTable(key);
		if (obj != null)
			return obj;
		obj = checkGlobalVariableTables(key);
		return obj;
	}
	
	public GlobalVariable checkGlobalVariableTables(String key) {
		GlobalVariable obj = checkPhysicsTable(key);
		if (obj != null)
			return obj;
		obj = checkWaterColumnTable(key);
		if (obj != null)
			return obj;
		obj = checkSystemTable(key);
		if (obj != null)
			return obj;
		obj = checkChemicalTable(key);
		return obj;
	}
	
	public Type checkTypeTable(String key) {
		return typeTable.get(key);
	}
	
	public GlobalVariable checkSystemTable(String key) {
		return systemVarTable.get(key);
	}

	public GlobalVariable checkWaterColumnTable(String key) {
		return waterColumnVarTable.get(key);
	}

	public GlobalVariable checkPhysicsTable(String key) {
		return physicsVarTable.get(key);
	}
	
	public GlobalVariable checkChemicalTable(String key) {
		return chemicalTable.get(key);
	}
	
	public void addChemical(String chemicalName) {
		Type floatType = (Type) typeTable.get("$float");
		String description = "The concentration of " + chemicalName + " in solution";
		Collection<Unit> units = new ArrayList<Unit>();
		units.add(new Unit(0, "mol", 1));
		GlobalVariable chemicalVar = new GlobalVariable(chemicalName, description, floatType, units, new Float(0), 1, false);
		chemicalVar.setWriteBackName(chemicalName + "$Conc");
		chemicalTable.put(chemicalName + "_Conc", chemicalVar);
	}
	
	public GlobalVariable removeChemical(String chemicalName) {
		return chemicalTable.remove(chemicalName + "_Conc");
	}

	public static AmbientVariableTables getTables() {
		if (tables == null) {
			tables = new AmbientVariableTables();
		}
		return tables;
	}
	
	public void destroyVariableTables() {
		chemicalTable = null;
		physicsVarTable = null;
		systemVarTable = null;
		waterColumnVarTable = null;
		typeTable = null;
		tables = null;
	}
	
	public static void destroyAmbientVariableTable() {
		if (tables != null) {
			tables.destroyVariableTables();
		}
	}
	
	public String[] getAllVariableNames() {
		Object[] sys_vars = systemVarTable.keySet().toArray();
		Object[] water_vars = waterColumnVarTable.keySet().toArray();
		Object[] phys_vars = physicsVarTable.keySet().toArray();
		Object[] chem_vars = chemicalTable.keySet().toArray();
		String [] all_vars = new String[(sys_vars.length 
				+ water_vars.length + phys_vars.length + chem_vars.length)];
		int pos = 0;
		for (int i = 0; i < sys_vars.length; i++) {
			all_vars[pos] = (String) sys_vars[i];
			pos++;
		}
		for (int i = 0; i < water_vars.length; i++) {
			all_vars[pos] = (String) water_vars[i];
			pos++;
		}
		for (int i = 0; i < phys_vars.length; i++) {
			all_vars[pos] = (String) phys_vars[i];
			pos++;
		}
		for (int i = 0; i < chem_vars.length; i++) {
			all_vars[pos] = (String) chem_vars[i];
			pos++;
		}
		return all_vars;
	}
	
	public Collection<String> retrieveChemicalBaseNames() {
		Collection<String> chemicalBaseNames = new ArrayList<String>(); 
		Collection<GlobalVariable> chemicalVars = chemicalTable.values();
		for (GlobalVariable chemical : chemicalVars) {
			chemicalBaseNames.add(chemical.getName());
		}
		return chemicalBaseNames;
	}
	
}
