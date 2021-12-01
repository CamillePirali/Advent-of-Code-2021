import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SeaScanner {

    private List<String> data;

    SeaScanner(String path) {
        try {
            data = Files.readAllLines(Paths.get(path));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int depthIncrease () {
        int totalIncrease = 0;
        int previous = (int) Double.POSITIVE_INFINITY;

        // Compares each line to the previous value
        for (String line: data) {
            int depth = Integer.parseInt(line);
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
            int window1 = Integer.parseInt(data.get(index));
            int window2 = Integer.parseInt(data.get(index + 3));
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
        System.out.println("Answer to part 2 : " + part2);
    }
}
