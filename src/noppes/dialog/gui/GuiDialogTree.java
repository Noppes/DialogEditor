package noppes.dialog.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import noppes.dialog.DialogEditor;

public class GuiDialogTree extends JScrollPane{
	private DialogEditor editor;
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
		JTree tree = new JTree(top);
		this.setViewportView(tree);
	}
}
