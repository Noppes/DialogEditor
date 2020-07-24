package noppes.dialog.gui;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

public class GuiDialogEdit extends JTabbedPane implements FocusListener, DocumentListener, ChangeListener{
	private Dialog dialog;
	private JTextField title;
	private DefaultMutableTreeNode node;
	private DefaultTreeModel model;
	private JTextArea area;
	private static int tabIndex = 0;
	private boolean isTesting = true;
	public static GuiDialogEdit	dialogInstance;

	public GuiDialogEdit(DefaultTreeModel model, DefaultMutableTreeNode node, Dialog dialog){
		this.node = node;
		this.model = model;
		this.dialog = dialog;
		dialogInstance = this;
		JPanel panel = new JPanel(false);
		SpellChecker.setUserDictionaryProvider( new FileUserDictionary() );
		if(isTesting) SpellChecker.registerDictionaries(DialogEditor.class.getResource("dict/"), null);
		else SpellChecker.registerDictionaries(DialogEditor.class.getResource("/src/noppes/dialog/dict/"), null);
		panel.add(new JLabel("Name"));
		panel.add(title = new JTextField(dialog.getTitle()));
		title.setPreferredSize(new Dimension(300, 24));
		title.addFocusListener(this);
		title.getDocument().addDocumentListener(this);
		addTab("Dialog", panel);
		area = new JTextArea(dialog.getText());
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		SpellChecker.register(area);
		addTab("Text", area);
		area.getDocument().addDocumentListener(this);
		this.setSelectedIndex(tabIndex);
		this.addChangeListener(this);

	}

	public void saveDialouge() {
		System.out.println(dialog.getText());
		DialogController.instance.saveDialog(dialog.category.getID(), dialog);
		title.setText(dialog.getTitle());
		model.reload(node);
	}

	@Override
	public void focusGained(FocusEvent e) {

	}
	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == title){
			System.out.println(dialog.getText());
			DialogController.instance.saveDialog(dialog.category.getID(), dialog);
			title.setText(dialog.getTitle());
			model.reload(node);
		}
	}
	@Override
	public void changedUpdate(DocumentEvent e) {
		if(e.getDocument() == title.getDocument()){
			dialog.setTitle(title.getText());
			model.reload(node);
		}
		if(e.getDocument() == area.getDocument()){
			dialog.setText(area.getText());
		}
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
