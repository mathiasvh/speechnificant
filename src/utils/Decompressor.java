package utils;

import java.io.File;
import java.io.IOException;

public class Decompressor {

	// prevent instantiation
	private Decompressor() {}

	public static byte[] decompress(byte[] muLawBytes) throws IOException {
		return Mulaw.decompress(muLawBytes);
	}

	public static byte[] decompress(File input) throws IOException {
		return decompress(Util.getBytesFromFile(input));
	}

}
