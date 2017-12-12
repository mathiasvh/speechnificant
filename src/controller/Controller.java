package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import utils.Compressor;
import utils.Decompressor;
import view.MainFrame;

public class Controller {
	
	private MainFrame mf;
	
	public Controller(MainFrame mfi) {
		this.mf = mfi;
		this.mf.setController(this);
	}
	
	public File getCurrentlyLoadFile() {
		return this.currentlyLoadedFile;
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
			mf.setFileLoaded(file.getName(), file.length());
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
	
	public void compress(File file) {
		createOutputDir();
		String fileName = file.getName().replaceAll(".raw", "");

		// make dir to work in for current file
		File currentFileDir = new File("out\\" + getProjectPath(file));
		if (!currentFileDir.exists()) {
			try {
				currentFileDir.mkdir();
			} catch (SecurityException se) {
				System.out.println("Could not create necessary directory.");
			}
		}

		byte[] compressedBytes;
		
		String compressedFile = currentFileDir.getAbsolutePath() + "\\" + "compressed.sph";
		try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
			compressedBytes = Compressor.compress(file);
			fos.write(compressedBytes);
			fos.close();
		} catch (IOException e) {}
		
		this.mf.setFileLastCompressedSize(new File(compressedFile).length());
	}
	
	public void decompress(File file) {
		createOutputDir();
		String fileName = file.getName().replaceAll(".sph", "");

		// make dir to work in for current file
		File currentFileDir = new File("out\\" + getProjectPath(file));
		if (!currentFileDir.exists()) {
			try {
				currentFileDir.mkdir();
			} catch (SecurityException se) {
				System.out.println("Could not create necessary directory.");
			}
		}

		byte[] decompressedBytes;
		// TODO if after .dec to .wav the wav is found, delete .dec
		String decompressedFile = currentFileDir.getAbsolutePath() + "\\" + fileName + ".dec";
		try (FileOutputStream fos = new FileOutputStream(decompressedFile)) {
			decompressedBytes = Decompressor.decompress(file);
			fos.write(decompressedBytes);
			fos.close();
		} catch (IOException e) {}
		
		
		String outputFile = "out\\" + getProjectPath(new File(decompressedFile)) + "\\decompressed.wav";
		String command = "assets\\ffmpeg -i " + decompressedFile
		+ /*" -f wav -ar 8000 -acodec wav " +*/ outputFile;
		System.out.println(command);
		boolean executed = execCommand(command);
		if (executed) {
			mf.setFileLoaded(file.getName(), file.length());
			this.currentlyLoadedFile = new File(outputFile);
		}
		
		this.mf.setFileLastDecompressedSize(new File(decompressedFile).length());
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

            toChild.println("y"); // write yes in case it asks to overwrite existing file
            toChild.close();
            System.out.println(s.next());
            s.close();
		} catch (Exception e) {
			// No element left in scanner
		}
	    return true;
	}
	
	private String getProjectPath(File file) {
		String fileName = file.getName().replaceAll(".raw", "").replaceAll(".sph", "").replaceAll(".dec", "").replaceAll(".wav", "");
		if(fileName.equals("loaded") || fileName.equals("compressed") || fileName.equals("decompressed"))
			return getLastDirectoryInPath(file);
		else
			return fileName;
	}
	
	private String getLastDirectoryInPath(File file) {
		String path = file.getAbsolutePath();
		String nameCutOff = path.substring(0, path.lastIndexOf("\\"));
		return nameCutOff.substring(nameCutOff.lastIndexOf("\\")).replace("\\", "");
	}
}
