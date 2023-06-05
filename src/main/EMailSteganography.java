package main;

import main.stegano.EmailContext;
import main.stegano.common.Category;
import main.stegano.common.Characters;
import main.stegano.common.Dictionary;
import main.stegano.common.EMailAddressPostfix;
import main.stegano.utils.BitUtils;
import main.stegano.window.BitWindow;

import java.util.*;

import static main.stegano.common.Category.*;
import static main.stegano.common.Constants.MAX_CHARACTERS;

public class EMailSteganography {
    private final Dictionary dictionary = new Dictionary();
    private final EmailContext emailContext;

    private final Map<Integer, Category> categoryByCode = new HashMap<>(values().length);
    private final Map<Integer, EMailAddressPostfix> eMailAddressPostfixByValue = new HashMap<>(EMailAddressPostfix.values().length);
    private final Map<Integer, Characters> characterByCode = new HashMap<>(Characters.values().length);

    public EMailSteganography(EmailContext emailContext) {
        this.emailContext = emailContext;

        for (Category value : values()) {
            this.categoryByCode.put(value.getCode(), value);
        }

        for (EMailAddressPostfix value : EMailAddressPostfix.values()) {
            this.eMailAddressPostfixByValue.put(value.getCode(), value);
        }

        for (Characters value : Characters.values()) {
            this.characterByCode.put(value.getCode(), value);
        }
    }

    public void embedMessage(String secretMessage, String coverMessage) {
        String[] split = secretMessage.split("\\s+");

        String correspondingNumbers = this.convertToNumber(split);

        // Nc
        int charactersNumber = this.countCharacters(coverMessage);

        BitWindow bitWindow = new BitWindow(charactersNumber, "10110001010100100001110010110000");

        while (bitWindow.isOpen()) {
            int d = bitWindow.secretBlockSelection();

            int x = d / charactersNumber;
            int m = d % charactersNumber;

            Set<String> sentences = emailContext.getSentences();

            int z = 0;
            int y = 0;
            for (String sentence : sentences) {
                int sentenceCharacters = this.countCharacters(sentence);
                z += sentenceCharacters;
                y++;

                if (z > m) {
                    z -= sentenceCharacters;
                    z = m - z;
                    y--;
                    break;
                }
            }

            char symbol;
            if (z > MAX_CHARACTERS) {
                int category = z / (MAX_CHARACTERS + 1);
                z = z % (MAX_CHARACTERS + 1);

                symbol = this.categoryByCode.get(category).getSymbol();
            }

            System.out.println("d: " + d);
            System.out.println("x: " + x);
            System.out.println("m: " + m);
            System.out.println("y: " + y);
            System.out.println("z: " + z);

            String xyzBitStream = BitUtils.fixSizeBits(x, 7) +
                    BitUtils.fixSizeBits(y, 2) +
                    BitUtils.fixSizeBits(z, 6);

            String AKey = xyzBitStream.substring(xyzBitStream.length() - 3);
            String remainingBis = xyzBitStream.substring(0, xyzBitStream.length() - 3);
            String EmailAddressPostfix = eMailAddressPostfixByValue.get(BitUtils.convertBinaryToInteger(AKey)).getValue();

            System.out.println("xyzBitStream: " + xyzBitStream);
            System.out.println("AKey: " + AKey);
            System.out.println("EmailAddressPostfix: " + EmailAddressPostfix);

            List<Character> characterList = new ArrayList<>(4);
            int index = 0;
            for (String partitionBit : BitUtils.partitionBits(remainingBis, 4)) {
                int Ki = BitUtils.convertBinaryToInteger(partitionBit);
                char Ci = this.makeEnglishCharacter(index, Ki);
                characterList.add(Ci);
                index++;
            }

            int Nz = bitWindow.getZeros();
            char Cz = this.makeEnglishCharacter(0, Nz);

            System.out.println("characterList " + characterList);
            System.out.println("Cz: " + Cz);
        }
    }

    public void extractMessage() {

    }

    private String convertToNumber(String[] words) {
        StringBuilder correspondingNumbers = new StringBuilder();

        for (String word : words) {
            correspondingNumbers.append(this.dictionary.getCorrespondingNumber(word));
        }

        return correspondingNumbers.toString();
    }

    private int countCharacters(String message) {
        return message.length();
    }

    private char makeEnglishCharacter(int index, int Ki) {
        return this.characterByCode.get((index * 16 + Ki) % 26).toString().charAt(0);
    }
}
