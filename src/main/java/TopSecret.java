import java.util.HashMap;
import java.util.Map;
/**
 * Commmand Line Utility
 */

public class TopSecret {

    public static void main(String[] args) {
        System.out.println(decipher("56","abcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890\nbcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890a"));
    }

    private static String decipher(String text, String key){
        String[] lines = key.split("\\n", -1);
        //validate cipher key
        if (lines.length < 2){
            throw new IllegalArgumentException("Key file must have 2 lines");
        }
        String firstLine = lines[0];
        String secondLine = lines[1];
        if (firstLine.length() != secondLine.length()){
            throw new IllegalArgumentException("Key file must have equal length");
        }
        //building map
        Map<Character, Character> decodeMap = new HashMap<>();
        for (int i = 0; i < secondLine.length(); i++) {
            decodeMap.put(secondLine.charAt(i), firstLine.charAt(i));
        }
        //decoding
        StringBuilder decoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            decoded.append(decodeMap.getOrDefault(c, c));
        }
        //return decoded text
        return decoded.toString();
    }
}
