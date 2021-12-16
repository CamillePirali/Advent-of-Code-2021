import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

class Polymerisation {

    private final HashMap<String, Long> polymerCounts = new HashMap<>();
    private final HashMap<String, String> rules = new HashMap<>();
    private char firstSymbol;
    private char lastSymbol;

    /** Parses a file representing a polymer template and stores the counts of each pair it contains,
     * as well as the edge elements (the other ones that won't appear twice in the map). */
    Polymerisation(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            // Parses the first line :
            String input = lines.get(0);
            this.firstSymbol = input.charAt(0);
            this.lastSymbol = input.charAt(input.length() - 1);
            for (int i = 0; i < input.length() - 1; i++){
                this.polymerCounts.put(input.substring(i, i+2),
                        this.polymerCounts.getOrDefault(input.substring(i, i+2), (long) 0) + 1);
            }

            for (int j = 2; j < lines.size(); j++){
                String[] line = lines.get(j).split("->");
                this.rules.put(line[0].trim(), line[1].trim());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** For each pair type in the map: applies the relevant transformation rule and stores the newly obtained
     * pairs as many times as the original pair appeared in the map. */
    public void recreateStep(){
        HashMap<String, Long> toAdd = new HashMap<>();
        HashMap<String, Long> toRemove = new HashMap<>();
        for (String key : this.polymerCounts.keySet()){
            long n = this.polymerCounts.get(key);
            String firstHalf = key.charAt(0) + this.rules.get(key);
            String secondHalf = this.rules.get(key) + key.charAt(1);
            toAdd.put(firstHalf, toAdd.getOrDefault(firstHalf, (long) 0) + n);
            toAdd.put(secondHalf, toAdd.getOrDefault(secondHalf, (long) 0) + n);
            toRemove.put(key, toRemove.getOrDefault(key, (long) 0) + n);
        }
        // Adds all new pairs
        for (String key : toAdd.keySet()){
            this.polymerCounts.put(key, this.polymerCounts.getOrDefault(key, (long) 0) + toAdd.get(key));
        }
        // Removes pairs between which elements were inserted
        for (String key : toRemove.keySet()){
            this.polymerCounts.put(key, this.polymerCounts.get(key) - toRemove.get(key));

        }
    }

    /** Applies the above method x times. */
    public void applySteps(int x){
        for (int i = 0; i < x; i++){
            this.recreateStep();
        }
    }

    /** Returns counts for each character in the polymer map. */
    public HashMap<Character, Long> getCharCounts() {
        HashMap<Character, Long> res = new HashMap<>();
        for (String key : this.polymerCounts.keySet()) {
            char first = key.charAt(0);
            char second = key.charAt(1);
            res.put(first, res.getOrDefault(first, (long) 0) + this.polymerCounts.get(key));
            res.put(second, res.getOrDefault(second, (long) 0) + this.polymerCounts.get(key));
        }
        res.put(this.firstSymbol, res.get(this.firstSymbol) + 1);
        res.put(this.lastSymbol, res.get(this.lastSymbol) + 1);

        res.replaceAll((k, v) -> res.get(k) / 2);
        return res;
    }

    /** Returns the difference between the most frequent character and the least frequent character
     * in the polymer after x steps. */
    public long getResult(int x){
        this.applySteps(x);
        HashMap<Character, Long> counts = this.getCharCounts();
        long max = 0;
        long min = Long.MAX_VALUE;
        for (Character key: counts.keySet()){
            max = Math.max(max, counts.get(key));
            min = Math.min(min, counts.get(key));
        }
        return max - min;
    }

    public static void main(String[] args) {
        Polymerisation polymer = new Polymerisation("day14.txt");
        System.out.println(polymer.getResult(10));

        Polymerisation polymer2 = new Polymerisation("day14.txt");
        System.out.println(polymer2.getResult(40));

    }
}
