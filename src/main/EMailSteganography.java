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
    private static final int X_SIZE = 7;
    private static final int Y_SIZE = 2;
    private static final int Z_SIZE = 6;

    private final Dictionary dictionary = new Dictionary();
    private final EmailContext emailContext;

    private final Map<Integer, Category> categoryByCode = new HashMap<>(values().length);
    private final Map<Character, Integer> codeByCategory = new HashMap<>(values().length);
    private final Map<Integer, EMailAddressPostfix> eMailAddressPostfixByValue = new HashMap<>(EMailAddressPostfix.values().length);
    private final Map<String, Integer> codeByEMailAddressPostfix = new HashMap<>(EMailAddressPostfix.values().length);
    private final Map<Integer, Characters> characterByCode = new HashMap<>(Characters.values().length);

    public EMailSteganography(EmailContext emailContext) {
        this.emailContext = emailContext;

        for (Category value : values()) {
            this.categoryByCode.put(value.getCode(), value);
        }

        for (Category value : values()) {
            this.codeByCategory.put(value.getSymbol(), value.getCode());
        }

        for (EMailAddressPostfix value : EMailAddressPostfix.values()) {
            this.eMailAddressPostfixByValue.put(value.getCode(), value);
        }

        for (EMailAddressPostfix value : EMailAddressPostfix.values()) {
            this.codeByEMailAddressPostfix.put(value.getValue(), value.getCode());
        }

        for (Characters value : Characters.values()) {
            this.characterByCode.put(value.getCode(), value);
        }
    }

    public Set<String> embedMessage(String secretMessage, String coverMessage) {
        String[] split = secretMessage.split("\\s+");

        String correspondingNumbers = this.convertToNumber(split);

        // Nc
        int charactersNumber = this.countCharacters(coverMessage);

        BitWindow bitWindow = new BitWindow(charactersNumber, correspondingNumbers);

        Set<String> emailAddresses = new HashSet<>();
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

            char symbol = 0;
            if (z > MAX_CHARACTERS) {
                int category = z / (MAX_CHARACTERS + 1);
                z = z % (MAX_CHARACTERS + 1);

                symbol = this.categoryByCode.get(category).getSymbol();
            }

            String xyzBitStream = BitUtils.fixSizeBits(x, X_SIZE) +
                    BitUtils.fixSizeBits(y, Y_SIZE) +
                    BitUtils.fixSizeBits(z, Z_SIZE);

            String AKey = xyzBitStream.substring(xyzBitStream.length() - 3);
            String remainingBits = xyzBitStream.substring(0, xyzBitStream.length() - 3);
            String emailAddressPostfix = eMailAddressPostfixByValue.get(BitUtils.convertBinaryToInteger(AKey)).getValue();

            System.out.println("xyzBitStream: " + xyzBitStream);

            List<Character> characterList = new ArrayList<>(4);
            int index = 0;
            for (String partitionBit : BitUtils.partitionBits(remainingBits, 4)) {
                int Ki = BitUtils.convertBinaryToInteger(partitionBit);
                char Ci = this.makeEnglishCharacter(index, Ki);
                characterList.add(Ci);
                index++;
            }

            int Nz = bitWindow.getZeros();
            char Cz = this.makeEnglishCharacter(0, Nz);

            StringBuilder emailAddress = new StringBuilder();
            for (Character character : characterList) {
                emailAddress.append(character);
            }

            if (symbol != 0) {
                emailAddress.append(symbol);
            }
            emailAddress.append(Cz);
            emailAddress.append(emailAddressPostfix);

            emailAddresses.add(emailAddress.toString());
        }

        System.out.println(emailAddresses);
        return emailAddresses;
    }

    public String extractMessage(Set<String> emailAddresses) {
        StringBuilder secretMessageBits = new StringBuilder();

        for (String emailAddress : emailAddresses) {
            StringBuilder xyzBitStream = new StringBuilder();

            String firstBits = BitUtils.convertIntegerToBinary(
                    this.makeKFromEnglishCharacter(emailAddress.charAt(0), 0)
            );
            String secondBits = BitUtils.convertIntegerToBinary(
                    this.makeKFromEnglishCharacter(emailAddress.charAt(1), 1)
            );
            String thirdBits = BitUtils.convertIntegerToBinary(this.makeKFromEnglishCharacter(emailAddress.charAt(2), 2));

            xyzBitStream.append(firstBits).append(secondBits).append(thirdBits);

            int indexOfPostfix = emailAddress.indexOf('@');
            String emailAddressPostfix = emailAddress.substring(indexOfPostfix);

            int postfixCode = this.codeByEMailAddressPostfix.get(emailAddressPostfix);
            System.out.println("postfixCode: " + postfixCode);

            xyzBitStream.append(BitUtils.fixSizeBits(postfixCode, 3));

            System.out.println("xyzBitStream: " + xyzBitStream);

            int x = BitUtils.convertBinaryToInteger(xyzBitStream.substring(0, X_SIZE));
            int y = BitUtils.convertBinaryToInteger(xyzBitStream.substring(X_SIZE, X_SIZE + Y_SIZE));
            int z = BitUtils.convertBinaryToInteger(xyzBitStream.substring(X_SIZE + Y_SIZE, X_SIZE + Y_SIZE + Z_SIZE));

            System.out.println("x: " + xyzBitStream.substring(0, X_SIZE));
            System.out.println("y: " + xyzBitStream.substring(X_SIZE, X_SIZE + Y_SIZE));
            System.out.println("z: " + xyzBitStream.substring(X_SIZE + Y_SIZE, X_SIZE + Y_SIZE + Z_SIZE));

            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("z: " + z);

            char c = emailAddress.charAt(3);
            if (Category.contains(c)) {
                z += this.codeByCategory.get(c);
            }

            int Nz = this.makeKFromEnglishCharacter(
                    emailAddress.substring(indexOfPostfix - 1, indexOfPostfix).charAt(0), 0
            );

            String fixSizeString = BitUtils.addZeroBefore(x + y + z, Nz);

            secretMessageBits.append(fixSizeString);
        }

        System.out.println(secretMessageBits);
        return secretMessageBits.toString();
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

    public int makeKFromEnglishCharacter(char c, int index) {
        return (c - (16 * index)) % 26;
    }
}
