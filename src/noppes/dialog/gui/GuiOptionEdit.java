package noppes.dialog.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import noppes.dialog.Dialog;
import noppes.dialog.DialogController;
import noppes.dialog.DialogOption;
import noppes.dialog.gui.GuiDialogTree.DialogNode;

public class GuiOptionEdit extends JTabbedPane implements FocusListener, DocumentListener, ActionListener{
	private DialogOption option;
	private JTextField title;
	private JComboBox<String> position;
	private JComboBox<String> type;
	private JTextField dialog;
	private JTextField color;
	
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	private JTree tree;
	private JPanel panel;
	
	public GuiOptionEdit(JTree tree, DefaultMutableTreeNode node, DialogOption option){
		this.node = node;
		this.tree = tree;
		this.model = (DefaultTreeModel) tree.getModel();
		this.option = option;
        panel = new JPanel(new GridBagLayout());      
        addTab("Option", panel);
        
        init();
	}
	
	private void init(){
        panel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        panel.add(title = new JTextField(option.getTitle()), gbc);
        title.setPreferredSize(new Dimension(300, 24));
        title.addFocusListener(this);
        title.getDocument().addDocumentListener(this);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Position"), gbc);
        gbc.gridx = 1;
        panel.add(position = new JComboBox<String>(new String[]{"0", "1", "2", "3", "4", "5"}), gbc);
        position.setSelectedIndex(option.optionID);
        position.setPreferredSize(new Dimension(100, 24));
        position.setEditable(false);
        position.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        panel.add(type = new JComboBox<String>(new String[]{"Quit", "Dialog", "Disabled", "Role", "Command"}), gbc);
        type.setSelectedIndex(option.getType());
        type.setPreferredSize(new Dimension(100, 24));
        type.setEditable(false);
        type.addActionListener(this);

        if(option.getType() == 1){
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel("Dialog ID"), gbc);
            gbc.gridx = 1;
            panel.add(dialog = new JTextField(option.getDialogID() + ""), gbc);
            dialog.setPreferredSize(new Dimension(300, 24));
            dialog.addFocusListener(this);
            dialog.getDocument().addDocumentListener(this);
        }

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Color"), gbc);
        gbc.gridx = 1;
        panel.add(color = new JTextField(option.getColorHex()), gbc);
        color.setPreferredSize(new Dimension(300, 24));
        color.addFocusListener(this);
        color.getDocument().addDocumentListener(this);

        panel.repaint();
        panel.revalidate();
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == dialog){
			try{
				option.setDialogID(Integer.parseInt(dialog.getText()));
				save();
			}
			catch(NumberFormatException ex){}
			init();
		}
		if(e.getSource() == color){
			try{
				option.setColorHex(color.getText());
				save();
			}
			catch(NumberFormatException ex){}
			init();
		}
	}
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		option.setTitle(title.getText());
		save();
		model.reload(node);
	}
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		changedUpdate(arg0);
	}
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		changedUpdate(arg0);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == position){
			DialogNode parent = (DialogNode)node.getParent();
			Dialog dialog = (Dialog) parent.getUserObject();
			
			int index = position.getSelectedIndex();
			Map<Integer,DialogOption> options = dialog.getOptions();
			DialogOption op = options.remove(index);
			if(op != null){
				options.put(option.optionID, op);
				op.optionID = option.optionID;
			}
			option.optionID = index;
			options.put(index, option);
			dialog.setOptions(options);
			save();
			
			model.reload(parent);
			tree.setSelectionPath(new TreePath(node.getPath()));
		}
		if(e.getSource() == type){
			DialogOption option = (DialogOption) node.getUserObject();
			option.setType(type.getSelectedIndex());
			save();
			init();
		}
	}
	
	private void save(){
		DialogNode parent = (DialogNode)node.getParent();
		Dialog dialog = (Dialog) parent.getUserObject();
		DialogController.instance.saveDialog(dialog.category.getID(), dialog);
	}
}
