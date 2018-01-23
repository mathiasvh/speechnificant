package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import controller.Controller;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	
	private JFileChooser fc;
	private JButton btnLoadFile, btnCompress, btnDecompress;
	private JPanel panel;
	private JLabel lblCurrentlyLoadFile, lblSizeLoadedFile, lblLastFileCompressedSize, lblLastFileDecompressedSize,
			lblCompressionPercentage, lblMSEValue;

	private File file;
	
	public MainFrame() {
		this.setTitle("Speechnificant");
		this.setResizable(false);
		this.setBounds(0, 0, 560, 285);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.panel = new JPanel(null);
		this.panel.setBounds(0, 0, this.getWidth(), this.getHeight());
	
		JLabel lblInput = new JLabel("Choose input file");
		lblInput.setBounds(10, 15, 120, 25);
		
		btnLoadFile = new JButton("Load file..");
		btnLoadFile.setBounds(150, 15, 150, 25);
		btnLoadFile.setToolTipText("Must be a .wav file");
		btnLoadFile.addActionListener(this);
		
		JLabel lblLoaded = new JLabel("File currently loaded:");
		lblLoaded.setBounds(10, 45, 130, 25);
		
		lblCurrentlyLoadFile = new JLabel("(none)");
		lblCurrentlyLoadFile.setBounds(150, 45, 300, 25);
		
		JLabel lblSizeLoaded = new JLabel("File size: ");
		lblSizeLoaded.setBounds(10, 60, 150, 25);
		
		lblSizeLoadedFile = new JLabel("N/A");
		lblSizeLoadedFile.setBounds(150, 60, 50, 25);

		JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		sep1.setBounds(0, 100, this.getWidth(), 1);

		btnCompress = new JButton("Compress");
		btnCompress.setBounds(10, 115, 150, 25);
		btnCompress.setToolTipText("Must be a .raw file (pcm_16be)");
		btnCompress.addActionListener(this);
		
		JLabel lblLastFileCompressed = new JLabel("File size of last compressed file: ");
		lblLastFileCompressed.setBounds(10, 145, 200, 25);
		
		lblLastFileCompressedSize = new JLabel("N/A");
		lblLastFileCompressedSize.setBounds(210, 145, 50, 25);

		JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
		sep2.setBounds(0, 175, 315, 1);

		btnDecompress = new JButton("Decompress");
		btnDecompress.setBounds(10, 190, 150, 25);
		btnDecompress.setToolTipText("Must be a .sph file");
		btnDecompress.addActionListener(this);
		
		JLabel lblLastFileDecompressed = new JLabel("File size of last decompressed file: ");
		lblLastFileDecompressed.setBounds(10, 210, 215, 25);
		
		lblLastFileDecompressedSize = new JLabel("N/A");
		lblLastFileDecompressedSize.setBounds(225, 210, 50, 25);
		
		JSeparator sep3 = new JSeparator(SwingConstants.VERTICAL);
		sep3.setBounds(315, 100, 1, this.getHeight() - 100);
		
		JLabel lblCompression= new JLabel("Compression percentage:");
		lblCompression.setBounds(325, 125, 160, 25);
		
		lblCompressionPercentage = new JLabel("N/A");
		lblCompressionPercentage.setBounds(490, 125, 50, 25);
		
		JLabel lblMSE = new JLabel("Mean Squared Error:");
		lblMSE.setBounds(325, 205, 130, 25);
		
		lblMSEValue = new JLabel("N/A");
		lblMSEValue.setBounds(460, 205, 50, 25);
		
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		this.panel.setLayout(null);
		this.panel.add(lblInput);
		this.panel.add(btnLoadFile);
		this.panel.add(lblLoaded);
		this.panel.add(lblCurrentlyLoadFile);
		this.panel.add(lblSizeLoaded);
		this.panel.add(lblSizeLoadedFile);
		this.panel.add(sep1);
		this.panel.add(btnCompress);
		this.panel.add(lblLastFileCompressed);
		this.panel.add(lblLastFileCompressedSize);
		this.panel.add(sep2);
		this.panel.add(btnDecompress);
		this.panel.add(lblLastFileDecompressed);
		this.panel.add(lblLastFileDecompressedSize);
		this.panel.add(sep3);
		this.panel.add(lblCompression);
		this.panel.add(lblCompressionPercentage);
		this.panel.add(lblMSE);
		this.panel.add(lblMSEValue);

		getContentPane().add(panel);
		this.panel.setVisible(true);
		
		this.setVisible(true);
	}
	
	public void setFileLastCompressedSize(long size) {
		this.lblLastFileCompressedSize.setText(readableFileSize(size));
	}
	
	public void setFileLastDecompressedSize(long size) {
		this.lblLastFileDecompressedSize.setText(readableFileSize(size));
	}
	
	public void setMSE(double mse) {
		System.out.println(mse);
		this.lblMSEValue.setText(round(mse, 2));
	}
	
	public void setCompressionPercentage(double percentage) {
		this.lblCompressionPercentage.setText(round(percentage, 2)  + "%");
	}
	
	private Controller controller = null;
	
	public void setController(Controller c) {
		if (controller == null)
			this.controller = c;
	}
	
	public void setFileLoaded(String fname, long size) {
		this.lblCurrentlyLoadFile.setText(fname);
		this.lblSizeLoadedFile.setText(readableFileSize(size));
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (controller == null)
			return;
		
		if (e.getSource() == btnLoadFile) {
			int returnVal = fc.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				if (!file.getName().endsWith(".wav")) {
					JOptionPane.showMessageDialog (null, "Only .wav files are allowed to be loaded!", "Invalid file format", JOptionPane.ERROR_MESSAGE);
					file = null;
					return;
				}
				
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
				if (!file.getName().endsWith(".pcm")) {
					JOptionPane.showMessageDialog (null, "Only .pcm files are allowed to compressed!", "Invalid file format", JOptionPane.ERROR_MESSAGE);
					file = null;
					return;
				}
				controller.compress(file);
			} else {
				// cancel
			}
			
		} else if (e.getSource() == btnDecompress) {
			File currentlyLoadedFile = this.controller.getCurrentlyLoadFile();
			if (currentlyLoadedFile == null)
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
			else
				fc.setCurrentDirectory(this.controller.getCurrentlyLoadFile());
			int returnVal = fc.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				if (!file.getName().endsWith(".sph")) {
					JOptionPane.showMessageDialog (null, "Only .sph files are allowed to compressed!", "Invalid file format", JOptionPane.ERROR_MESSAGE);
					file = null;
					return;
				}
				controller.decompress(file);
			} else {
				// cancel
			}
		}
	}
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static String round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.toString();
	}
	

}
