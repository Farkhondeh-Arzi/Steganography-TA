package main.stegano.window;

import main.stegano.utils.BitUtils;
import main.stegano.utils.StringUtils;

import static main.stegano.common.Constants.MAX_READING_TIME;

public class BitWindow {

    private final MessageBits messageBits;
    private final int max;
    //Nz
    private int zeros = 0;

    public BitWindow(int charactersNumber, String messageBits) {
        this.messageBits = new MessageBits(messageBits);
        this.max = MAX_READING_TIME * charactersNumber;
    }

    public int secretBlockSelection() {
        int decimal = 0;

        while (BitUtils.convertBinaryToInteger(StringUtils.reverse(BitUtils.convertIntegerToBinary(decimal))) < max && this.messageBits.hasNext()) {
            decimal = decimal << 1;
            // 10110001010100100001110010110000
            char next = messageBits.next();

            if (next == '1') {
                decimal = decimal | BitUtils.convertBinaryToInteger(next);
            }
        }

        //    00100101010001101
        // 10000100101010001101
        String bitString = StringUtils.reverse(Integer.toBinaryString(decimal));

        char character = 'n';
        while (character != '1') {
            bitString = bitString.substring(1);
            character = bitString.charAt(0);
            this.zeros++;
        }

        // this.zeros is one more than it's actual value because first bit was removed;
        // Therefore, to calculate the index of the current position on the bit string of the secret message, it goes back one unit
        this.zeros--;

        return BitUtils.convertBinaryToInteger(bitString);
    }

    public boolean isOpen() {
        return this.messageBits.hasNext();
    }

    public int getZeros() {
        return zeros;
    }
}
