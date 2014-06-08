package noppes.dialog.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;
import noppes.dialog.gui.GuiDialogTree.DialogNode;

public class GuiOptionEdit extends JTabbedPane implements FocusListener, DocumentListener, ActionListener{
	private DialogOption option;
	private JTextField title;
	private JComboBox position;
	
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	private JTree tree;
	
	public GuiOptionEdit(JTree tree, DefaultMutableTreeNode node, DialogOption option){
		this.node = node;
		this.tree = tree;
		this.model = (DefaultTreeModel) tree.getModel();
		this.option = option;
        JPanel panel = new JPanel(new GridBagLayout());       
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        panel.add(title = new JTextField(option.title), gbc);
        
        title.setPreferredSize(new Dimension(300, 24));
        title.addFocusListener(this);
        title.getDocument().addDocumentListener(this);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Position"), gbc);
        gbc.gridx = 1;
        panel.add(position = new JComboBox(new String[]{"0", "1", "2", "3", "4", "5"}), gbc);
        position.setSelectedIndex(option.id);
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
		option.title = title.getText();
		model.reload(node);
		DialogEditor.Instance.setEdited(true);
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
			DialogOption op = dialog.options.get(index);
			if(op != null){
				dialog.options.put(option.id, op);
				op.id = option.id;
			}
			option.id = index;
			dialog.options.put(index, option);
			model.reload(parent);
			tree.setSelectionPath(new TreePath(node.getPath()));
		}
	}
}
