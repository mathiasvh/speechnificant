package utils;

import java.io.File;
import java.io.IOException;

public class Compressor {

	// prevent instantiation
	private Compressor() {}

	public static byte[] compress(short[] input) throws IOException {
		return Mulaw.compress(input);
	}

	public static byte[] compress(File input) throws IOException {
		return compress(Util.getShortsFromFile(input));
	}

}
