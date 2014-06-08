package noppes.dialog.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import noppes.dialog.DialogEditor;
import noppes.dialog.nbt.CompressedStreamTools;
import noppes.dialog.nbt.NBTTagCompound;

public class GuiMenuBar extends JMenuBar implements ActionListener{
	private JMenuItem itemLoad;
	private JMenuItem itemSave;
	private JMenuItem itemSaveAs;
	private JMenuItem itemExit;
	private DialogEditor editor;
	
	final JFileChooser fileChooser = new JFileChooser();
	
	public GuiMenuBar(DialogEditor editor){
		this.editor = editor;
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		itemLoad = new JMenuItem("Load", KeyEvent.VK_L);
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(this);
		menu.add(itemLoad);
		
		
		itemSave = new JMenuItem("Save", KeyEvent.VK_S);
		itemSave.setEnabled(false);
		itemSave.addActionListener(this);
		menu.add(itemSave);
		
		itemSaveAs = new JMenuItem("Save as");
		itemSaveAs.setEnabled(false);
		itemSaveAs.addActionListener(this);
		menu.add(itemSaveAs);

		menu.addSeparator();
		itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(this);
		menu.add(itemExit);
		
		FileFilter filter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return ".dat";
			}
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || arg0.getName().toLowerCase().endsWith(".dat");
			}
		};
		fileChooser.setFileFilter(filter);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == itemExit)
			System.exit(0);
		if(e.getSource() == itemLoad){
            int returnVal = fileChooser.showOpenDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
            	editor.load(file);
            }
		}
		if(e.getSource() == itemSave){
			editor.save(editor.activeFile);
		}
		if(e.getSource() == itemSaveAs){
			System.out.println("save");
            int returnVal = fileChooser.showSaveDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
            	editor.save(file);
            }
		}
	}

	public void enableSave() {
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemSave.setEnabled(true);
		itemSaveAs.setEnabled(true);
		
	}
}
