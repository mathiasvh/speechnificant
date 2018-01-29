package linearPrediction;
import java.util.*;
import java.util.ArrayList;
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
 * @author Tharun
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
    public LinearPrediction(int windowSize, int poles) {
        this.windowSize = windowSize;
        this.poles = poles;
        this.PedictionOutput = new double[poles];
        this.PedictionError = new double[poles];
       
        

    }

    /**
     * Apply Linear Predictive Coding 
     * 
     * @param window windowed part of voice sample, must be of the same size as the windowSize passed in constructor
     * @return an array of size 2 containing LPC coefficients in 0 and PedictionError coefficients in 1
     */
    public double[][] applyLinearPredictiveCoding(double[] window) {
    	
        
        if(windowSize != window.length) {
            throw new IllegalArgumentException("Given window length was not equal to the one provided in constructor : [" 
                    + window.length +"] != [" + windowSize + "]");
        }
        
        
        Arrays.fill(PedictionOutput, 0.0d);
        Arrays.fill(PedictionError, 0.0d);
       

        DiscreteAutocorrelation dalj = new DiscreteAutocorrelation();
        double[] autocorrelations = new double[poles];
        for(int i = 0; i < poles; i++) {
        
        //   autocorrelations[i] = dalj.autocorrelate(window, i);
       	autocorrelations[i] = dalj.Myautocorrelate(window[i], window[i+1]);
      // 	System.out.println(autocorrelations[i]+"autoii");
        }
        
        PedictionOutput[0]=0.0;
        PedictionOutput[1]=window[0];
        PedictionError[0]=window[0];
       
      //  PedictionError[0] = autocorrelations[0];
        for (int m = 1; m < poles; m++) {
            PedictionError[m] = autocorrelations[m-1];
             }
     
       for(int j=2;j<poles;j++) {
    	   PedictionOutput[j] = PedictionError[j-1]+PedictionOutput[j-1];
       }
//   Testing purpose
       //for(int h=0;h<poles;h++) {
//    	   System.out.println(PedictionOutput[h]+"outhhhh");
//       }
  
      
        
        return new double[][] { PedictionOutput, PedictionError };
    }
    
    public double[] applyLinearSynthesisCoding(double[][] PredictedSamples) {
    double 	SynthesisOutput[] = PredictedSamples[0];
    double 	SynthesisError[] = PredictedSamples[1];
    
    double[] Input = new double[SynthesisOutput.length];
    for (int k=0;k<SynthesisOutput.length;k++) {
    	Input[k] = SynthesisOutput[k]+SynthesisError[k];
    }
    
    return Input;
    
    }
    	
    
    
 // Testing purpose
   
//     public static void main(String[] args) {
//    	double [] myList = {1,5,4,7};
//  
//    	 LinearPrediction lp = new LinearPrediction(4,3);
//    lp.applyLinearPredictiveCoding(myList);
//    double[][]Output = lp.applyLinearPredictiveCoding(myList);
//    lp.applyLinearSynthesisCoding(Output);
//    	
//		
//	}
   
}
