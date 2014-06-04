package noppes.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import noppes.dialog.gui.GuiDialogTree;
import noppes.dialog.gui.GuiMenuBar;
import noppes.dialog.nbt.CompressedStreamTools;
import noppes.dialog.nbt.NBTTagCompound;

public class DialogEditor extends JFrame implements ActionListener{
	public static DialogEditor Instance;

	public HashMap<Integer,DialogCategory> categories = new HashMap<Integer, DialogCategory>();
	public HashMap<Integer,Dialog> dialogs = new HashMap<Integer, Dialog>();
	
	public DialogEditor(){
		Instance = this;
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		this.add(BorderLayout.NORTH, new JScrollPane(new GuiDialogTree(this)));

		
		this.setJMenuBar(new GuiMenuBar(this));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}
	
	public static void main(String[] args) {
		DialogEditor editor = new DialogEditor();
		editor.setVisible(true);

	}

	public void load(File file) {

        try {
			NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
