package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import controller.Controller;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private JFileChooser fc;
	private JButton btnLoadFile;
	
	private File file;
	
	public MainFrame() {
		this.setTitle("Speechnificant");
		this.setResizable(false);
		this.setBounds(0, 0, 450, 430);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JLabel lblInput = new JLabel("Choose input file");
		lblInput.setBounds(10, 15, 120, 25);
		
		btnLoadFile = new JButton("Load file..");
		btnLoadFile.setBounds(150, 15, 150, 25);
		btnLoadFile.addActionListener(this);
		
		fc = new JFileChooser();
		
		this.setLayout(null);
		this.add(lblInput);
		this.add(btnLoadFile);

		this.setVisible(true);
	}
	
	private Controller controller = null;
	
	public void setController(Controller c) {
		if (controller == null)
			this.controller = c;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (controller == null)
			return;
		
		if (e.getSource() == btnLoadFile) {
			int returnVal = fc.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				controller.load(file);
			} else {
				// cancel
			}
		}
	}
	

	

}
