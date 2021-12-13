import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class PageFolder {

    public HashMap<Integer, List<Integer>> page = new HashMap<>();
    private int maxY = 0;
    private int maxX = 0;
    public List<String> instructions = new ArrayList<>();

    /** Creates a new object from a file representing a page containing some dots. */
    PageFolder(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            int separationIndex = 0;
            // Parses the input representing a list of dots on a page, keeping track of the size of the page
            for (String l : lines) {
                if (l.isEmpty()){
                    separationIndex = lines.indexOf(l);
                    break;
                }
                String[] coordinates = l.split(",");
                int y = Integer.parseInt(coordinates[1]);
                int x = Integer.parseInt(coordinates[0]);
                this.maxY = Math.max(y, this.maxY);
                this.maxX = Math.max(x, this.maxX);
                this.updateMap(page, y, x);
            }
            // Parses the folding instructions
            for (int i = separationIndex + 1; i < lines.size(); i++){
                String[] instructions = lines.get(i).split("\s");
                this.instructions.add(instructions[2]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Helper method to add a new value to a given key. */
    public void updateMap(HashMap<Integer, List<Integer>> map, Integer key, Integer value){
        List<Integer> val = map.getOrDefault(key, new ArrayList<>());
        val.add(value);
        map.put(key, val);
    }

    /** Helper method to remove a value from a key in a HashMap. */
    public void removeFromMap(HashMap<Integer, List<Integer>> map, Integer key, Integer value){
        List<Integer> val = map.get(key);
        int index = -1;
        for (int i = 0; i < val.size(); i++){
            if (Objects.equals(val.get(i), value)){
                index = i;
                break;
            }
        }
        val.remove(index);
        map.put(key, val);
    }

    /** Merges two horizontal lines together into the first line. */
    public void mergeLinesHorizontally (int line1, int line2){
        if (!this.page.containsKey(line1) && (this.page.containsKey(line2))){
            for (int n : this.page.get(line2)) {
                this.updateMap(page, line1, n);
            }
            return;
        }
        else if (!this.page.containsKey(line1) && !this.page.containsKey(line2)){
            return;
        }
        List<Integer> main = this.page.get(line1);
        if (this.page.containsKey(line2)) {
            for (int n : this.page.get(line2)) {
                if (!main.contains(n)) {
                    main.add(n);
                }
            }
        }
    }

    /** Merges two vertical lines into the first line. */
    public void mergeLinesVertically(int line1, int line2){
        for (int key : this.page.keySet()){
            if (!this.page.get(key).contains(line2)){
                continue;
            }
            else {
                if (!this.page.get(key).contains(line1)){
                    this.updateMap(page, key, line1);
                }
            }
        }
    }

    /** Counts the total number of dots present on the page. */
    public int countDots(){
        int total = 0;
        for (int key : this.page.keySet()){
            for (int val : this.page.get(key)){
                total++;
            }
        }
        return total;
    }

    /** Folds the page according to an instruction and returns the number of visible dots. */
    public int foldPage (String instruction){
        String[] components = instruction.split("=");
        String direction = components[0];
        int line = Integer.parseInt(components[1]);
        // Folds horizontally
        if (Objects.equals(direction, "y")){
            for (int i = 1; i <= this.maxY - line; i++){
                this.mergeLinesHorizontally(line - i, line + i);
                this.page.remove(line + i);
            }
            this.maxY = line;
        }
        else {
            // Folds vertically
            for (int i = 1; i <= this.maxX - line; i++){
                this.mergeLinesVertically(line - i, line + i);
                for (int key : this.page.keySet()){
                    if (this.page.get(key).contains(line + i)){
                        this.removeFromMap(this.page, key, line + i);
                    }
                }
            }
            this.maxX = line;
        }
        return this.countDots();
    }

    /** Folds the page following all the instructions passed when creating the object. */
    public void completeFold(){
        for (int i = 0; i < this.instructions.size(); i++){
            int res = this.foldPage(instructions.get(i));
            if (i == 0){
                System.out.println("Answer to Part 1 : " + res);
            }
        }
    }

    /** Prints the page by representing dots by "#" and non-dots by ".". */
    public void printMap(){
        for (int i = 0; i <= this.maxY; i++){
            StringBuilder line = new StringBuilder();
            if (this.page.containsKey(i)){
                for (int j = 0; j <= this.maxX; j++){
                    if (this.page.get(i).contains(j)){
                        line.append("#");
                    }
                    else {
                        line.append(".");
                    }
                }
            }
            else {
                line.append(String.join("", Collections.nCopies(this.maxX, ".")));
            }
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        PageFolder folder = new PageFolder("day13.txt");
        folder.completeFold();
        System.out.println("Answer to Part 2 : ");
        folder.printMap();
    }
}
