package noppes.dialog.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import noppes.dialog.Dialog;
import noppes.dialog.DialogCategory;
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;

public class GuiDialogTree extends JScrollPane implements MouseListener, ActionListener, TreeSelectionListener{
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	private JTree tree;
	private JComponent component;
	
	private JMenuItem add = new JMenuItem("Add");
	private JMenuItem remove = new JMenuItem("Remove");
	private JPopupMenu menu = new JPopupMenu();
	
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		content = new DefaultMutableTreeNode("Root");
		tree = new JTree(content);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(this);
		tree.addTreeSelectionListener(this);
		this.setViewportView(tree);
		
		menu.add(add);
		menu.add(remove);
		add.addActionListener(this);
		remove.addActionListener(this);
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
		if(component != null)
			editor.remove(component);
        
		DefaultMutableTreeNode node = getSelectedNode();
		if(node == null)
			return;
		if(node.getUserObject() instanceof DialogCategory){
			DialogCategory category = (DialogCategory) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiCategoryEdit((DefaultTreeModel)tree.getModel(), node, category));
		}
		else if(node.getUserObject() instanceof Dialog){
			Dialog dialog = (Dialog) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiDialogEdit((DefaultTreeModel)tree.getModel(), node, dialog));
		}
		else if(node.getUserObject() instanceof DialogOption){
			DialogOption option = (DialogOption) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiOptionEdit((DefaultTreeModel)tree.getModel(), node, option));
		}
		editor.getContentPane().validate();
		editor.repaint();
	}
	@Override
	public void mousePressed(MouseEvent e)  {
		boolean rightClicked = (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
		if(!rightClicked)
			return;
		tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
		        
		DefaultMutableTreeNode node = getSelectedNode();
		if(node == null || editor.activeFile == null)
			return;

		add.setVisible(true);
		remove.setVisible(true);
		
		if(node.getUserObject() instanceof Dialog){
			Dialog dialog = (Dialog) node.getUserObject();
			add.setVisible(dialog.options.size() < 6);
		}
		else if(node.getUserObject() instanceof DialogOption){
			add.setVisible(false);
		}
		else if(!(node.getUserObject() instanceof DialogCategory)){
			remove.setVisible(false);
		}
		menu.show(this, e.getX(), e.getY());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		DefaultMutableTreeNode node = getSelectedNode();
		if(node == null)
			return;
		if(event.getSource() == add){
			DefaultMutableTreeNode selected = node;
			if(node.getUserObject() instanceof DialogCategory){
				DialogCategory category = (DialogCategory) node.getUserObject();
				Dialog dialog = new Dialog();
				editor.controller.saveDialog(category.id, dialog);
				selected = new DefaultMutableTreeNode(dialog);
				node.add(selected);
			}
			else if(node.getUserObject() instanceof Dialog){
				Dialog dialog = (Dialog) node.getUserObject();
				DialogOption option = new DialogOption();
				for(int i = 0; i < 6; i++){
					if(!dialog.options.containsKey(i)){
						dialog.options.put(i, option);
						break;
					}
				}
				selected = new DefaultMutableTreeNode(option);
				node.add(selected);
			}
			else if(!(node.getUserObject() instanceof DialogOption)){
				DialogCategory category = new DialogCategory();
				editor.controller.saveCategory(category);
				selected = new DefaultMutableTreeNode(category);
				node.add(selected);
			}
			((DefaultTreeModel)tree.getModel()).reload(node);	
			tree.setSelectionPath(new TreePath(selected.getPath()));
		}
		if(event.getSource() == remove){
			int result = JOptionPane.showConfirmDialog(this, node.getUserObject() + "\nIs about to be deleted.", "Delete warning", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.NO_OPTION)
				return;
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			if(node.getUserObject() instanceof DialogCategory){
				DialogCategory category = (DialogCategory) node.getUserObject();
				editor.controller.removeCategory(category.id);
				node.removeFromParent();
			}
			if(node.getUserObject() instanceof Dialog){
				Dialog dialog = (Dialog) node.getUserObject();
				editor.controller.removeDialog(dialog);
				node.removeFromParent();
			}
			if(node.getUserObject() instanceof DialogOption){
				DialogOption option = (DialogOption) node.getUserObject();
				Dialog dialog = (Dialog) parent.getUserObject();
				dialog.options.values().remove(option);
				node.removeFromParent();
			}
			((DefaultTreeModel)tree.getModel()).reload(parent);	
		}
			
	}
	
	private DefaultMutableTreeNode getSelectedNode(){
		TreePath path = tree.getSelectionPath();
		if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
			return null;
		return (DefaultMutableTreeNode) path.getLastPathComponent();
	}
	
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}


}
