import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SeaScanner {

    private List<Integer> data = new ArrayList<>();

    SeaScanner(String path) {
        try {
            // Reads the file and adds its parsed contents to a list
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines){
                data.add(Integer.parseInt(line));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int depthIncrease () {
        int totalIncrease = 0;
        int previous = (int) Double.POSITIVE_INFINITY;

        // Compares each line to the previous value
        for (int depth: data) {
            if (depth > previous){
                totalIncrease ++;
            }
            previous = depth;
        }
        return totalIncrease;
    }

    public int slidingWindow () {
        int totalIncrease = 0;

        // Takes two windows and compares the one element that differs in both
        for (int index = 0; index < data.size() - 3; index++){
            int window1 = data.get(index);
            int window2 = data.get(index + 3);
            if (window2 > window1){
                totalIncrease++;
            }
        }
        return totalIncrease;
    }

    public static void main(String[] args) {
        SeaScanner scanner = new SeaScanner("day1.txt");
        int part1 = scanner.depthIncrease();
        System.out.println("Answer to part 1 : " + part1);
        int part2 = scanner.slidingWindow();
        System.out.println("Answer to part 2 : " +part2);
    }
}
