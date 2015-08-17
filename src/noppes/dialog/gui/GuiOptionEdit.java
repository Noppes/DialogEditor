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
	private Dialog dialog;
	private JTextField title;
	private JComboBox position;
	
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	private JTree tree;
	
	public GuiOptionEdit(JTree tree, DefaultMutableTreeNode node, Dialog dialog, DialogOption option){
		this.node = node;
		this.tree = tree;
		this.model = (DefaultTreeModel) tree.getModel();
		this.option = option;
		this.dialog = dialog;
        JPanel panel = new JPanel(new GridBagLayout());       
        
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
        gbc.gridy = 1;
        panel.add(new JLabel("Position"), gbc);
        gbc.gridx = 1;
        panel.add(position = new JComboBox(new String[]{"0", "1", "2", "3", "4", "5"}), gbc);
        position.setSelectedIndex(option.optionID);
        position.setEditable(true);
        position.addActionListener(this);
        
        addTab("Option", panel);
	}
	@Override
	public void focusGained(FocusEvent e) {
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == title){
			
		}
	}
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		option.setTitle(title.getText());
		DialogController.instance.saveDialog(dialog.category.getID(), dialog);
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
			DialogController.instance.saveDialog(dialog.category.getID(), dialog);
			
			model.reload(parent);
			tree.setSelectionPath(new TreePath(node.getPath()));
		}
	}
}
