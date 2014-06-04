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

public class GuiMenuBar extends JMenuBar implements ActionListener{
	private JMenuItem itemLoad;
	private JMenuItem itemSave;
	private JMenuItem itemSaveAs;
	private JMenuItem itemExit;
	
	final JFileChooser fileChooser = new JFileChooser();
	
	public GuiMenuBar(){
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		itemLoad = new JMenuItem("Load", KeyEvent.VK_L);
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(this);
		menu.add(itemLoad);
		
		itemSave = new JMenuItem("Save", KeyEvent.VK_S);
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemSave.addActionListener(this);
		menu.add(itemSave);
		
		itemSaveAs = new JMenuItem("Save as");
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
                System.out.println(file.getAbsolutePath());
            } else {
            	
            }
		}
	}
}
