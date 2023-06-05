package main.stegano.common;

public enum EMailAddressPostfix {
    GMAIL(0b000, "@gmail.com"),
    HOTMAIL(0b001, "@hotmail.com"),
    YAHOO(0b010, "@yahoo.com"),
    REDIFFMAIL(0b011, "@rediffmail.com"),
    BTINTERNER(0b100, "@btinternet.com"),
    AOL(0b101, "@aol.com"),
    MSN(0b110, "@msn.com"),
    VERIZON(0b111, "@verizon.com");
    private final int code;
    private final String value;

    EMailAddressPostfix(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
