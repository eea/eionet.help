package eionet.help.util;

public class Util {

    /**
     * Converts a string to valid SQL string literal.
     */
    public static String strLiteral(String in) {
        in = (in != null ? in : "");
        StringBuffer ret = new StringBuffer("'");

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\'') {
                ret.append("''");
            } else {
                ret.append(c);
            }
        }
        ret.append('\'');

        return ret.toString();
    }

    /**
     * Returns true if argument string does not contain anything (either is null or empty string).
     */
    public static boolean nullString(String str) {
        return (str == null || str.length() == 0);
    }
}
