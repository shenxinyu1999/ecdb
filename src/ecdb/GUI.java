package ecdb;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI {

	JFrame frame;
	JPanel body;
	
	public GUI() {
		frame = new JFrame();
		body = new JPanel(new FlowLayout());
		frame.add(body);
		frame.setMinimumSize(new Dimension(1920, 1080));
		frame.setTitle("Easy Creative");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void displayMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	public void startConnecting() {
		frame.setTitle("Connecting...");
	}

	public void titleSetName() {
		frame.setTitle("Easy Creative");
	}

	public void login() {
		JButton importButton = new JButton("import");
		JButton exitButton = new JButton("exit");
		
		importButton.setPreferredSize(new Dimension(200, 100));
		exitButton.setPreferredSize(new Dimension(200, 100));
		
		body.add(importButton);
		body.add(exitButton);
		
		exitButton.addActionListener(new ExitListener());
		body.revalidate();
	}
	
	private class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			DatabaseConnectionService.closeConnection();
			frame.dispose();
		}

	}
}
