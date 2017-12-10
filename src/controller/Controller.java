package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import gui.MainFrame;

public class Controller {
	
	private MainFrame mf;
	
	public Controller(MainFrame mfi) {
		this.mf = mfi;
		this.mf.setController(this);
	}
	
	public void load(File file) {
		createOutputDir();
		
		String fileName = file.getName().replaceAll(".wav", "");
		
		// make dir to work in for current file
		File currentFileDir = new File("out\\" + fileName);
		if (!currentFileDir.exists()) {
			try {
				currentFileDir.mkdir();
			} catch (SecurityException se) {
				System.out.println("Could not create necessary directory.");
			}
		}
		
		String command = "assets\\ffmpeg -i " + file.getAbsolutePath()
		+ " -f s16be -ar 8000 -acodec pcm_s16be " + "out\\" + fileName + "\\loaded.raw";
		boolean executed = execCommand(command);
		if (executed)
			mf.setFileLoaded(file.getName());
	}
	
	private void createOutputDir() {
		File outputDir = new File("out\\");

		// if the directory does not exist, create it
		if (!outputDir.exists()) {
			try {
				outputDir.mkdir();
			} catch (SecurityException se) {
				System.out.println("Could not create necessary directory.");
			}
		}
	}
	
	public void compress() {
		//TODO
	}
	
	public void decompress() {
		//TODO
	}
	
	/**
	 * Executes a command as if it were executed in a MSDOS terminal
	 * @param cmd	The command to be executed
	 * @return boolean	Whether or not the command has been executed, either succesfull or not
	 */
	private boolean execCommand(String cmd) {
		try {
			cmd = "cmd /c " + cmd;
			final Process process = Runtime.getRuntime().exec(cmd);
			final InputStream in = process.getInputStream();
			int ch;
			while ((ch = in.read()) != -1)
				System.out.print((char) ch);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
