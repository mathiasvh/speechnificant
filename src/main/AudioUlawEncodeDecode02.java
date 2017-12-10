package main;

public class AudioUlawEncodeDecode02 {
	static int value = 0;
	static int increment = 1;
	static int limit = 4;
	static short shortValue = (short) value;

	public static void main(String args[]) {

		System.out.println("Process and display truncation");

		System.out.println();
		System.out.println("Process and display ULAW");
		// Reinitialize values in the processing loop.
		processAndDisplayUlaw();

	}

	// This encoding method is loosely based on
	// online material at:
	// http://www.speech.cs.cmu.edu/comp.speech/
	// Section2/Q2.7.html
	static byte encode(short sample) {
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

	// This decode method is loosely based on
	// material at:
	// http://web.umr.edu/~dcallier/school/
	// 311_final_report.doc
	// That material was published by David Callier
	// and Chess Combites as a semester project and
	// has been reformulated into Java code by this
	// author..
	static short decode(byte ulawByte) {
		// Perform one's complement to undo the one's
		// complement at the end of the encode
		// algorithm.
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
	
	/*
	 * 	The original sample value (which is the same as for the truncation experiment)
		The encoded ULAW byte value in hex notation
		The 16-bit value produced by decoding the ULAW byte
		The difference between the original sample value and the decoded value
		That difference (error) expressed as a percent of the original sample value
	 */

	static void processAndDisplayUlaw() {
		while ((shortValue >= 0) & (shortValue < 32000)) {
			byte ulawByte = encode(shortValue);
			short result = decode(ulawByte);
			System.out.print(shortValue + " "); // val
			System.out.print("0x" + Integer.toHexString(ulawByte & 0xff) + " "); //ULAW byte
			System.out.print(result + " "); // decoded val
			System.out.print(shortValue - result); // diff
			if (shortValue > 0) { // diff in percentage
				System.out.println(" " + ((float) (100.0 * (shortValue - result) / shortValue)) + "%");
			} else {
				System.out.println();
			} // end else
			value = value + increment;
			shortValue = (short) value;
			if (value > limit) {
				increment *= 2;
				limit *= 2;
			} // end if
			if (increment > 32000)
				break;
		} // end while loop
	}// end processAndDisplayTruncation
}
