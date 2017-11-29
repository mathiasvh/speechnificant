package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private JFileChooser fc;
	private JButton btnOpenFile;
	
	private File file;
	
	public MainFrame() {
		this.setTitle("Speechnificant");
		this.setResizable(false);
		this.setBounds(0, 0, 450, 430);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JLabel lblInput = new JLabel("Choose input file");
		lblInput.setBounds(10, 15, 120, 25);
		
		btnOpenFile = new JButton("Open file..");
		btnOpenFile.setBounds(150, 15, 150, 25);
		btnOpenFile.addActionListener(this);
		
		fc = new JFileChooser();
		
		this.setLayout(null);
		this.add(lblInput);
		this.add(btnOpenFile);

		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOpenFile) {
			int returnVal = fc.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				String command = "assets\\ffmpeg -i " + "examples\\" + file.getName()
				+ " -f s16be -ar 8000 -acodec pcm_s16be " + "examples\\" + file.getName().replaceAll(".wav", "")
				+ ".raw";
				execCommand(command);
			} else {
				// cancel
			}
		}
	}
	

	/**
	 * Executes a command as if it were executed in a MSDOS terminal
	 * @param cmd	The command to be executed
	 */
	private void execCommand(String cmd) {
		try {
			cmd = "cmd /c " + cmd;
			final Process process = Runtime.getRuntime().exec(cmd);
			final InputStream in = process.getInputStream();
			int ch;
			while ((ch = in.read()) != -1)
				System.out.print((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
