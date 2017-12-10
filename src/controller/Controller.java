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
		String command = "assets\\ffmpeg -i " + "examples\\" + file.getName()
		+ " -f s16be -ar 8000 -acodec pcm_s16be " + "examples\\" + file.getName().replaceAll(".wav", "")
		+ ".raw";
		execCommand(command);
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
