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

public class GuiDialogEdit extends JTabbedPane implements FocusListener, DocumentListener{
	private Dialog dialog;
	private JTextField title;
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	
	public GuiDialogEdit(DefaultTreeModel model, DefaultMutableTreeNode node, Dialog dialog){
		this.node = node;
		this.model = model;
		this.dialog = dialog;
        JPanel panel = new JPanel(false);
        panel.add(new JLabel("Name"));
        panel.add(title = new JTextField(dialog.title));
        title.setPreferredSize(new Dimension(200, 24));
        title.addFocusListener(this);
        title.getDocument().addDocumentListener(this);
        addTab("Dialog", panel);
	}
	@Override
	public void focusGained(FocusEvent e) {
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == title){
			DialogController.instance.saveDialog(dialog.category.id, dialog);
			title.setText(dialog.title);
			model.reload(node);
		}
	}
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		dialog.title = title.getText();
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
