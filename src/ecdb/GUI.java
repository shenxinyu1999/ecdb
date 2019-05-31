package ecdb;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI {
	JFrame frame;
	JPanel body;
	
	public GUI() {
		frame = new JFrame();
		body = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
		frame.setTitle("Easy Creative");
		frame.setMinimumSize(new Dimension(700, 700));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void displayError(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
