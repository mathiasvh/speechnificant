package utils;

import java.io.File;
import java.io.IOException;
import utils.Mulaw;
import utils.Util;
import linearPrediction.LinearPrediction;
import linearPrediction.DiscreteAutocorrelation;
import silence_removal.EndPointDetection;;
public class Compressor {

	// prevent instantiation
	private Compressor() {}

	public static byte[] compress(short[]input) throws IOException {
		short [] inputShorts = input;
		float [] inputFloats = Util.convertShortArrayToFloatArray(inputShorts);
		EndPointDetection epdt = new EndPointDetection(inputFloats,8000);
		float[] epdResult=epdt.doEndPointDetection();
		double[] inputDoubles = Util.convertFloatArrayToDoubleArray(epdResult);
		LinearPrediction lpc = new LinearPrediction(inputDoubles.length,(inputDoubles.length-1));
		   short[][]predictOutput = lpc.applyLinearPredictiveCoding(inputDoubles);
		byte[] afterMuLaw = Mulaw.compress(predictOutput[0]);
		//return comppress(afterMuLaw);
		return afterMuLaw;		
	}

	public static byte[] compress(File input) throws IOException {
		
		return compress(Util.getShortsFromFile(input));
	}

}
