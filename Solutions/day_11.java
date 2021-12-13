import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Octopus {

    private int[][] octopusGrid;

    /** Creates a new object by parsing a file representing octopuses with different energy levels.*/
    Octopus (String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            this.octopusGrid = new int[lines.size()][lines.get(0).length()];

            for (int i = 0; i < lines.size(); i++){
                char[] allNumbers = lines.get(i).toCharArray();
                for (int j = 0; j < allNumbers.length; j++){
                    this.octopusGrid[i][j] = Character.getNumericValue(allNumbers[j]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Helper method to add a new value to a given key. */
    public void updateMap(HashMap<Integer, List<Integer>> map, Integer key, Integer value){
        List<Integer> val = map.getOrDefault(key, new ArrayList<>());
        val.add(value);
        map.put(key, val);
    }

    /** Recursive function modelling the flashing of an octopus at a given position and returning
     * the number of flashes that happened. */
    public int flash(int i, int j, HashMap<Integer, List<Integer>> flashed){
        // Check that the positions are valid
        if (i < 0 || i >= this.octopusGrid.length || j < 0 || j >= this.octopusGrid[0].length){
            return 0;
        }
        this.octopusGrid[i][j] ++;
        if (this.octopusGrid[i][j] <= 9){
            return 0;
        }
        this.updateMap(flashed, i, j);
        // Gets the total number of octopuses who flashed during that step
        int total = 0;
        for (int k = -1; k <= 1; k++){
            for (int l = -1; l <= 1; l++){
                if (!flashed.containsKey(i + k) || !flashed.get(i + k).contains(j + l)){
                    total += flash(i + k, j + l, flashed);
                }
            }
        }
        this.octopusGrid[i][j] = 0;
        return 1 + total;
    }

    /** Increases the energy of each octopus by 1. */
    public void increaseEnergy(){
        for (int i = 0; i < this.octopusGrid.length; i++){
            for (int j = 0; j < this.octopusGrid[0].length; j++){
                this.octopusGrid[i][j]++;
            }
        }
    }

    /** Imitates the behaviour of octopuses over a given number of steps. Returns either the
     * number of flashes that occurred over all the steps (boolean=false), or the step at which
     * all flashes synchronised (boolean=true). */
    public int getFlashes(int steps, boolean getSynchronised){
        int total = 0;
        for (int s = 0; s < steps; s++){
            HashMap<Integer, List<Integer>> flashed = new HashMap<>();
            this.increaseEnergy();
            for (int i = 0; i < this.octopusGrid.length; i++) {
                for (int j = 0; j < this.octopusGrid[0].length; j++) {
                    if (this.octopusGrid[i][j] > 9){
                        total += this.flash(i,j,flashed);
                    }
                }
            }
            // Makes sure all flashed octopuses have their energy set to 0
            int sync = 0;
            for (int key : flashed.keySet()){
                for (int val : flashed.get(key)){
                    this.octopusGrid[key][val] = 0;
                    sync++;
                }
            }
            // Checks if all octopuses synchronised
            if (getSynchronised && sync == this.octopusGrid.length * this.octopusGrid[0].length){
                return s + 1;
            }
        }
        return total;
    }

    public static void main(String[] args) {
        Octopus grid = new Octopus("day11.txt");
        System.out.println("Answer to Part 1 : " + grid.getFlashes(100, false));

        Octopus grid2 = new Octopus("day11.txt");
        System.out.println("Answer to Part 2 : " + grid2.getFlashes(1000, true));
    }
}
