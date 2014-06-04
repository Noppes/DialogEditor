package noppes.dialog.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import noppes.dialog.DialogCategory;
import noppes.dialog.DialogEditor;

public class GuiDialogTree extends JScrollPane{
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		content = new DefaultMutableTreeNode("Root");
		JTree tree = new JTree(content);
		this.setViewportView(tree);
	}
	public void refresh() {
		content.removeAllChildren();
		for(DialogCategory category : editor.controller.categories.values()){
			content.add(new DefaultMutableTreeNode(category));
		}
		this.invalidate();
	}
}
