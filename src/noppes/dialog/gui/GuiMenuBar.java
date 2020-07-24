package noppes.dialog.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import noppes.dialog.DialogEditor;

public class GuiMenuBar extends JMenuBar implements ActionListener{
	private JMenuItem itemLoad;
	private JMenuItem itemExit;
	private JMenuItem saveItem;
	private DialogEditor editor;
	
	final JFileChooser fileChooser = new JFileChooser();
	
	public GuiMenuBar(DialogEditor editor){
		this.editor = editor;
        File file = getDefaultFile();
        if(file != null)
        	fileChooser.setCurrentDirectory(file);
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		itemLoad = new JMenuItem("Load", KeyEvent.VK_L);
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(this);
		menu.add(itemLoad);
		
		saveItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(this);
		menu.add(saveItem);
		
		menu.addSeparator();
		itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(this);
		menu.add(itemExit);
		
		FileFilter filter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return "dialogs folder";
			}
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || arg0.getName().toLowerCase().equals("dialogs");
			}
		};
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	private File getDefaultFile(){
		File home = new File(System.getProperty("user.home"));
		if(!home.exists())
			return null;
        String s = System.getProperty("os.name").toLowerCase();
        if(s.contains("win")){
        	File file = new File(home,"AppData/Roaming/.minecraft");
        	return file.exists()?file:home;
        }
        if(s.contains("linux") || s.contains("unix")){
        	File file = new File(home,".minecraft");
        	return file.exists()?file:home;
        }
        if(s.contains("mac")){
        	File file = new File(home,"Library/Application Support/minecraft");
        	return file.exists()?file:home;
        }
        return home;
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
		if(e.getSource() == saveItem) {
			if(GuiDialogEdit.dialogInstance != null) GuiDialogEdit.dialogInstance.saveDialouge();
		}
	}
}
