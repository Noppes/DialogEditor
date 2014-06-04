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
	public DialogController controller = new DialogController();
	public GuiDialogTree tree;
	public GuiMenuBar menu;
	public File activeFile;
	
	public DialogEditor(){
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
