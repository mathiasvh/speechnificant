package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import gui.MainFrame;

public class Controller {
	
	private MainFrame mf;
	
	public Controller(MainFrame mfi) {
		this.mf = mfi;
		this.mf.setController(this);
	}
	
	private File currentlyLoadedFile = null;
	
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
		
		String outputFile = "out\\" + fileName + "\\loaded.raw";
		
		String command = "assets\\ffmpeg -i " + file.getAbsolutePath()
		+ " -f s16be -ar 8000 -acodec pcm_s16be " + outputFile;
		boolean executed = execCommand(command);
		if (executed) {
			mf.setFileLoaded(file.getName());
			this.currentlyLoadedFile = new File(outputFile);
		}
			
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
	public static boolean execCommand(String cmd) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
            Scanner s = new Scanner(p.getInputStream());
            PrintWriter toChild = new PrintWriter(p.getOutputStream());

            toChild.println("N");    // write to child's stdin
            toChild.close();            // or you can use toChild.flush()
            System.out.println(s.next());
            s.close();
		} catch (Exception e) {
			// No element left in scanner
		}
	    return true;
	}
	/*private boolean execCommand(String cmd) {
		try {
			cmd = "cmd /c " + cmd;
			final Process process = Runtime.getRuntime().exec(cmd);
			final InputStream in = process.getInputStream();
			int ch;
			while ((ch = in.read()) != -1)
				System.out.print((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		String s = null;
		while ((s = stdInput.readLine()) != null) {
		    System.out.println(s);
		}
		return true;
	}*/

}
