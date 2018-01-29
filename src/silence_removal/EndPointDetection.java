package silence_removal;

import java.io.File;
import java.io.IOException;

import utils.Util;

public class EndPointDetection {

	private float[] originalSignal; // input
	private float[] silenceRemovedSignal;// output
	private int samplingRate;
	private int firstSamples;
	private int samplePerFrame;

	public EndPointDetection(float[] originalSignal, int samplingRate) {
		this.originalSignal = originalSignal;
		this.samplingRate = samplingRate;
		samplePerFrame = this.samplingRate / 1000;
		firstSamples = samplePerFrame * 200;// according to formula
	}

	public float[] doEndPointDetection() {
		float[] voiced = new float[originalSignal.length];// for identifying
															// each
		// sample whether it is
		// voiced or unvoiced
		float sum = 0;
		double sd = 0.0;
		double m = 0.0;
		System.out.println(firstSamples+"fssfsf");

		// 1. calculation of mean
		for (int i = 0; i < firstSamples; i++) {
			//System.out.println(originalSignal[i]+"osss");
			sum += originalSignal[i];
		}
		// System.err.println("total sum :" + sum);
		m = sum / firstSamples;// mean
		System.out.println(m+"meann");
		sum = 0;// reuse var for S.D.

		// 2. calculation of Standard Deviation
		for (int i = 0; i < firstSamples; i++) {
			sum += Math.pow((originalSignal[i] - m), 2);
		}
		sd = Math.sqrt(sum / firstSamples);
		System.out.println(sd+"standarddevvv");
		// System.err.println("summm sum :" + sum);
		// System.err.println("mew :" + m);
		// System.err.println("sigma :" + sd);

		// 3. identifying whether one-dimensional Mahalanobis distance function
		// i.e. |x-u|/s greater than ####3 or not,
		for (int i = 0; i < originalSignal.length; i++) {
			//System.out.println(originalSignal[0]+"origi00");
			// System.out.println("x-u/SD  ="+(Math.abs(originalSignal[i] -u ) /
			// sd));
			if ((Math.abs(originalSignal[i] - m) / sd) > 1) {
				
				voiced[i] = 1;
			}
			else {
				voiced[i] = 0;
			}
			//System.out.println(voiced[i]+"voiceiiii");
		}

		// 4. calculation of voiced and unvoiced signals
		// mark each frame to be voiced or unvoiced frame
		int frameCount = 0;
		int usefulFramesCount = 1;
		int count_voiced = 0;
		int count_unvoiced = 0;
		int voicedFrame[] = new int[originalSignal.length / samplePerFrame];
		int loopCount = originalSignal.length - (originalSignal.length % samplePerFrame);// skip
																							// the
																							// last
		for (int i = 0; i < loopCount; i += samplePerFrame) {
			count_voiced = 0;
			count_unvoiced = 0;
			for (int j = i; j < i + samplePerFrame; j++) {
				if (voiced[j] == 1) {
					count_voiced++;
				}
				else {
					count_unvoiced++;
				}
			}
		//	System.out.println(count_voiced+"voiceddd"+count_unvoiced+"unvoiceeddd");
			if (count_voiced > count_unvoiced) {
				usefulFramesCount++;
				voicedFrame[frameCount++] = 1;
			}
			else {
				voicedFrame[frameCount++] = 0;
			}
			//System.out.println(usefulFramesCount+"voiceiiiiFCCCC");
		}

		// 5. silence removal
		silenceRemovedSignal = new float[usefulFramesCount * samplePerFrame];
		int k = 0;
		for (int i = 0; i < frameCount; i++) {
			if (voicedFrame[i] == 1) {
				for (int j = i * samplePerFrame; j < i * samplePerFrame + samplePerFrame; j++) {
					silenceRemovedSignal[k++] = originalSignal[j];
				}
			}
		}
	//	System.out.println(silenceRemovedSignal+"srrr");
		for(int i=0;i<silenceRemovedSignal.length;i++) {
			System.out.println(silenceRemovedSignal[i]+"srrarray");
		}
		// end
		return silenceRemovedSignal;
	}
	public static void main(String[] args) throws IOException {
		
		//float signal[] = {1,43,45,6,0,15,0,76,00,67,12,14};
		 File myWavFile = new File("C:/Users/mynor/Desktop/demo1.wav");
		 short [] inputShorts = Util.getShortsFromFile(myWavFile);
		 float [] inputFloats = Util.convertShortArrayToFloatArray(inputShorts);
		
		EndPointDetection epd = new EndPointDetection(inputFloats,8000);
		epd.doEndPointDetection();
	}
	
}
