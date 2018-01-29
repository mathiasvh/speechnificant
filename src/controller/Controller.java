package controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		
		String outputFile = "out\\" + fileName + "\\loaded.pcm";
		
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
		
		long compressedFileSize = new File(compressedFile).length();
		long originalFileSize = file.length();
		this.mf.setFileLastCompressedSize(compressedFileSize);
		double compressionPercentage = Math.abs(((double) originalFileSize - (double) compressedFileSize) / (double) originalFileSize) * 100;
		this.mf.setCompressionPercentage(compressionPercentage);
	}
	
	public void decompress(File file) {
		createOutputDir();

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
		
		String decompressedFilePath = currentFileDir.getAbsolutePath() + "\\" + "decompressed.pcm";
		try (FileOutputStream fos = new FileOutputStream(decompressedFilePath)) {
			decompressedBytes = Decompressor.decompress(file);
			fos.write(decompressedBytes);
			fos.close();
		} catch (IOException e) {}
		
		

		String outputFile = "out\\" + getProjectPath(new File(decompressedFilePath)) + "\\decompressed.wav";
		String command = "assets\\ffmpeg -f s16be -ar 8000 -ac 1 -i " + decompressedFilePath
		+ " -ar 8000 -ac 1 " + outputFile;
		
		boolean executed = execCommand(command);
		if (executed) {
			this.mf.setFileLastDecompressedSize(new File(decompressedFilePath).length());
			
			// calculate MSE
			double totalMSE = 0.0d;
			try { 
				File decompressedFile = new File(decompressedFilePath);
				DataInputStream disDecompressed = new DataInputStream(new FileInputStream(decompressedFile));
				long decompressedFileLength = decompressedFile.length();
				
				long originalFileLength = this.currentlyLoadedFile.length();
				DataInputStream disOriginal = new DataInputStream(new FileInputStream(this.currentlyLoadedFile));

				int index = 0;
				
				while (index < originalFileLength && index < decompressedFileLength) {
					short original = disOriginal.readShort();
					short decomp = disDecompressed.readShort();
					
					totalMSE += (double) Math.pow(decomp - original, 2);
					
					index += 2;
				}

				disDecompressed.close();
				disOriginal.close();
				
				totalMSE = totalMSE / (index/2);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.mf.setMSE(totalMSE);
			
		}
		
		// delete pcm file if wav file is succesfully created
		File f = new File(decompressedFilePath);
		if (f.exists() && !f.isDirectory())
			(new File(decompressedFilePath)).delete();
		
		
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
		int indexLastDot = file.getName().lastIndexOf(".");
		String fileName = file.getName().substring(0, indexLastDot);
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
