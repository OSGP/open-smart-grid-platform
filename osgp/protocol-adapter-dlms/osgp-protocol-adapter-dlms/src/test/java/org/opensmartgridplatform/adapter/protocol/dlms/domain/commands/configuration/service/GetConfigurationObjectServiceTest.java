package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static java.lang.Integer.reverse;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.Test;

public class GetConfigurationObjectServiceTest {
    @Test
    public void test2() {
        final BitSet bitSet = new BitSet();
        bitSet.set(3);
        System.out.println("bitSet " + Long.toString(bitSet.toLongArray()[0], 2));
    }

    @Test
    public void test3() {
        final byte[] flagBytes = { (byte) 0b11100101, (byte) 0b10011111 };
        final String word = this.toBinary(flagBytes[0]) + this.toBinary(flagBytes[1]);
        System.out.println(word);
    }

    private String toBinary(final byte flagByte) {
        return Integer.toBinaryString((flagByte & 0xFF) + 256).substring(1);
    }

    @Test
    public void test4() {
        final String spec = "1110010110011111";

        final byte b1 = (byte) Integer.parseInt(spec.substring(0, 8), 2);
        System.out.println("b1 " + Integer.toBinaryString(b1));

        final byte b2 = (byte) Integer.parseInt(spec.substring(8, 16), 2);
        System.out.println("b2 " + Integer.toBinaryString(b2));

        System.out.println("b1,b2 " + b1 + "," + b2);
    }

    @Test
    public void test5() {

        final String word = "1110010110011111";

        final BitSet bitSet = new BitSet(16);

        for (int index = 0; index < word.length(); index++) {
            if (word.charAt(index) == '1') {
                bitSet.set(15 - index);
            }
        }

        final byte[] flagBytes = bitSet.toByteArray();

        System.out.println("flagBytes " + Arrays.toString(flagBytes));
    }

    @Test
    public void test6() {

        final StringBuilder word = new StringBuilder();

        word.setCharAt(0, '1');
        word.setCharAt(1, '1');
        word.setCharAt(2, '1');
        word.setCharAt(5, '1');
        word.setCharAt(7, '1');
        word.setCharAt(8, '1');
        word.setCharAt(11, '1');
        word.setCharAt(12, '1');
        word.setCharAt(13, '1');
        word.setCharAt(14, '1');
        word.setCharAt(15, '1');

        final String spec = word.toString();

        final byte b1 = (byte) Integer.parseInt(spec.substring(0, 8), 2);
        System.out.println("b1 " + Integer.toBinaryString(b1));

        final byte b2 = (byte) Integer.parseInt(spec.substring(8, 16), 2);
        System.out.println("b2 " + Integer.toBinaryString(b2));

        System.out.println("b1,b2 " + b1 + "," + b2);
    }

    @Test
    public void test() {

        final byte b1 = (byte) 0b11100101;
        System.out.println("b1 " + Integer.toBinaryString(b1));
        System.out.println("b1 reversed " + Integer.toBinaryString(Integer.reverse(b1 << 24) & 0xff));

        final byte b2 = (byte) 0b10011111;
        System.out.println("b2 " + Integer.toBinaryString(b2));

        final byte[] flagBytes = { b1, b2 };
        System.out.println("flagBytes " + Arrays.toString(flagBytes));

        // current code
        // The operation 'byte & 0xFF' converts a byte to an unsigned int (java bytes are signed values in the range
        // [-128, 127])
        // The two ints form a single word (long) using '<< 8' (binary left shift of 8 positions) and addition
        final long word = ((flagBytes[0] & 0xFF) << 8) + (flagBytes[1] & 0xFF);
        System.out.println("word " + Long.toBinaryString(word));

        final BitSet bitSet1 = BitSet.valueOf(new long[] { word });
        System.out.println("bitSet1 " + Long.toString(bitSet1.toLongArray()[0], 2));
        System.out.println("bitSet1 " + bitSet1);

        final byte[] byteArray1 = bitSet1.toByteArray();
        System.out.println("byteArray1 (before swap) " + Arrays.toString(byteArray1));

        this.swap(byteArray1);
        System.out.println("byteArray1 " + Arrays.toString(byteArray1));

        // naive approach
        final byte[] flagBytes2 = new byte[] { (byte) reverse(b2), (byte) reverse(b1) };
        System.out.println("flagBytes2 " + Arrays.toString(flagBytes2));

        final BitSet bitSet2 = BitSet.valueOf(flagBytes2);
        System.out.println("bitSet2 " + Long.toString(bitSet2.toLongArray()[0], 2));
        System.out.println("bitSet2 " + bitSet2);

        final byte[] byteArray2 = bitSet2.toByteArray();
        System.out.println("byteArray2 " + Arrays.toString(byteArray2));
    }

    private void swap(final byte[] bytes) {
        // swap bytes to set MSB first
        final byte tmp = bytes[1];
        bytes[1] = bytes[0];
        bytes[0] = tmp;
    }
}