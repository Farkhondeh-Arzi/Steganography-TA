package main.stegano.window;

public class MessageBits {
    private final String bits;
    private int index = 0;

    public MessageBits(String bits) {
        this.bits = bits;
    }

    public char next() {
        return bits.charAt(index++);
    }

    public boolean hasNext() {
        return index < bits.length();
    }
}
