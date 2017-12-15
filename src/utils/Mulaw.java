package utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Mulaw {
	
	public static byte[] compress(short[] input) throws IOException {
		long nbShorts = input.length;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);

		if (nbShorts == 0L)
			return null;

		int index = 0;

		while (index < nbShorts) {
			try {
				byte ulawByte = encode(input[index++]);
				dos.writeByte(ulawByte);
			} catch (IndexOutOfBoundsException ioobe) {
				// index too high
			}
		}
		return outputStream.toByteArray();
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
	
	public static byte[] decompress(byte[] muLawBytes) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(outputStream);
		long nbBytes = muLawBytes.length;

		if (nbBytes == 0L)
			return null;

		int index = 0;

		while (index < nbBytes) {
			byte nextByte = muLawBytes[index++];
			dos.writeShort(decode(nextByte));
		}

		return outputStream.toByteArray();
	}
	
	private static short decode(byte ulawByte) {
		/* Perform one's complement to undo the one's complement at the end of
		 * the encode algorithm. */
		ulawByte = (byte) (~ulawByte);
		// Get the sign bit from the ulawByte
		int sign = ulawByte & 0x80;
		// Get the value of the exponent in the three
		// bytes to the right of the sign bit.
		int exp = (ulawByte & 0x70) >> 4;
		// Get the mantissa by masking off and saving
		// the four lsb in the ulawByte.
		int mantis = ulawByte & 0xf;
		// Construct the 16-bit output value as type
		// int for simplicity and cast to short
		// before returning.
		int rawValue = (mantis << (12 - 8 + (exp - 1))) + (132 << exp) - 132;
		// Change the sign if necessary and return
		// the 16-bit estimate of the original
		// sample value.
		return (short) ((sign != 0) ? -rawValue : rawValue);
	}

}
