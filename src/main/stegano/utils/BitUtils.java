package main.stegano.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BitUtils {
    public static String fixSizeBits(int number, int fixedNumber) {
        StringBuilder fixedSizeZeros = new StringBuilder();

        fixedSizeZeros.append("0".repeat(Math.max(0, fixedNumber)));

        String bitStream = Integer.toBinaryString(number);

        return fixedSizeZeros.substring(0, fixedSizeZeros.length() - bitStream.length()) + bitStream;
    }

    public static String addZeroBefore(int number, int zeros) {
        StringBuilder fixedSizeString = new StringBuilder();

        String bitStream = Integer.toBinaryString(number);

        fixedSizeString.append("0".repeat(Math.max(0, zeros)));
        fixedSizeString.append(bitStream);

        return fixedSizeString.toString();
    }

    public static String convertIntegerToBinary(int integer) {
        return Integer.toBinaryString(integer);
    }

    public static int convertBinaryToInteger(String binary) {
        return Integer.parseInt(binary, 2);
    }

    public static int convertBinaryToInteger(char binary) {
        return Integer.parseInt(String.valueOf(binary), 2);
    }

    public static List<String> partitionBits(String bitStream, int groupNumber) {
        List<String> parts = new ArrayList<>();

        int length = bitStream.length();
        int partsNumber = length / groupNumber;

        for (int i = 0; i < partsNumber; i++) {
            parts.add(bitStream.substring(i * groupNumber, (i + 1) * groupNumber));
        }

        Collections.reverse(parts);
        return parts;
    }
}
