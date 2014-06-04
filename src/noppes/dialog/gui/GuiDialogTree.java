package noppes.dialog.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class GuiDialogTree extends JScrollPane{
	public GuiDialogTree(){
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
		JTree tree = new JTree(top);
		this.setViewportView(tree);
	}
}
