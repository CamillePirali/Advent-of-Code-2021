import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.max;


class Coordinates {

    public int x1, y1, x2, y2;

    /** Reads a line of the format x1,y1 -> x2,y2 and parses it into instance variables. */
    public Coordinates (String line){
        String[] points = line.split("->");
        String[] start = points[0].trim().split(",");
        String[] end = points[1].trim().split(",");
        this.x1 = Integer.parseInt(start[0]);
        this.y1 = Integer.parseInt(start[1]);
        this.x2 = Integer.parseInt(end[0]);
        this.y2 = Integer.parseInt(end[1]);
    }

    /** Returns a list of tuples containing coordinates of each point crossed by the line
     * starting at x1,y1 and ending at x2,y2. If getDiagonal is set to true, also includes
     * diagonal lines. Otherwise, only includes horizontal or vertical lines. */
    public List<List<Integer>> getStraightLine(boolean getDiagonal){
        List<List<Integer>> res = new ArrayList<>();
        if (this.x1 == this.x2){
            for (int i = 0; i < abs(y1 - y2) + 1; i++){
                int y = min(y1, y2) + i;
                res.add(new ArrayList<>(Arrays.asList(x1, y)));
            }
        }
        else if (this.y1 == this.y2){
            for (int i = 0; i < abs(x1 - x2) + 1; i++){
                int x = min(x1, x2) + i;
                res.add(new ArrayList<>(Arrays.asList(x, y1)));
            }
        }
        else {
            if (getDiagonal){
                int difference = abs(x1 - x2);
                for (int i = 0; i <= difference; i++){
                    int x = x1 < x2 ? x1 + i : x1 - i;
                    int y = y1 < y2 ? y1 + i : y1 - i;
                    res.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
        }
        return res;
    }

    /** Returns the highest values between each pair of coordinates. */
    public int[] getMaxXAndY(){
        return new int[]{max(x1, x2), max(y1, y2)};
    }

}

class CoordinatesReader {

    private final List<Coordinates> path = new ArrayList<>();
    private int x = 0;
    private int y = 0;
    private int[][] map;

    /** Reads a file and creates a coordinates object for each line. */
    public void readFile (String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));

            for (String line : lines){
                path.add(new Coordinates (line));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Creates a matrix representing the grid on which the lines are drawn. The size of the grid
     * is computed by taking the highest number for x and y positions. */
    public void createMap() {
        for (Coordinates coord : path){
            int[] best = coord.getMaxXAndY();
            this.x = max(this.x, best[0]);
            this.y = max(this.y, best[1]);
        }
        this.map = new int[this.x + 1][this.y + 1];
    }

    /** Fills the map with counts of the number of times each position is crossed by a line. */
    public void fillMap(boolean getDiagonal){
        this.createMap();
        for (Coordinates coord : path){
            List<List<Integer>> allPoints = coord.getStraightLine(getDiagonal);
            for (List<Integer> point : allPoints){
                this.map[point.get(0)][point.get(1)]++;
            }
        }
    }

    /** Returns the number of points that are crossed by 2 or more lines. Includes diagonals
     * if the boolean is set to true. */
    public int getAnswer(boolean getDiagonal) {
        this.fillMap(getDiagonal);
        int total = 0;
        for (int[] row : this.map){
            for (int val : row){
                if (val >= 2){
                    total++;
                }
            }
        }
        return total;
    }

    public static void main(String[] args) {
        CoordinatesReader reader = new CoordinatesReader();
        reader.readFile("day5.txt");
        int part1 = reader.getAnswer(false);
        System.out.println("Answer to Part 1 : " + part1);

        CoordinatesReader reader2 = new CoordinatesReader();
        reader2.readFile("day5.txt");
        int part2 = reader2.getAnswer(true);
        System.out.println("Answer to Part 2 : " +part2);
    }
}
