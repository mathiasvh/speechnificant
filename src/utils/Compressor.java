package utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

public class Compressor {
	
	// prevent instantiation
	private Compressor() {}
	
	public static byte[] compress(File input) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(input));
		
		long size = (long) Math.ceil(input.length() /2);
		LinkedList<Short> shortList = new LinkedList<Short>();
		
		for (int i = 0; i < size; i++)
			shortList.add(dis.readShort());
		dis.close();
		
		short[] inputShortsArray = new short[shortList.size()];
		int index = 0;
		for(Short s : shortList)
			inputShortsArray[index++] = (short) s;
		
		return compress(inputShortsArray);
	}
	
	public static byte[] compress(short[] input) throws IOException {
		long nbShorts = input.length;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);
		
		if (nbShorts == 0L)
			return null;
			
		
		int counter = 0;
		
		while (counter < nbShorts) {
			try {
				byte ulawByte = encode(input[counter]);
				dos.writeByte(ulawByte);
				counter++;
			} catch (IndexOutOfBoundsException ioobe) {
				// index too high
			}
			//System.out.print("0x" + Integer.toHexString(ulawByte & 0xff) + " ");

		}
		
		return outputStream.toByteArray();
	}
	
	@SuppressWarnings("unused")
	private static short byte2short(byte[] data) {
		return (short) ((data[0] << 8) | (data[1]));
	}
	
	@SuppressWarnings("unused")
	private static short byte2shortAlternative(byte[] data) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(data[0]);
		bb.put(data[1]);
		return bb.getShort(0);
	}
	
	private static byte encode(short sample) {
		final short BIAS = 132;// 0x84
		final short CLIP = 32635;// 32767-BIAS

		// Convert sample to sign-magnitude
		int sign = sample & 0x8000;
		if (sign != 0) {
			sample = (short) -sample;
			sign = 0x80;
		}

		/*
		 * Because of the bias that is added, allowing a value larger than CLIP
		 * would result in integer overflow, so clip it.
		 */
		if (sample > CLIP)
			sample = CLIP;

		/*
		 * Convert from 16-bit linear PCM to ulaw Adding this bias guarantees a
		 * 1 bit in the exponent region of the data, which is the eight bits to
		 * the right of the sign bit.
		 */
		sample += BIAS;

		/*
		 * Exponent value is the position of the first 1 to the right of the
		 * sign bit in the exponent region of the data. Find the position of the
		 * first 1 to the right of the sign bit, counting from right to left in
		 * the exponent region. The exponent position (value) can range from 0
		 * to 7. Could use a table lookup but will compute on the fly instead
		 * because that is better for teaching the algorithm.
		 */
		int exp;
		
		// Shift sign bit off to the left
		short temp = (short) (sample << 1);
		for (exp = 7; exp > 0; exp--) {
			if ((temp & 0x8000) != 0)
				break;// found it
			temp = (short) (temp << 1);
		}

		/*
		 * The mantissa is the four bits to the right of the first 1 bit in the
		 * exponent region. Shift those four bits to the four lsb of the 16-bit
		 * value.
		 */
		temp = (short) (sample >> (exp + 3));
		// Mask and save those four bits
		int mantis = temp & 0x000f;
		
		/*
		 * Construct the complement of the ulaw byte. Set the sign bit in the
		 * msb of the 8-bit byte. The value of sign is either 0x00 or 0x80.
		 * Position the exponent in the three bits to the right of the sign bit.
		 * Set the 4-bit mantissa in the four lsb of the byte. Note that the
		 * one's complement of this value will be returned.
		 */
		byte ulawByte = (byte) (sign | (exp << 4) | mantis);
		
		// Now complement to create actual ulaw byte and return it.
		return (byte) ~ulawByte;
	}

}
