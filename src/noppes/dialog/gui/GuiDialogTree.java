package noppes.dialog.gui;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
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
import noppes.dialog.EnumNodeType;

public class GuiDialogTree extends JScrollPane implements MouseListener, ActionListener, TreeSelectionListener {
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	private JTree tree;
	private JComponent component;
	
	private JMenuItem add = new JMenuItem("Add");
	private JMenuItem remove = new JMenuItem("Remove");
	private JPopupMenu menu = new JPopupMenu();
	
	public GuiDialogTree(DialogEditor editor){
		this.editor = editor;
		content = new DialogNode("Root");
		tree = new JTree(content);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(this);
		tree.addTreeSelectionListener(this);
        tree.setDragEnabled(true);  
        tree.setDropMode(DropMode.ON_OR_INSERT);  
		tree.setTransferHandler(new NodeDragHandler(this));
		this.setViewportView(tree);
		
		menu.add(add);
		menu.add(remove);
		add.addActionListener(this);
		remove.addActionListener(this);
	}
	
	
	public void refresh() {
		content.removeAllChildren();
		for(DialogCategory category : editor.controller.categories.values()){
			DefaultMutableTreeNode parent = new DialogNode(category);
			content.add(parent);
			for(Dialog dialog : category.dialogs.values()){
				DefaultMutableTreeNode child = new DialogNode(dialog);
				parent.add(child);
				for(DialogOption option : dialog.options.values()){
					child.add(new DialogNode(option));
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
        
		DialogNode node = getSelectedNode();
		if(node == null)
			return;
		if(node.type == EnumNodeType.CATEGORY){
			DialogCategory category = (DialogCategory) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiCategoryEdit((DefaultTreeModel)tree.getModel(), node, category));
		}
		else if(node.type == EnumNodeType.DIALOG){
			Dialog dialog = (Dialog) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiDialogEdit((DefaultTreeModel)tree.getModel(), node, dialog));
		}
		else if(node.type == EnumNodeType.OPTION){
			DialogOption option = (DialogOption) node.getUserObject();
			editor.add(BorderLayout.CENTER, component = new GuiOptionEdit(tree, node, option));
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
		        
		DialogNode node = getSelectedNode();
		if(node == null || editor.activeFile == null)
			return;

		add.setVisible(true);
		remove.setVisible(true);
		
		if(node.type == EnumNodeType.DIALOG){
			Dialog dialog = (Dialog) node.getUserObject();
			add.setVisible(dialog.options.size() < 6);
		}
		else if(node.type == EnumNodeType.OPTION){
			add.setVisible(false);
		}
		else if(node.type == EnumNodeType.ROOT){
			remove.setVisible(false);
		}
		menu.show(this, e.getX(), e.getY());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		DialogNode node = getSelectedNode();
		if(node == null)
			return;
		if(event.getSource() == add){
			DefaultMutableTreeNode selected = node;
			if(node.type == EnumNodeType.CATEGORY){
				DialogCategory category = (DialogCategory) node.getUserObject();
				Dialog dialog = new Dialog();
				editor.controller.saveDialog(category.id, dialog);
				selected = new DialogNode(dialog);
				node.add(selected);
				DialogEditor.Instance.setEdited(true);
			}
			else if(node.type == EnumNodeType.DIALOG){
				Dialog dialog = (Dialog) node.getUserObject();
				DialogOption option = new DialogOption();
				for(int i = 0; i < 6; i++){
					if(!dialog.options.containsKey(i)){
						dialog.options.put(i, option);
						break;
					}
				}
				selected = new DialogNode(option);
				node.add(selected);
				DialogEditor.Instance.setEdited(true);
			}
			else if(node.type == EnumNodeType.ROOT){
				DialogCategory category = new DialogCategory();
				editor.controller.saveCategory(category);
				selected = new DialogNode(category);
				node.add(selected);
				DialogEditor.Instance.setEdited(true);
			}
			((DefaultTreeModel)tree.getModel()).reload(node);	
			tree.setSelectionPath(new TreePath(selected.getPath()));
		}
		if(event.getSource() == remove){
			int result = JOptionPane.showConfirmDialog(this, node.getUserObject() + "\nIs about to be deleted.", "Delete warning", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.NO_OPTION)
				return;
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			if(node.type == EnumNodeType.CATEGORY){
				DialogCategory category = (DialogCategory) node.getUserObject();
				editor.controller.removeCategory(category.id);
				node.removeFromParent();
				DialogEditor.Instance.setEdited(true);
			}
			if(node.type == EnumNodeType.DIALOG){
				Dialog dialog = (Dialog) node.getUserObject();
				editor.controller.removeDialog(dialog);
				node.removeFromParent();
				DialogEditor.Instance.setEdited(true);
			}
			if(node.type == EnumNodeType.OPTION){
				DialogOption option = (DialogOption) node.getUserObject();
				Dialog dialog = (Dialog) parent.getUserObject();
				dialog.options.values().remove(option);
				node.removeFromParent();
				DialogEditor.Instance.setEdited(true);
			}
			((DefaultTreeModel)tree.getModel()).reload(parent);	
		}
			
	}
	
	private DialogNode getSelectedNode(){
		TreePath path = tree.getSelectionPath();
		if(path == null || !(path.getLastPathComponent() instanceof DialogNode))
			return null;
		return (DialogNode) path.getLastPathComponent();
	}
	
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}


	class DialogNode extends DefaultMutableTreeNode implements Transferable{
		public EnumNodeType type = EnumNodeType.ROOT;
		public DialogNode(Object ob){
			super(ob);
			if(ob instanceof DialogCategory)
				type = EnumNodeType.CATEGORY;
			else if(ob instanceof Dialog)
				type = EnumNodeType.DIALOG;
			else if(ob instanceof DialogOption)
				type = EnumNodeType.OPTION;
		}
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return this;
		}
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return null;
		}
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return false;
		}
		
	}
	
	class NodeDragHandler extends TransferHandler{
		private GuiDialogTree tree;
		private NodeDragHandler(GuiDialogTree tree){
			this.tree = tree;
		}
		@Override
	    public boolean canImport(TransferHandler.TransferSupport support) {
			if(!support.isDrop())
				return false;
	        DialogNode selected = tree.getSelectedNode();
	        if(selected.type != EnumNodeType.DIALOG)
	        	return false;
			JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
	        DialogNode node = (DialogNode) dl.getPath().getLastPathComponent();

	        return selected.getParent() != node && node.type == EnumNodeType.CATEGORY;
	    }
	    @Override
	    public boolean importData(TransferHandler.TransferSupport support) {
        	DialogNode node = tree.getSelectedNode();
	        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
	        int childIndex = dl.getChildIndex();
	        TreePath dest = dl.getPath();
	        DialogNode parent = (DialogNode)dest.getLastPathComponent();
	        JTree tree = (JTree)support.getComponent();
	        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
	        int index = childIndex; 
	        if(childIndex == -1) {     
	            index = parent.getChildCount();
	        }
        	Dialog dialog = (Dialog) node.getUserObject();
        	DialogCategory category = (DialogCategory) ((DialogNode)node.getParent()).getUserObject();
        	category.dialogs.values().remove(dialog);
        	category = (DialogCategory) parent.getUserObject();
        	category.dialogs.put(dialog.id, dialog);
            model.removeNodeFromParent(node);
            model.insertNodeInto(node, parent, index++);
            DialogEditor.Instance.setEdited(true);
	        return true;
	    }

	    protected Transferable createTransferable(JComponent c) {
	    	return tree.getSelectedNode();
	    }   
	    public int getSourceActions(JComponent c) {  
	        return MOVE;  
	    }  
	}
}
