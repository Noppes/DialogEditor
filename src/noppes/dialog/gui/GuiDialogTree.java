package noppes.dialog.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import noppes.dialog.Dialog;
import noppes.dialog.DialogCategory;
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;

public class GuiDialogTree extends JScrollPane{
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	private JTree tree;
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		content = new DefaultMutableTreeNode("Root");
		tree = new JTree(content);
		this.setViewportView(tree);
	}
	public void refresh() {
		content.removeAllChildren();
		for(DialogCategory category : editor.controller.categories.values()){
			DefaultMutableTreeNode parent = new DefaultMutableTreeNode(category);
			content.add(parent);
			for(Dialog dialog : category.dialogs.values()){
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(dialog);
				parent.add(child);
				for(DialogOption option : dialog.options.values()){
					child.add(new DefaultMutableTreeNode(option));
				}
			}
		}
		tree.expandRow(0);
	}
}
