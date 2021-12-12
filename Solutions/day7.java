import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class CrabAligner {

    private int[] crabs;
    private int lowest;
    private int highest;

    /** Reads a file and creates an array of integers. */
    public void readPositions(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            String numbers = lines.get(0);
            this.crabs = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Gets the min and max value in the array (values between which to search). */
    public void getHighestAndLowest(){
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int i : crabs){
            highest = Math.max(i, highest);
            lowest = Math.min(i, lowest);
        }
        this.highest = highest;
        this.lowest = lowest;
    }

    /** Finds the minimal possible cost, based on a constant or increasing rate (boolean value). */
    public int minimiseCost(boolean constantRate){
       this.getHighestAndLowest();
       int best = Integer.MAX_VALUE;

       for (int i = this.lowest; i < this.highest; i++){
           int current = 0;
           if (constantRate) {
               for (int position : this.crabs) {
                   current += Math.abs(position - i);
               }
           }
           else {
               for (int position : this.crabs) {
                   int n = Math.abs(position - i);
                   current += (n * (n+1))/2;
               }
           }
           best = Math.min(best, current);
       }
       return best;
    }
    
    public static void main(String[] args) {
        CrabAligner crab = new CrabAligner();
        crab.readPositions("day7.txt");
        System.out.println("Answer to Part 1 : " + crab.minimiseCost(true));
        System.out.println("Answer to Part 2 : " +crab.minimiseCost(false));
    }
}
