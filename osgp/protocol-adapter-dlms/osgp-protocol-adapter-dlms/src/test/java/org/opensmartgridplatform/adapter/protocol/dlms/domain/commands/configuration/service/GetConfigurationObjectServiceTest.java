package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.Test;

public class GetConfigurationObjectServiceTest {
    @Test
    public void test() {

        // 1) convert first byte to int: b1 & 0xFF -> 0 0 0 b1
        // 2) shift the first int 8 bits to the left: -> 0 0 b1 0
        // 3) convert second byte to int: b2 & 0xFF -> 0 0 0 b2
        // add the 2 ints into a long: 0 0 b1 0 + 0 0 0 b2 = 0 0 0 0 0 0 b1 b2
        // create a BitSet from the long
        final byte b1 = 85;
        System.out.println(Integer.toBinaryString(b1));
        final byte b2 = (byte) 204;
        System.out.println(Integer.toBinaryString(b2));
        final byte[] bytes = { b1, b2 };
        final int i1 = bytes[0] & 0xFF;
        System.out.println(Integer.toBinaryString(i1));
        final int i2 = bytes[1] & 0xFF;
        System.out.println(Integer.toBinaryString(i2));
        final int i1b = i1 << 8;
        System.out.println(Integer.toBinaryString(i1b));
        final long l = i1b + i2;
        System.out.println(Long.toBinaryString(l));
        final long[] longs = { l };
        final BitSet bitSet = BitSet.valueOf(longs);

        final BitSet bitSet2 = BitSet.valueOf(bytes);

        this.printBits(bitSet);
        this.printBits(bitSet2);

        final byte[] bytes1 = bitSet.toByteArray();
        final byte[] bytes2 = bitSet2.toByteArray();

        System.out.println(Arrays.toString(bytes1));
        System.out.println(Arrays.toString(bytes2));
    }

    private void printBits(final BitSet b) {
        for (int i = 0; i < b.size(); i++) {
            System.out.print(b.get(i) ? "1" : "0");
        }
        System.out.println();
    }
}