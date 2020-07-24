package noppes.dialog.gui;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import noppes.dialog.Dialog;
import noppes.dialog.DialogCategory;
import noppes.dialog.DialogController;
import noppes.dialog.DialogEditor;
import noppes.dialog.DialogOption;
import noppes.dialog.EnumNodeType;
import noppes.dialog.Json;
import noppes.dialog.Json.JsonException;

public class GuiDialogTree extends JScrollPane implements MouseListener, ActionListener, TreeSelectionListener, ClipboardOwner {
	private DialogEditor editor;
	private DefaultMutableTreeNode content;
	private JTree tree;
	private JComponent component;
	
	private JMenuItem add = new JMenuItem("Add");
	private JMenuItem remove = new JMenuItem("Remove");
	private JMenuItem copy = new JMenuItem("Copy");
	private JMenuItem paste = new JMenuItem("Paste");
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
		menu.addSeparator();
		menu.add(paste);
		menu.add(copy);
		add.addActionListener(this);
		remove.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
	}
	
	
	public void refresh() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				content.removeAllChildren();
				Enumeration<TreePath> paths = tree.getExpandedDescendants(tree.getPathForRow(0));
				for(DialogCategory category : editor.controller.categories.values()){
					DefaultMutableTreeNode parent = new DialogNode(category);
					content.add(parent);
					for(Dialog dialog : category.dialogs.values()){
						DefaultMutableTreeNode child = new DialogNode(dialog);
						parent.add(child);
						for(DialogOption option : dialog.getOptions().values()){
							child.add(new DialogNode(option));
						}
					}
				}
		        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		        model.reload();
		        if(paths != null){
			        while(paths.hasMoreElements()){
			        	TreePath path = paths.nextElement();
			        	tree.expandPath(path);
			        }
		        }
			}
		});
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(component != null)
			editor.remove(component);
        
		DialogNode node = getSelectedNode();
		if(node == null)
			return;
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
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

		copy.setVisible(true);
		paste.setVisible(true);
		
		DialogNode copied = getClipboard();
		
		if(node.type == EnumNodeType.DIALOG){
			Dialog dialog = (Dialog) node.getUserObject();
			add.setVisible(dialog.getOptions().size() < 6);
			copy.setVisible(true);
			paste.setVisible(false);
		}
		else if(node.type == EnumNodeType.OPTION){
			add.setVisible(false);
			copy.setVisible(false);
			paste.setVisible(false);
		}
		else if(node.type == EnumNodeType.ROOT){
			remove.setVisible(false);
			copy.setVisible(false);
			paste.setEnabled(copied != null && copied.type == EnumNodeType.CATEGORY);
		}
		else if(node.type == EnumNodeType.CATEGORY){
			paste.setEnabled(copied != null && copied.type == EnumNodeType.DIALOG);
		}
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, this);
		menu.show(this, p.x, p.y);
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
				editor.controller.saveDialog(category.getID(), dialog);
				selected = new DialogNode(dialog);
				node.add(selected);
			}
			else if(node.type == EnumNodeType.DIALOG){
				Dialog dialog = (Dialog) node.getUserObject();
				DialogOption option = new DialogOption(-1);
				dialog.addOption(option);
				editor.controller.saveDialog(dialog.category.getID(), dialog);
				selected = new DialogNode(option);
				node.add(selected);
			}
			else if(node.type == EnumNodeType.ROOT){
				DialogCategory category = new DialogCategory();
				editor.controller.saveCategory(category);
				selected = new DialogNode(category);
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
			if(node.type == EnumNodeType.CATEGORY){
				DialogCategory category = (DialogCategory) node.getUserObject();
				editor.controller.removeCategory(category.getID());
				node.removeFromParent();
			}
			if(node.type == EnumNodeType.DIALOG){
				Dialog dialog = (Dialog) node.getUserObject();
				editor.controller.removeDialog(dialog);
				node.removeFromParent();
			}
			if(node.type == EnumNodeType.OPTION){
				int id = ((DialogOption) node.getUserObject()).optionID;
				Dialog dialog = (Dialog) parent.getUserObject();
				dialog.removeOption(id);
				editor.controller.saveDialog(dialog.category.getID(), dialog);
				node.removeFromParent();
				
			}
			((DefaultTreeModel)tree.getModel()).reload(parent);	
		}
		if(event.getSource() == copy){
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			clip.setContents(node, this);
		}
		if(event.getSource() == paste){
			DialogNode pasted = getClipboard();
			if(pasted == null)
				return;
			if(pasted.type == EnumNodeType.DIALOG && node.type == EnumNodeType.CATEGORY){
				addDialog((DialogCategory)node.getUserObject(), (Dialog)pasted.getUserObject());
				refresh();						
			}
			if(pasted.type == EnumNodeType.CATEGORY && node.type == EnumNodeType.ROOT){
				addCategory((DialogCategory) pasted.getUserObject());
				refresh();						
			}
			
		}
			
	}
	
	public boolean addCategory(DialogCategory category) {
		if(editor.controller.categories.containsKey(category.getID())){
			category.setID(editor.controller.getUniqueCategoryID());
		}
		if(editor.controller.containsCategoryName(category, category.getTitle())){
			String[] buttons = {"Overwrite", "Change", "Cancel"};
			int result = JOptionPane.showOptionDialog(this, category + "\nCategory found with the same namey", "Conflict warning", JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);
			if(result == 0)
				editor.controller.removeCategory(editor.controller.getCategoryFromName(category, category.getTitle()).getID());
			if(result == 1){
	    		while(editor.controller.containsCategoryName(category, category.getTitle()))
	    			category.setTitle(category.getTitle() + "_");
			}
			if(result == 2)
				return false;
		}
		editor.controller.saveCategory(category);		
		return true;
	}


	public boolean addDialog(DialogCategory category, Dialog dialog){
		if(editor.controller.dialogs.containsKey(dialog.id)){
			String[] buttons = {"Overwrite", "Increment", "Cancel"};
			int result = JOptionPane.showOptionDialog(this, dialog + "\nDialog found with the same id", "Conflict warning", JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);
			if(result == 0)
				editor.controller.removeDialog(editor.controller.dialogs.get(dialog.id));
			if(result == 1)
				dialog.id = editor.controller.getUniqueDialogID();
			if(result == 2)
				return false;
		}
		if(editor.controller.containsDialogName(category, dialog, dialog.getTitle())){
			String[] buttons = {"Overwrite", "Change", "Cancel"};
			int result = JOptionPane.showOptionDialog(this, dialog + "\nDialog found with the same name in this category", "Conflict warning", JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);
			if(result == 0)
				editor.controller.removeDialog(editor.controller.getDialogFromName(category, dialog, dialog.getTitle()));
			if(result == 1){
	    		while(editor.controller.containsDialogName(category, dialog, dialog.getTitle()))
	    			dialog.setTitle(dialog.getTitle() + "_");
			}
			if(result == 2)
				return false;
		}
		DialogController.instance.saveDialog(category.getID(), dialog);
		return true;
	}
	
	private DialogNode getClipboard(){
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable content = clip.getContents(this);
		if(!content.isDataFlavorSupported(DialogNode.dmselFlavor))
			return null;
		try {
			byte[] bytes = (byte[]) content.getTransferData(DialogNode.dmselFlavor);
			return DialogNode.readFromBytes(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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


	static class DialogNode extends DefaultMutableTreeNode implements Transferable{
		public EnumNodeType type = EnumNodeType.ROOT;
		public static DataFlavor dmselFlavor = new DataFlavor(DialogNode.class, "Test data flavor");
		public DialogNode(Object ob){
			super(ob);
			if(ob instanceof DialogCategory)
				type = EnumNodeType.CATEGORY;
			else if(ob instanceof Dialog)
				type = EnumNodeType.DIALOG;
			else if(ob instanceof DialogOption){
				type = EnumNodeType.OPTION;
			}
		}
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return toBytes();
		}
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{dmselFlavor};
		}
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return dmselFlavor == flavor;
		}

		
		public byte[] toBytes() throws IOException{
			Json json = null;
			if(type == EnumNodeType.CATEGORY){
				json = ((DialogCategory)getUserObject()).data;
			}
			if(type == EnumNodeType.DIALOG){
				json = ((Dialog)getUserObject()).data.copy();
			}
			if(json == null)
				return null;
			
			json.put("NodeType", type.ordinal());
			return json.toBytes();
		}
		
		public static DialogNode readFromBytes(byte[] bytes) throws IOException, JsonException{
			if(bytes == null)
				return null;
			Json json = Json.readFromBytes(bytes);
			
			if(!json.has("NodeType"))
				return null;
			EnumNodeType type = EnumNodeType.values()[json.get("NodeType").getInt()];

			if(type == EnumNodeType.CATEGORY){
				DialogCategory category = new DialogCategory(json);
				return new DialogNode(category);
			}
			if(type == EnumNodeType.DIALOG){
				Dialog dialog = new Dialog(json);
				return new DialogNode(dialog);
			}
			return null;
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
			DialogNode selected;
			try {
				byte[] bytes = (byte[]) support.getTransferable().getTransferData(DialogNode.dmselFlavor);
				selected = DialogNode.readFromBytes(bytes);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	        if(selected == null || selected.type != EnumNodeType.DIALOG)
	        	return false;
			JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
	        DialogNode node = (DialogNode) dl.getPath().getLastPathComponent();

	        return selected.getParent() != node && node.type == EnumNodeType.CATEGORY;
	    }
	    @Override
	    public boolean importData(TransferHandler.TransferSupport support) {
			DialogNode node;
			try {
				byte[] bytes = (byte[]) support.getTransferable().getTransferData(DialogNode.dmselFlavor);
				node = DialogNode.readFromBytes(bytes);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
	        TreePath dest = dl.getPath();
	        DialogNode parent = (DialogNode)dest.getLastPathComponent();
        	Dialog dialog = (Dialog) node.getUserObject();
			DialogCategory category = (DialogCategory) parent.getUserObject();
        	if(this.tree.addDialog(category, dialog)){
				this.tree.refresh();					
		        return true;
        	}
	        return false;
	    }

	    @Override
	    protected Transferable createTransferable(JComponent c) {
	    	return tree.getSelectedNode();
	    }   
	    
	    @Override
	    public int getSourceActions(JComponent c) {  
	        return COPY;  
	    }  
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		
	}
}
