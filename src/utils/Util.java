package utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

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

	public static double[] convertShortArrayToDoubleArray(short[] input) {
		double[] result = new double[input.length];
		for (int i = 0; i < input.length; i++)
			result[i] = (short) input[i];
		return result;
	}
	
	public static float[] shortArrayToFloatArray(short[] input) {
		float[] result = new float[input.length];
		for (int j = 0; j < input.length; j++)
			result[j] = (short) input[j];
		return result;
	}

	public static double[] floatArrayToDoubleArray(float[] input) {
		double[] result = new double[input.length];
		for (int i = 0; i < input.length; i++)
			result[i] = (float) input[i];
		return result;
	}
	
	
	public static short[] convertDoubleArrayToShortArray(double[] input) {
		short[] result = new short[input.length];
		for (int i = 0; i < input.length; i++)
			result[i] = (short) input[i];
		return result;
	}
	
	public static byte[] getBytesFromFile(File input) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(input));

		int size = (int) Math.ceil(input.length());
		byte[] muLawBytes = new byte[size];
		dis.readFully(muLawBytes);
		dis.close();

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
}
