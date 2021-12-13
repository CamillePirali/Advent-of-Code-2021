import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class FloorScanner{

    private int[][] heightmap;

    /** Creates a new object from a file representing a heightmap. */
    FloorScanner(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            this.heightmap = new int[lines.size()][lines.get(0).length()];

            for (int i = 0; i < lines.size(); i++){
                char[] allNumbers = lines.get(i).toCharArray();
                for (int j = 0; j < allNumbers.length; j++){
                    this.heightmap[i][j] = Character.getNumericValue(allNumbers[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Method to find the points that are lower than any adjacent location. */
    public List<Integer> findLowPoints(){
        List<Integer> lowPoints = new ArrayList<>();
        for (int i = 0; i < this.heightmap.length; i++){
            for (int j = 0; j < this.heightmap[i].length; j++) {
                // Checks the 4 positions around the point to see if it is a local minimum :
                // above
                if (i != 0 && this.heightmap[i-1][j] <= this.heightmap[i][j]){
                    continue;
                }
                // under
                if (i != this.heightmap.length - 1 && this.heightmap[i+1][j] <= this.heightmap[i][j]){
                    continue;
                }
                // left
                if (j != 0 && this.heightmap[i][j-1] <= this.heightmap[i][j]){
                    continue;
                }
                // right
                if (j != this.heightmap[i].length - 1 && this.heightmap[i][j+1] <= this.heightmap[i][j]){
                    continue;
                }
                lowPoints.add(this.heightmap[i][j]);
            }
        }
        return lowPoints;
    }

    /** Returns the sum of the risk level of each low point on the map (solves part 1). */
    public int getRiskLevel(){
        int res = 0;
        List<Integer> lowPoints = this.findLowPoints();
        for (int i : lowPoints){
            res += 1 + i;
        }
        return res;
    }

    /** Helper method to add a new value for a given key. */
    public void updateMap(HashMap<Integer, List<Integer>> map, Integer key, Integer value){
        List<Integer> val = map.getOrDefault(key, new ArrayList<>());
        val.add(value);
        map.put(key, val);
    }

    /** Helper method to merge the contents of two maps, removing duplicates. */
    public HashMap<Integer, List<Integer>> mergeMaps(HashMap<Integer, List<Integer>> map1,
                                                     HashMap<Integer, List<Integer>> map2){
        for (int key : map2.keySet()){
            // if the key doesn't exist yet : adds it to the first map
            if (!map1.containsKey(key)){
                map1.put(key, map2.get(key));
            }
            else {
                for (int val : map2.get(key)){
                    if (! map1.get(key).contains(val)){
                        this.updateMap(map1, key, val);
                    }
                }
            }
        }
        return map1;
    }

    /** Helper method to keep only the top x values between an array of current
     * highest values and a new value. */
    public int[] updateTopX(int[] current, int value){
        for (int i = 0; i < current.length; i++){
            if (value > current[i]){
                int previous = current[i];
                current[i] = value;
                for (int j = i+1; j < current.length; j++){
                    int aux = current[j];
                    current[j] = previous;
                    previous = aux;
                }
                break;
            }
        }
        return current;
    }

    /** Finds all the basin borders (the highest points in the heightmap). */
    public HashMap<Integer, List<Integer>> getBasinLimits(){
        HashMap<Integer, List<Integer>> limits = new HashMap<>();
        for (int i = 0; i < this.heightmap.length; i++){
            for (int j = 0; j < this.heightmap[i].length; j++){
                if (this.heightmap[i][j] == 9){
                    this.updateMap(limits, i, j);
                }
            }
            // Adds a data point for the right border
            this.updateMap(limits, i, this.heightmap[i].length);
        }
        return limits;
    }

    /** Method to add points in the heightmap to basins based on other adjacent points. Returns
     * an imperfect solution that is then refined with the function recreateBasins. */
    public List<HashMap<Integer, List<Integer>>> getBasins(){
        HashMap<Integer, List<Integer>> limits = this.getBasinLimits();
        List<HashMap<Integer, List<Integer>>> basins = new ArrayList<>();

        for (int i = 0; i < this.heightmap.length; i++) {
            for (int j = 0; j < this.heightmap[i].length; j++) {
                // If the point isn't a limit : adds it to a basin
                if (!limits.get(i).contains(j)) {
                    boolean found = false;
                    // Checks if the point belongs to an existing basin
                    for (HashMap<Integer, List<Integer>> map : basins) {
                        // Checks if point is right neighbour of other point
                        if (j != 0 && map.get(i) != null && map.get(i).contains(j - 1)) {
                            this.updateMap(map, i, j);
                            found = true;
                        }
                        // Checks if point is below other point
                        else if (i != 0 && map.get(i-1) != null && map.get(i - 1).contains(j)) {
                            this.updateMap(map, i, j);
                            found = true;
                        }
                        // Checks if point is linked to a point belonging to the basin
                        else if (i != 0) {
                            int current = j;
                            while (!limits.get(i).contains(current)) {
                                if (map.get(i-1) != null && map.get(i - 1).contains(current)) {
                                    this.updateMap(map, i, j);
                                    found = true;
                                    break;
                                }
                                current++;
                            }
                        }
                    }
                    if (!found) {
                        HashMap<Integer, List<Integer>> toAdd = new HashMap<>();
                        this.updateMap(toAdd, i, j);
                        basins.add(toAdd);
                    }
                }
            }
        }
        return basins;
    }


    /** Refines the basins found by the getBasins method by merging adjacent basins together. */
    public List<HashMap<Integer, List<Integer>>> recreateBasins() {
        List<HashMap<Integer, List<Integer>>> basins = this.getBasins();
        List<HashMap<Integer, List<Integer>>> updatedBasins = new ArrayList<>();

        int[] visited = new int[basins.size()];
        int index = 0;
        while (index < basins.size()){
            if (visited[index] == 1){
                index++;
                continue;
            }
            HashMap<Integer, List<Integer>> toMerge = basins.get(index);
            List<Integer> mergeIndex = new ArrayList<>();
            for (int j = index + 1; j < basins.size(); j++){
                if (visited[j] == 1){
                    continue;
                }
                // If two maps have a key, value pair in common: adds them to the list of maps to add
                for (int key : toMerge.keySet()){
                    if (basins.get(j).containsKey(key)){
                        for (int val : toMerge.get(key)){
                            if (basins.get(j).get(key).contains(val)){
                                mergeIndex.add(j);
                                visited[j] = 1;
                                break;
                            }
                        }
                    }
                }
            }
            // For each map in the list : merges it with the current map
            for (int i : mergeIndex){
                toMerge = this.mergeMaps(toMerge, basins.get(i));
            }
            updatedBasins.add(toMerge);
            index++;
        }
        return updatedBasins;
    }

    /** Returns the product of the sizes of the x largest basins found. */
    public int getLargestBasins(int x){
        int[] topX = new int[x];
        List<HashMap<Integer, List<Integer>>> basins = this.recreateBasins();
        for (HashMap<Integer, List<Integer>> basin : basins){
            int size = 0;
            for (int key : basin.keySet()){
                for (int val : basin.get(key)){
                    size ++;
                }
            }
            topX = this.updateTopX(topX, size);
        }
        int result = 1;
        for (int n : topX){
            result *= n;
        }
        return result;
    }

    public static void main(String[] args) {
        FloorScanner scanner = new FloorScanner("day9.txt");
        System.out.println("Answer to Part 1 : " + scanner.getRiskLevel());
        System.out.println("Answer to Part 2 : " + scanner.getLargestBasins(3));
    }
}
