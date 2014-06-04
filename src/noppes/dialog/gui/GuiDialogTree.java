package noppes.dialog.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import noppes.dialog.Dialog;
import noppes.dialog.DialogCategory;
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;

public class GuiDialogTree extends JScrollPane implements TreeSelectionListener{
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	private JTree tree;
	private JComponent component;
	
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		content = new DefaultMutableTreeNode("Root");
		tree = new JTree(content);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
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
		editor.getContentPane().validate();
		editor.repaint();
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = tree.getSelectionPath();
		if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
			return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(component == null)
			editor.remove(component);
		if(node.getUserObject() instanceof DialogCategory){
			DialogCategory category = (DialogCategory) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiCategoryEdit());
		}
		editor.getContentPane().validate();
		editor.repaint();
	}
}
