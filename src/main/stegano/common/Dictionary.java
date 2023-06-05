package main.stegano.common;

import main.stegano.utils.BitUtils;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    private static int SIZE = 69888;
    private static final int fistBoxSize = 256;
    private static final int secondBoxSize = 4096;
    private static final Map<String, Integer> INDEX_BY_WORD = new HashMap<>();
    private static final Map<String, String> WORD_BY_INDEX = new HashMap<>();

    public String getCorrespondingNumber(String word) {
        String lengthBinary = this.getLengthBinary(word.length());

        String p;
        if (INDEX_BY_WORD.containsKey(word)) {
            int index = INDEX_BY_WORD.get(word);

            if (index < fistBoxSize) {
                p = "00" + Integer.toBinaryString(index - 1);
            } else if (index < fistBoxSize + secondBoxSize) {
                p = "01" + Integer.toBinaryString(index - fistBoxSize - 1);
            } else {
                p = "10" + Integer.toBinaryString(index - fistBoxSize - secondBoxSize - 1);
            }
        } else {
            int ascciCode = 0;

            for (int i = 0; i < word.length(); i++) {
                ascciCode += word.charAt(i);
            }

            p = "11" + Integer.toBinaryString(ascciCode);
        }

        return lengthBinary + p;
    }

    public String getCorrespondingWord(String bitStream) {

        return WORD_BY_INDEX.get(bitStream);
    }

    private String getLengthBinary(int length) {
        int division = length / 16;
        int zerosNumber = (division + 1) * 4;

        length -= Math.pow(16, division) - 1;

        return BitUtils.fixSizeBits(length, zerosNumber);
    }
}
