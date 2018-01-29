package utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import linearPrediction.LinearPrediction;
import linearPrediction.DiscreteAutocorrelation;

import javax.sound.sampled.UnsupportedAudioFileException;



public class Util {
	
	public static  short[] getShortsFromFile(File input) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(input));

		long size = (long) Math.ceil(input.length() / 2);
		LinkedList<Short> shortList = new LinkedList<Short>();

		for (int i = 0; i < size; i++)
			shortList.add(dis.readShort());
		dis.close();

		short[] inputShortsArray = new short[shortList.size()];
		int index = 0;
		for (Short s : shortList)
			inputShortsArray[index++] = (short) s; 
		return inputShortsArray;
	}
	public static void printShortsArray(short[] array) {
		for(int i = 0; i < array.length; i++) {
		//	System.out.println(array[i]);
			
		}
	//	System.out.println(array);
		//Util.convertShortArrayToDoubleArray(array);
	}
	public static void printDoubleArray(double[] array) {
		
		for(int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
			
		}
	}
	public static double[] convertShortArrayToDoubleArray(short[] Array) {
		
		double[] data = new double[Array.length];
		for(int j = 0; j < Array.length; j++) {
		data[j]=  (short) Array[j];
		}
		
		
		return data;
		}
	
public static float[] convertShortArrayToFloatArray(short[] Array) {
		
		float[] data = new float[Array.length];
		for(int j = 0; j < Array.length; j++) {
		data[j]=  (short) Array[j];
		}
		
		
		return data;
		}
	
	
public static short[] convertDoubleArrayToShortArray(double[] Array) {
		
		short[] data1 = new short[Array.length];
		for(int j = 0; j < Array.length; j++) {
		data1[j]=  (short) Array[j];
		}
		
		return data1;
		
	}
	
	public static byte[] getBytesFromFile(File input) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(input));

		int size = (int) Math.ceil(input.length());
		byte[] muLawBytes = new byte[size];
		dis.readFully(muLawBytes);
		dis.close();
		
		
		System.out.println(muLawBytes);
		return muLawBytes;
	}

	public static short byte2short(byte[] data) {
		return (short) ((data[0] << 8) | (data[1]));
	}

	public static short byte2shortAlternative(byte[] data) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.put(data[0]);
		bb.put(data[1]);
		return bb.getShort(0);
	}
	public static void main(String[] args) throws IOException  {
		   File myWavFile = new File("/Users/tharun/Documents/SpeechCompress/male.wav");		 
		   short [] inputShorts = Util.getShortsFromFile(myWavFile);
		   
		   double[] inputDoubles = Util.convertShortArrayToDoubleArray(inputShorts);
		  // Util.printDoubleArray(inputDoubles);
		  // System.out.println(inputDoubles+"ipdoubs");
		  LinearPrediction lpc = new LinearPrediction(inputDoubles.length,(inputDoubles.length-1));
		   short[][]predictOutput = lpc.applyLinearPredictiveCoding(inputDoubles);
		  lpc.applyLinearSynthesisCoding(predictOutput);
		   
		   
		   
		   
 	}
}
