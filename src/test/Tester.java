package test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;

import utils.Compressor;
import utils.Decompressor;

public class Tester {
	
	
	public static void main(String[] args) throws IOException {
		try {
			testRandomData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int testVal = 609;
		int decTestVal = decompressedValue(testVal);
		int diff = Math.abs(testVal - decTestVal);
		System.out.println("--> " + testVal + " becomes " + decTestVal + " (difference is " + diff + ")");
	}
	
	private static short decompressedValue(int value) throws IOException {
		return byte2short(Decompressor.decompress(Compressor.compress((new short[] { (short) value }))));
	}
	
	private static short byte2short(byte[] data) {
		return (short) ((data[0] << 8) | (data[1]));
	}
	
	public static void testRandomData() throws IOException {
		short[] testData = new short[40];

		for (int i = 0; i < testData.length; i++)
			testData[i] = (short) ThreadLocalRandom.current().nextInt(0, 15001);
		
		byte[] muLawBytes = Compressor.compress(testData);
		byte[] decompressedTestData = Decompressor.decompress(muLawBytes);
		ShortBuffer shortBuf = ByteBuffer.wrap(decompressedTestData).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
		short[] decompressedTestDataShorts = new short[shortBuf.remaining()];
		shortBuf.get(decompressedTestDataShorts);
		
		boolean sameLength = testData.length == decompressedTestDataShorts.length;
		
		System.out.println("Same length of array: " + sameLength);
		System.out.println("Original \t Decompressed \t Difference");
		for(int i = 0; i < testData.length && i < decompressedTestDataShorts.length; i++) {
			short val = (short) decompressedTestDataShorts[i];
			int diff = Math.abs(testData[i] - val);
			System.out.println(testData[i] + "\t\t " + val + "\t\t " + diff);
		}
	}

}
