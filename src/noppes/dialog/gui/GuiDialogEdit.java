package noppes.dialog.gui;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import noppes.dialog.Dialog;
import noppes.dialog.DialogController;
import noppes.dialog.DialogEditor;

public class GuiDialogEdit extends JTabbedPane implements FocusListener, DocumentListener, ChangeListener{
	private Dialog dialog;
	private JTextField title;
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	private JTextArea area;
	private static int tabIndex = 0;
	
	public GuiDialogEdit(DefaultTreeModel model, DefaultMutableTreeNode node, Dialog dialog){
		this.node = node;
		this.model = model;
		this.dialog = dialog;
        JPanel panel = new JPanel(false);
        panel.add(new JLabel("Name"));
        panel.add(title = new JTextField(dialog.title));
        title.setPreferredSize(new Dimension(300, 24));
        title.addFocusListener(this);
        title.getDocument().addDocumentListener(this);
        addTab("Dialog", panel);
        addTab("Text", area = new JTextArea(dialog.text));
        area.getDocument().addDocumentListener(this);
        this.setSelectedIndex(tabIndex);
        this.addChangeListener(this);
        
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
	public void changedUpdate(DocumentEvent e) {
		if(e.getDocument() == title.getDocument()){
			dialog.title = title.getText();
			model.reload(node);
		}
		if(e.getDocument() == area.getDocument()){
			dialog.text = area.getText();
		}

		DialogEditor.Instance.setEdited(true);
	}
	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		tabIndex = this.getSelectedIndex();
	}
}
