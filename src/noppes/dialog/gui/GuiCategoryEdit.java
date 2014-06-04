package noppes.dialog.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GuiCategoryEdit extends JTabbedPane{
	public GuiCategoryEdit(){
		JComponent panel1 = makeTextPanel("Panel #1");
        addTab("Tab 1", panel1);
	}
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
}
