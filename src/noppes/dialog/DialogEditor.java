package noppes.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import noppes.dialog.gui.GuiDialogTree;
import noppes.dialog.gui.GuiMenuBar;

public class DialogEditor extends JFrame{
	public static DialogEditor Instance;
	public DialogController controller = new DialogController();
	public GuiDialogTree tree;
	public GuiMenuBar menu;
	public File activeFile;
	private boolean isEdited = false;
	
	public DialogEditor(){
		Instance = this;
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		setResizable(false);
		this.add(BorderLayout.NORTH, new JScrollPane(tree = new GuiDialogTree(this)));
		
		this.setJMenuBar(menu = new GuiMenuBar(this));
	}
	
	public static void main(String[] args) {
		DialogEditor editor = new DialogEditor();
		editor.setVisible(true);
	}

	public void load(File file) {
		try {
			controller.loadCategories(file);
			activeFile = file;
			tree.refresh();
			menu.enableSave();
			setTitle(activeFile.getAbsolutePath());
			setEdited(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(File file){
		try {
			controller.saveCategories(file);
			activeFile = file;
			setTitle(activeFile.getAbsolutePath());
			System.out.println(activeFile.getAbsolutePath());
			setEdited(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setEdited(boolean bo){
		if(isEdited == bo)
			return;
		isEdited = bo;
		setTitle(activeFile.getAbsolutePath() + (isEdited?"*":""));
	}
}
