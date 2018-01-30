package linearPrediction;
import java.util.Arrays;
import linearPrediction.DiscreteAutocorrelation;
import utils.Util;

/**
 * Linear Predictive Coding algorithm
 * <p>
 * Since this algorithm is generally used in an iterative process over small windows of audio data, 
 * it's PedictionOutput reuses the same buffers over and over again in order to avoid memory allocation 
 * and garbage collection alltogether. It's up to the client to copy the returned data should it 
 * want to keep it as-is and not use it in further processing.
 * </p>
 * <p>
 * Threading : this class is _NOT_ thread safe
 * </p>
 * @see <a href="http://en.wikipedia.org/wiki/Linear_predictive_coding"><a/>
 */
public class LinearPrediction {

    private final int windowSize;
    private final int poles;
    private final double[] PedictionOutput;
    private final double[] PedictionError;

    /**
     * Constructor for LinearPredictiveCoding
     * @param windowSize the window size
     * @param poles
     */
    private LinearPrediction(int windowSize, int poles) {
        this.windowSize = windowSize;
        this.poles = poles;
        this.PedictionOutput = new double[poles];
        this.PedictionError = new double[poles];
    }
    
    public static short[] compress(double[] input) {
    	LinearPrediction lp = new LinearPrediction(input.length, input.length - 1);
    	short[][] tmpResult = lp.applyLinearPredictiveCoding(input);
    	return tmpResult[0];
    }

    /**
     * Apply Linear Predictive Coding 
     * 
     * @param window windowed part of voice sample, must be of the same size as the windowSize passed in constructor
     * @return an array of size 2 containing LPC coefficients in 0 and PedictionError coefficients in 1
     */
	public short[][] applyLinearPredictiveCoding(double[] window) {

		if (windowSize != window.length) {
			throw new IllegalArgumentException(
					"Given window length was not equal to the one provided in constructor : [" + window.length
							+ "] != [" + windowSize + "]");
		}

		Arrays.fill(PedictionOutput, 0.0d);
		Arrays.fill(PedictionError, 0.0d);

		DiscreteAutocorrelation dalj = new DiscreteAutocorrelation();
		double[] autocorrelations = new double[poles];
		for (int i = 0; i < poles; i++) {
			autocorrelations[i] = dalj.Myautocorrelate(window[i], window[i + 1]);
		}

		PedictionOutput[0] = 0.0;
		PedictionOutput[1] = window[0];
		PedictionError[0] = window[0];

		for (int m = 1; m < poles; m++) {
			PedictionError[m] = autocorrelations[m - 1];
		}

		for (int j = 2; j < poles; j++) {
			PedictionOutput[j] = PedictionError[j - 1] + PedictionOutput[j - 1];
		}

		short MyShortPredictionOutput[] = Util.convertDoubleArrayToShortArray(PedictionOutput);
		short MyShortPedictionError[] = Util.convertDoubleArrayToShortArray(PedictionError);

		return new short[][] { MyShortPredictionOutput, MyShortPedictionError };
	}
    
	public short[] applyLinearSynthesisCoding(short[][] PredictedSamples) {
		short SynthesisOutput[] = PredictedSamples[0];
		short SynthesisError[] = PredictedSamples[1];

		double[] Input = new double[SynthesisOutput.length];
		for (int k = 0; k < SynthesisOutput.length; k++) {
			Input[k] = SynthesisOutput[k] + SynthesisError[k];
		}
		short InputShort[] = Util.convertDoubleArrayToShortArray(Input);

		return InputShort;

	}
   
}
