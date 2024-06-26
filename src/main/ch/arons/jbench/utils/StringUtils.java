package ch.arons.jbench.utils;

/**
 * String utility.
 */
public class StringUtils {

    /**
     * Check if a string is null or empty.
     * 
     * @param s string to check.
     * @return true if empty.
     */
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        
        if (s.length() == 0) {
            return true;
        }
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ( !Character.isWhitespace(c) ) {
                return false;
            }
        }
        return true;
    }
}
