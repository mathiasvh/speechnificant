package compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Compressor {
	
	// prevent instantiation
	private Compressor() {}
	
	public static byte[] compress(File input) throws IOException {
		FileInputStream in = new FileInputStream(input);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		long nbBytes = input.length();
		long nbShorts = nbBytes / 2;
		
		if (nbBytes == 0L) {
			in.close();
			return null;
		}
			
		
		int index = 0;
		
		while (index < nbShorts) {
			byte[] next2bytes = new byte[2];
			in.read(next2bytes, 0, 2);
			short shortValue = byte2short(next2bytes);
			byte ulawByte = encode(shortValue);
			outputStream.write(ulawByte);
			index += 2;
			//System.out.print("0x" + Integer.toHexString(ulawByte & 0xff) + " ");

		}
		
		in.close();
		
		byte[] result = outputStream.toByteArray();
		return result;
	}
	
	private static short byte2short(byte[] data) {
	    return (short)((data[0]<<8) | (data[1]));
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
		} // end if

		// Because of the bias that is added, allowing
		// a value larger than CLIP would result in
		// integer overflow, so clip it.
		if (sample > CLIP)
			sample = CLIP;

		// Convert from 16-bit linear PCM to ulaw
		// Adding this bias guarantees a 1 bit in the
		// exponent region of the data, which is the
		// eight bits to the right of the sign bit.
		sample += BIAS;

		// Exponent value is the position of the first
		// 1 to the right of the sign bit in the
		// exponent region of the data.
		// Find the position of the first 1 to the
		// right of the sign bit, counting from right
		// to left in the exponent region. The
		// exponent position (value) can range from 0
		// to 7.
		// Could use a table lookup but will compute
		// on the fly instead because that is better
		// for teaching the algorithm.
		int exp;
		// Shift sign bit off to the left
		short temp = (short) (sample << 1);
		for (exp = 7; exp > 0; exp--) {
			if ((temp & 0x8000) != 0)
				break;// found it
			temp = (short) (temp << 1);// shift and loop
		} // end for loop

		// The mantissa is the four bits to the right
		// of the first 1 bit in the exponent region.
		// Shift those four bits to the four lsb of
		// the 16-bit value.
		temp = (short) (sample >> (exp + 3));
		// Mask and save those four bits
		int mantis = temp & 0x000f;
		// Construct the complement of the ulaw byte.
		// Set the sign bit in the msb of the 8-bit
		// byte. The value of sign is either 0x00 or
		// 0x80.
		// Position the exponent in the three bits to
		// the right of the sign bit.
		// Set the 4-bit mantissa in the four lsb of
		// the byte.
		// Note that the one's complement of this
		// value will be returned.
		byte ulawByte = (byte) (sign | (exp << 4) | mantis);
		// Now complement to create actual ulaw byte
		// and return it.
		return (byte) ~ulawByte;
	}

}
