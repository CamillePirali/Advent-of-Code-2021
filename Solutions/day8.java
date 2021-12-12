import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/** Class to decode seven-segment display values. */
class DisplayReader {

    private String[][] inputs;
    private String[][] outputs;
    private final HashMap<Integer, HashMap<Character, Integer>> keys;

    /** Creates a new DisplayReader object with a hashmap of reference signatures for each of the
     * ten possible numbers (based on the strings given as input). For example, if 1 is represented
     * by "cf", the entry "1" in the HashMap will be {c=1, f=1}. */
    DisplayReader(HashMap<Integer, String> key) {
        this.keys = new HashMap<>();
        HashMap<Character, Character> defaultMap = new HashMap<>();
        defaultMap.put('a', 'a'); defaultMap.put('b', 'b'); defaultMap.put('c', 'c'); defaultMap.put('d', 'd');
        defaultMap.put('e','e'); defaultMap.put('f','f'); defaultMap.put('g','g');

        for (int i : key.keySet()){
            this.keys.put(i, toHashMap(key.get(i), defaultMap));
        }
    }

    /** Reads lines of a file of the format input | output, and adds each line segment to
     * the relevant array. */
    public void readInput(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            this.inputs = new String[lines.size()][10];
            this.outputs = new String[lines.size()][4];
            for (int i= 0; i < lines.size(); i++){
                String[] parts = lines.get(i).split("\\|");
                this.inputs[i] = parts[0].trim().split("\s");
                this.outputs[i] = parts[1].trim().split("\s");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Method to answer part 1 : counts the number of times digits 1, 4, 7 and 8 appear in
     * the output values (based on their unique length). */
    public int uniqueCount() {
        int count = 0;
        for (String[] output : this.outputs){
            for (String number : output ) {
                int n = number.length();
                if (n == 2 || n == 3 || n == 4 || n == 7) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Returns a HashMap mapping each character to the number of times it appears in an input line. */
    public HashMap<Character, Integer> getOccurrences(String[] line){
        HashMap<Character, Integer> result = new HashMap<>();
        for (String number : line){
            for (int i = 0; i < number.length(); i++){
                result.put(number.charAt(i), result.getOrDefault(number.charAt(i), 0) + 1);
            }
        }
        return result;
    }

    /** Decodes an input by mapping each character to the one it represents (using a set of rules
     * derived from the problem statement. */
    public HashMap<Character, Character> mapCharacters(String[] numbers){
        HashMap<Character, Integer> occ = this.getOccurrences(numbers);
        HashMap<Character, Character> map = new HashMap<>();

        // Gets relevant numbers (of length 2,3 and 4)
        char[] lenOf2 = new char[2];
        char[] lenOf3 = new char[3];
        char[] lenOf4 = new char[4];
        for (String s : numbers){
            switch (s.length()){
                case 2 -> s.getChars(0, 2, lenOf2, 0);
                case 3 -> s.getChars(0, 3, lenOf3, 0);
                case 4 -> s.getChars(0, 4, lenOf4, 0);
            }
        }

        // Attributes a (only number in the representation of 7 ("acf") which isn't in the
        // representation of 2 ("cf")
        int[] options = new int[]{0,1,2};
        for (int i = 0; i < lenOf3.length; i++){
            for (char c2 : lenOf2){
                if (lenOf3[i] == c2) {
                    options[i] = -1;
                    break;
                }
            }
        }
        for (int i : options){
            if (i != -1){
                map.put(lenOf3[i], 'a');
                occ.remove(lenOf3[i]);
                break;
            }
        }

        // Attributes the unequivocal matches (only characters occurring n times in the numbers)
        List<Character> allChanged = new ArrayList<>();
        for (Character key : occ.keySet()){
            switch (occ.get(key)) {
                case 6 -> {
                    map.put(key, 'b');
                    allChanged.add(key);
                }
                case 4 -> {
                    map.put(key, 'e');
                    allChanged.add(key);
                }
                case 8 -> {
                    map.put(key, 'c');
                    allChanged.add(key);
                }
                case 9 -> {
                    map.put(key, 'f');
                    allChanged.add(key);
                }
            }
        }
        for (char c : allChanged){
            occ.remove(c);
        }

        // Attributes d and g (both occurring 7 times, but only d occurs in the representation of 4).
        for (char c : lenOf4){
            if (map.get(c) == null){
                map.put(c, 'd');
                occ.remove(c);
            }
        }
        for (char last : occ.keySet()){
            map.put(last, 'g');
        }
        return map;
    }

    /** Creates a HashMap mapping a number on a seven-segment display to the segments it uses.
     * For example, 0={a=1, b=1, c=1, e=1, f=1, g=1}*/
    public HashMap<Character, Integer> toHashMap(String number, HashMap<Character, Character> map){
        HashMap<Character, Integer> result = new HashMap<>();
        for (char c : number.toCharArray()){
            result.put(map.get(c), 1);
        }
        return result;
    }

    /** Finds the digit that corresponds to the HashMap given as argument (by comparing it to the keys
     * defined when creating the class object). */
    public String getDigit(HashMap<Character, Integer> number){
        for (Integer key : this.keys.keySet()){
            if (this.keys.get(key).keySet().equals(number.keySet())){
                return Integer.toString(key);
            }
        }
        return "";
    }

    /** Method solving part 2 : returns the sum of all decoded output values. */
    public int totalCount(){
        int total = 0;
        for (int i = 0; i < this.outputs.length; i++){
            StringBuilder result = new StringBuilder();
            HashMap<Character, Character> map = this.mapCharacters(this.inputs[i]);
            // Appends the 4 digits of the output to a string builder, then parses it into an integer.
            for (String output : this.outputs[i]){
                result.append(getDigit(toHashMap(output, map)));
            }
            total += Integer.parseInt(String.valueOf(result));
        }
        return total;
    }

    public static void main(String[] args) {
        // Rules given by the problem statement.
        HashMap<Integer, String> key = new HashMap<>();
        key.put(0, "abcefg"); key.put(1, "cf"); key.put(2, "acdeg"); key.put(3, "acdfg");
        key.put(4, "bcdf"); key.put(5, "abdfg"); key.put(6, "abdefg"); key.put(7, "acf");
        key.put(8, "abcdefg"); key.put(9, "abcdfg");

        DisplayReader reader = new DisplayReader( key);
        reader.readInput("day8.txt");
        System.out.println("Answer to part 1 : " + reader.uniqueCount());
        System.out.println("Answer to part 2 : " + reader.totalCount());
    }
}
