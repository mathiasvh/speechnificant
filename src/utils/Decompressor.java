package utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Decompressor {
	
	// prevent instantiation
	private Decompressor() {}

	public static byte[] decompress(File input) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(input));

		int size = (int) Math.ceil(input.length());
		byte[] muLawBytes = new byte[size];
		dis.readFully(muLawBytes);
		dis.close();

		return decompress(muLawBytes);
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
