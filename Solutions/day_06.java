import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Class allowing the use of threads to divide the problem into smaller parts. */
class Parallel implements Runnable{

    private final int fish;
    private final int days;
    private final RecursiveFish object;
    public long result;

    Parallel(int fish, int days, RecursiveFish object){
        this.object = object;
        this.fish = fish;
        this.days = days;
    }

    @Override
    public void run() {
        this.result = this.object.getResult(this.days, this.fish);
    }
}

class RecursiveFish {

    private int[] schoolOfFish;
    public HashMap<Integer, Integer> typesOfFish = new HashMap<>();

    /** Creates a new RecursiveFish object. */
    RecursiveFish(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            String[] fish = lines.get(0).split(",");
            this.readList(fish);
            this.updateMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Reads a file and stores each number in an array. */
    public void readList(String[] lines) {
        int[] numbers = new int[lines.length];
        for (int i = 0; i < numbers.length; i++){
            numbers[i] = Integer.parseInt(lines[i]);
        }
        this.schoolOfFish = numbers;
    }

    /** Keeps track of how many fish of each type there are. */
    public void updateMap() {
        for (int fish : this.schoolOfFish){
            this.typesOfFish.put(fish, this.typesOfFish.getOrDefault(fish, 0) + 1);
        }
    }

    /** Recursive function simulating the growth of a school of fish starting with a single type
     * of fish. Returns the number of existing fish after a given amount of days. */
    public long simulateGrowth(int fish, int days) {
        if (days == 0){
            return 0;
        }
        else if (fish == 0){
            return 1 + simulateGrowth(6, days - 1) + simulateGrowth(8, days - 1);
        }
        else {
            return simulateGrowth(fish - 1, days - 1);
        }
    }

    /** Method to get the number of fish born from a specific fish type after a given time period. */
    public long getResult(int days, int fish) {
        return this.typesOfFish.get(fish) * (1 + simulateGrowth(fish, days));
    }

    /** Creates as many threads as there are different fish types in the original input.
     * Each thread solves its own sub-problem for the given number of days. The sum of all results
     * is returned once all the threads have finished running. */
    public long solveProblem(int days){
        try {
            List<Parallel> parallels = new ArrayList<>();
            List<Thread> threads = new ArrayList<>();
            for (int n : this.typesOfFish.keySet()) {
                parallels.add(new Parallel(n, days, this));
                threads.add(new Thread(parallels.get(parallels.size() - 1)));
            }
            long result = 0;
            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }
            for (Parallel p : parallels){
                result += p.result;
            }
            return result;

        } catch ( InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        RecursiveFish fish = new RecursiveFish("day6.txt");
        System.out.println("Answer to Part 1 : " + fish.solveProblem(80));
        System.out.println("Answer to Part 2 : " + fish.solveProblem(256));
    }
}
