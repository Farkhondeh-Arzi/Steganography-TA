package main.stegano.utils;

public class StringUtils {

    public static String reverse(String s) {
        StringBuilder stringBuilder = new StringBuilder(s);

        stringBuilder.reverse();

        return stringBuilder.toString();
    }
}
