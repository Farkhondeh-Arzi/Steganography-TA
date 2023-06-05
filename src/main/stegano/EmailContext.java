package main.stegano;

import java.util.LinkedHashSet;
import java.util.Set;

public class EmailContext {
    Set<String> sentences = new LinkedHashSet<>();

    public EmailContext(String coverMessage) {
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < coverMessage.length(); i++) {
            char c = coverMessage.charAt(i);

            sentence.append(c);

            if (c == '.') {
                this.sentences.add(sentence.toString());
                sentence = new StringBuilder();
            }
        }
    }

    public Set<String> getSentences() {
        return sentences;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder("sentences: ");
        for (String sentence : this.sentences) {
            toString.append("\n").append(sentence);
        }

        return toString.toString();
    }
}
