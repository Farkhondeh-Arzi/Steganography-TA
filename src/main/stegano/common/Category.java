package main.stegano.common;

public enum Category {
    DOT(1, '.'),
    UNDERLINE(2, '_'),
    DASH(3, '-');

    private final int code;
    private final char symbol;

    Category(int code, char symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public int getCode() {
        return code;
    }

    public char getSymbol() {
        return symbol;
    }
}
