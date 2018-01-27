package utils;

import java.io.File;
import java.io.IOException;
import utils.Mulaw;
import utils.Util;

public class Compressor {

	// prevent instantiation
	private Compressor() {}

	public static byte[] compress(short[] input) throws IOException {
		byte[] afterMuLaw = Mulaw.compress(input);
		//return comppress(afterMuLaw);
		return afterMuLaw;
		
	}

	public static byte[] compress(File input) throws IOException {
		return compress(Util.getShortsFromFile(input));
	}

}
