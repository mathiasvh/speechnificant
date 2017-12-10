package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import controller.Controller;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private JFileChooser fc;
	private JButton btnLoadFile, btnCompress, btnDecompress;
	private JPanel panel;
	private JLabel lblCurrentlyLoadFile, lblLastFileCompressedSize;
	
	private File file;
	
	public MainFrame() {
		this.setTitle("Speechnificant");
		this.setResizable(false);
		this.setBounds(0, 0, 450, 270);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.panel = new JPanel(null);
		this.panel.setBounds(0, 0, this.getWidth(), this.getHeight());
	
		JLabel lblInput = new JLabel("Choose input file");
		lblInput.setBounds(10, 15, 120, 25);
		
		btnLoadFile = new JButton("Load file..");
		btnLoadFile.setBounds(150, 15, 150, 25);
		btnLoadFile.addActionListener(this);
		
		JLabel lblLoaded = new JLabel("File currently loaded:");
		lblLoaded.setBounds(10, 45, 130, 25);
		
		lblCurrentlyLoadFile = new JLabel("(none)");
		lblCurrentlyLoadFile.setBounds(150, 45, 300, 25);

		JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		sep1.setBounds(0, 85, this.getWidth(), 1);

		btnCompress = new JButton("Compress");
		btnCompress.setBounds(10, 100, 150, 25);
		btnCompress.addActionListener(this);
		
		JLabel lblLastFileCompressed = new JLabel("Filesize of last compressed file (in kB): ");
		lblLastFileCompressed.setBounds(10, 130, 230, 25);
		
		lblLastFileCompressedSize = new JLabel("N/A");
		lblLastFileCompressedSize.setBounds(250, 130, 50, 25);

		JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
		sep2.setBounds(0, 160, this.getWidth(), 1);

		btnDecompress = new JButton("Decompress");
		btnDecompress.setBounds(10, 175, 150, 25);
		btnDecompress.addActionListener(this);
		
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		this.panel.setLayout(null);
		this.panel.add(lblInput);
		this.panel.add(btnLoadFile);
		this.panel.add(lblLoaded);
		this.panel.add(lblCurrentlyLoadFile);
		this.panel.add(sep1);
		this.panel.add(btnCompress);
		this.panel.add(lblLastFileCompressed);
		this.panel.add(lblLastFileCompressedSize);
		this.panel.add(sep2);
		this.panel.add(btnDecompress);

		getContentPane().add(panel);
		this.panel.setVisible(true);
		
		this.setVisible(true);
	}
	
	public void setFileLastCompressedSize(long size) {
		this.lblLastFileCompressedSize.setText(readableFileSize(size));
	}
	
	private Controller controller = null;
	
	public void setController(Controller c) {
		if (controller == null)
			this.controller = c;
	}
	
	public void setFileLoaded(String fname) {
		this.lblCurrentlyLoadFile.setText(fname);
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
		} else if (e.getSource() == btnCompress) {
			File currentlyLoadedFile = this.controller.getCurrentlyLoadFile();
			if (currentlyLoadedFile == null)
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
			else
				fc.setCurrentDirectory(this.controller.getCurrentlyLoadFile());
			int returnVal = fc.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				controller.compress(file);
			} else {
				// cancel
			}
			
		} else if (e.getSource() == btnDecompress) {
			controller.decompress();
		}
	}
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	

}
