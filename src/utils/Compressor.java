package utils;

import java.io.File;
import java.io.IOException;
import utils.Mulaw;
import utils.Util;
import linearPrediction.LinearPrediction;
import linearPrediction.DiscreteAutocorrelation;

public class Compressor {

	// prevent instantiation
	private Compressor() {}

	public static byte[] compress(short[][] input) throws IOException {
		byte[] afterMuLaw = Mulaw.compress(input[0]);
		//return comppress(afterMuLaw);
		return afterMuLaw;		
	}

	public static byte[] compress(File input) throws IOException {
		short [] inputShorts = Util.getShortsFromFile(input);
		double[] inputDoubles = Util.convertShortArrayToDoubleArray(inputShorts);
		LinearPrediction lpc = new LinearPrediction(inputDoubles.length,(inputDoubles.length-1));
		   short[][]predictOutput = lpc.applyLinearPredictiveCoding(inputDoubles);
		return compress(predictOutput);
	}

}
