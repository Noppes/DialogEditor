package noppes.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import noppes.dialog.gui.GuiDialogTree;
import noppes.dialog.gui.GuiMenuBar;

public class DialogEditor extends JFrame implements ActionListener{
	
	public DialogEditor(){
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		this.add(BorderLayout.NORTH, new JScrollPane(new GuiDialogTree()));

		
		this.setJMenuBar(new GuiMenuBar());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}
	
	public static void main(String[] args) {
		DialogEditor editor = new DialogEditor();
		editor.setVisible(true);

	}

}
