package VEW.UIComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import VEW.Planktonica2.ControllerStructure.VariableType;

public class VariableEditorPanel extends JPanel {

	private Dimension d;
	private VarType current_selection = VarType.GROUPVAR;
	
	JPanel variable_info = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	JLabel type = new JLabel("Type/Scope :");
	JComboBox  type_combo = new JComboBox();
	JLabel name = new JLabel("Variable Name :   ");
	JTextField var_name = new JTextField();
	JLabel desc = new JLabel("Description :");
	JTextField var_desc = new JTextField();
	JLabel history_size = new JLabel("History Size :  ");
	JTextField h_size = new JTextField("0");
	JLabel initial_value = new JLabel("Initial Value :  ");
	JTextField i_val = new JTextField("0");
	JLabel fs_link = new JLabel("Food-Set Link :");
	JComboBox  link_combo = new JComboBox();
	JButton add_var = new JButton("Add");
	
	public VariableEditorPanel(Dimension dimension) {
		this.setPreferredSize(dimension);
		d = dimension;
		this.initialize();
	}

	public VarType getCurrent_selection() {
		return current_selection;
	}

	private void initialize() {
		variable_info.setPreferredSize(new Dimension(d.width - 25, d.height - 25));
		add_var.addActionListener(new AddVarListener(this));
		// Label for scope choice
		c.ipady = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		variable_info.add(type, c);
		// Combo box to choose scope
		for (VarType vt : VarType.values()) {
			type_combo.addItem(vt);
	    }
		type_combo.addItemListener(new VarTypeListener(this));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 3;
		c.gridy = 0;
		variable_info.add(type_combo, c);
		
		// Label for Variable name
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		variable_info.add(name, c);
		// Text field for Var name
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 1;
		variable_info.add(var_name, c);
		
		// Label for Variable description
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		variable_info.add(desc, c);
		// Text field for Var description
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 2;
		variable_info.add(var_desc, c);

		change_var_type(current_selection);
		this.add(variable_info,BorderLayout.WEST);
	}
	
	public void change_var_type(VarType var_type) {
		current_selection = var_type;
		boolean i_value = true;
		boolean history = true;
		boolean food_set = true;
		switch (var_type) {
		case GROUPVAR : 
			food_set = false;
			break;
		case GROUPPARAM :
			history = false;
			food_set = false;
			break;
		case LOCALVAR :
			i_value = false;
			history = false;
			food_set = false;
			break;
		case FOODPARAM :
			history = false;
			break;
		case FOODSET :
			i_value = false;
			history = false;
			food_set = false;
			break;
		case FOODLOCAL :
			i_value = false;
			history = false;
			break;
		case FOODVAR :
			break;
		}
		history_size.setVisible(false);
		variable_info.remove(history_size);
		variable_info.remove(h_size);
		initial_value.setVisible(false);
		variable_info.remove(initial_value);
		variable_info.remove(i_val);
		fs_link.setVisible(false);
		variable_info.remove(fs_link);
		variable_info.remove(link_combo);
		variable_info.remove(add_var);
		variable_info.validate();
		int y = 3;
		c.gridwidth = 2;
		if (history) {
			// Label for History Size
			history_size.setVisible(true);
			c.gridx = 0;
			c.gridy = y;
			variable_info.add(history_size, c);
			// Text field for History Size
			c.gridx = 3;
			c.gridy = y;
			variable_info.add(h_size, c);
			y++;
		}
		if (i_value) {
			// Label for Initial value
			initial_value.setVisible(true);
			c.gridx = 0;
			c.gridy = y;
			variable_info.add(initial_value, c);
			// Text field for Initial value
			c.gridx = 3;
			c.gridy = y;
			variable_info.add(i_val, c);
			y++;
		}
		if (food_set) {
			// Label for food-set link
			fs_link.setVisible(true);
			c.gridx = 0;
			c.gridy = y;
			variable_info.add(fs_link, c);
			// Combo box for food links
			link_combo.addItem("Food Set 1");
			link_combo.addItem("Food Set 2");
			c.gridx = 3;
			c.gridy = y;
			variable_info.add(link_combo, c);
			y++;
		}
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = y;
		variable_info.add(add_var, c);
		variable_info.validate();
	}
	
	public VariableType construct_variable() {
		if (var_name.getText().equals("")) {
			JOptionPane.showMessageDialog(variable_info, "Variable must have a name");
			return null;
		}
		// TODO check name is unique
		// Check that history has a legal value
		switch (current_selection) {
		case GROUPVAR :
		case FOODVAR :
			try {
				int hist = Integer.parseInt(h_size.getText());
				if (hist < 0) {
					JOptionPane.showMessageDialog(variable_info, "History size must be 0 or higher");
					return null;
				}
			} catch (NumberFormatException n) {
				JOptionPane.showMessageDialog(variable_info, "Invalid history size");
				return null;
			}
		}
		// Check that initial value is legal
		switch (current_selection) {
		case GROUPVAR :
		case GROUPPARAM :
		case FOODPARAM :
		case FOODVAR :
			try {
				int init = Integer.parseInt(i_val.getText());
			} catch (NumberFormatException n) {
				JOptionPane.showMessageDialog(variable_info, "Invalid variable initial value");
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1484036714507019850L;

	static class VarTypeListener implements ItemListener {

		private VariableEditorPanel parent;
		
		public VarTypeListener(VariableEditorPanel par) {
			this.parent = par;
		}
		
		@Override
		public void itemStateChanged(ItemEvent i) {
			VarType selection = (VarType) i.getItem();
			if (!parent.getCurrent_selection().equals(selection)) {
				parent.change_var_type(selection);
			}
		}
		
	}
	
	static class AddVarListener implements ActionListener {

		private VariableEditorPanel parent;
		
		public AddVarListener(VariableEditorPanel par) {
			this.parent = par;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			VariableType var = parent.construct_variable();
			// Add it to the model
		}
	}
}
