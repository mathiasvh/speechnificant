package utils;

import java.io.File;
import java.io.IOException;
import utils.Mulaw;
import utils.Util;
import linearPrediction.LinearPrediction;
import silence_removal.EndPointDetection;;
public class Compressor {

	// prevent instantiation
	private Compressor() {}

	public static byte[] compress(short[] input) throws IOException {
		float[] afterEpd = EndPointDetection.compress(Util.shortArrayToFloatArray(input), 8000);
		short[] afterLPC = LinearPrediction.compress(Util.floatArrayToDoubleArray(afterEpd));
		byte[] afterMuLaw = Mulaw.compress(afterLPC);
		
		return afterMuLaw;	
	}

	public static byte[] compress(File input) throws IOException {
		return compress(Util.getShortsFromFile(input));
	}

}
