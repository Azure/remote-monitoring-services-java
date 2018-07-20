package helpers;

public class Random {

    private java.util.Random rand = null;

    public Random() {
        rand = new java.util.Random();
    }

    public String NextString() {
        return NextString(32, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public String NextString(int length, String characters) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            builder.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return builder.toString();
    }
}
