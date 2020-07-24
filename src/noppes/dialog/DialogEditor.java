package noppes.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import noppes.dialog.gui.GuiDialogTree;
import noppes.dialog.gui.GuiMenuBar;

public class DialogEditor extends JFrame{
	public static DialogEditor Instance;
	public DialogController controller = new DialogController();
	public GuiDialogTree tree;
	public GuiMenuBar menu;
	public File activeFile;
	
	public DialogEditor(){
		Instance = this;
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		setResizable(true);
		this.add(BorderLayout.NORTH, new JScrollPane(tree = new GuiDialogTree(this)));
		
		this.setJMenuBar(menu = new GuiMenuBar(this));
		this.setTitle("Load a <world> folder to get started");
	} 
	
	public static void main(String[] args) {
		DialogEditor editor = new DialogEditor();
		editor.setVisible(true);
	}

	public void load(File file) {
		if(file.getAbsolutePath().endsWith("dialogs")){
			start(file);
			return;
		}
		List<String> list = Arrays.asList(file.list());
		if(list.contains("dialogs")){
			start(new File(file, "dialogs"));
			return;
		}

		if(list.contains("customnpcs")){
			file = new File(file, "customnpcs");
			list = Arrays.asList(file.list());
			if(list.contains("dialogs")){
				start(new File(file, "dialogs"));
				return;
			}
		}
	}
	
	private void start(File file){
		try {
			if(!file.isDirectory())
				return;
			controller.loadCategories(file);
			activeFile = file;
			tree.refresh();
			setTitle(activeFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
