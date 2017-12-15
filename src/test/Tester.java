package test;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.junit.Test;

import utils.Compressor;
import utils.Decompressor;

public class Tester {
	
	
	public static void main(String[] args) {
		try {
			testData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void testData() throws IOException {
		short[] testData = new short[40];
		short value = 1;

		for (int i = 0; i < testData.length; i++) {
			testData[i] = value;
			value += 20;
		}
		
		byte[] muLawBytes = Compressor.compress(testData);
		byte[] decompressedTestData = Decompressor.decompress(muLawBytes);
		ShortBuffer intBuf = ByteBuffer.wrap(decompressedTestData).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
		short[] decompressedTestDataInts = new short[intBuf.remaining()];
		intBuf.get(decompressedTestDataInts);
		
		boolean sameLength = testData.length == decompressedTestDataInts.length;
		
		System.out.println("Same length: " + sameLength);
		System.out.println("Original \t Decompressed");
		for(int i = 0; i < testData.length && i < decompressedTestDataInts.length; i++) {
			short val = (short) decompressedTestDataInts[i];
			System.out.println(testData[i] + "\t" + val);
		}
		
		/*File tmpFile = File.createTempFile("original", ".tmp");
		DataOutputStream dos = new DataOutputStream(new ByteArrayOutputStream());
		DataInputStream dis = new DataInputStream(new FileInputStream(tmpFile));*/
	
		
		
	}
	

}
