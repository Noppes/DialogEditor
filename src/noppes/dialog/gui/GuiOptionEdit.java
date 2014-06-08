package noppes.dialog.gui;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import noppes.dialog.Dialog;
import noppes.dialog.DialogController;
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;

public class GuiOptionEdit extends JTabbedPane implements FocusListener, DocumentListener{
	private DialogOption option;
	private JTextField title;
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	
	public GuiOptionEdit(DefaultTreeModel model, DefaultMutableTreeNode node, DialogOption option){
		this.node = node;
		this.model = model;
		this.option = option;
        JPanel panel = new JPanel(false);
        panel.add(new JLabel("Name"));
        panel.add(title = new JTextField(option.title));
        title.setPreferredSize(new Dimension(200, 24));
        title.addFocusListener(this);
        title.getDocument().addDocumentListener(this);
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
}
