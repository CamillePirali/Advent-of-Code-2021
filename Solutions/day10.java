import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

class BracketReader {

    private List<char[]> chunks = new ArrayList<>();
    private HashMap<Character, Integer> opening;
    private HashMap<Character, Integer> closing;

    /** Creates a new object by parsing a file and creating two HashMaps mapping bracket pairs
     * to a value. */
    BracketReader(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            this.chunks = lines.stream().map(String::toCharArray).collect(Collectors.toList());
            this.opening = new HashMap<>();
            this.opening.put('(',0); this.opening.put('[',1); this.opening.put('{',2);this.opening.put('<',3);
            this.closing = new HashMap<>();
            this.closing.put(')',0); this.closing.put(']',1); this.closing.put('}',2); this.closing.put('>',3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns the syntax error score of a line if it is corrupted. */
    public int corruptedLineScore(char[] line, HashMap<Character, Integer> scores){
        Stack<Integer> brackets = new Stack<>();
        for (char c : line){
            if (this.opening.containsKey(c)){
                brackets.push(this.opening.get(c));
            }
            else {
                if (!this.closing.get(c).equals(brackets.pop())){
                    return scores.get(c);
                }
            }
        }
        return 0;
    }

    /** Returns the syntax error score of a line if it is incomplete. */
    public long incompleteLineScore(char[] line, HashMap<Character, Integer> scores){
        Stack<Integer> brackets = new Stack<>();
        for (char c : line){
            if (this.opening.containsKey(c)){
                brackets.push(this.opening.get(c));
            }
            else {
                if (!this.closing.get(c).equals(brackets.pop())){
                    return 0;
                }
            }
        }
        long total = 0;
        while (!brackets.empty()){
            long toAdd = brackets.pop();
            char key = ' ';
            for (char c : this.closing.keySet()){
                if (this.closing.get(c) == toAdd){
                    key = c;
                }
            }
            total *= 5;
            total += scores.get(key);
        }

        return total;
    }

    /** Computes the error score of the file used to instantiate the object, based on corrupted
     * or incomplete lines (depending on the boolean value). */
    public long computeErrorScore(HashMap<Character, Integer> scores, boolean corrupted){
        if (corrupted) {
            long score = 0;
            for (char[] line : chunks) {
                score += corruptedLineScore(line, scores);
            }
            return score;
        }
        else {
            List<Long> score = new ArrayList<>();
            for (char[] line : chunks){
                long res = incompleteLineScore(line, scores);
                if (res != 0){
                    score.add(res);
                }
            }
            Collections.sort(score);
            return score.get(score.size() / 2);
        }
    }

    public static void main(String[] args) {
        // Creates scoring hashmaps based on the problem statement
        HashMap<Character, Integer> scoresCorrupted = new HashMap<>();
        scoresCorrupted.put(')', 3); scoresCorrupted.put(']', 57); scoresCorrupted.put('}', 1197);
        scoresCorrupted.put('>', 25137);

        HashMap<Character, Integer> scoresIncomplete = new HashMap<>();
        scoresIncomplete.put(')',1); scoresIncomplete.put(']',2); scoresIncomplete.put('}',3);
        scoresIncomplete.put('>',4);

        BracketReader reader = new BracketReader("day10.txt");
        System.out.println("Answer to Part 1 : " + reader.computeErrorScore(scoresCorrupted, true));
        System.out.println("Answer to Part 2 : " + reader.computeErrorScore(scoresIncomplete, false));
    }
}
