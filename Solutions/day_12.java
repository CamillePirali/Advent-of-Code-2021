import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PathFinder {

    private final HashMap<String, List<String>> caves = new HashMap<>();

    /** Helper method to add a new value to a given key. */
    public void updateMap(HashMap<String, List<String>> map, String key, String value){
        List<String> val = map.getOrDefault(key, new ArrayList<>());
        val.add(value);
        map.put(key, val);
    }

    /** Adds one or two new pairs of caves to the hashmap. */
    private void addCave(String c1, String c2){
        if (Objects.equals(c2, "start") || Objects.equals(c1, "end")){
            this.updateMap(this.caves, c2, c1);
        }
        else {
            this.updateMap(this.caves, c1, c2);
            if (!Objects.equals(c1, "start") && !Objects.equals(c2, "end")){
                this.updateMap(this.caves, c2, c1);
            }
        }
    }

    /** Creates a new object by parsing a file representing a series of linked caves. */
    PathFinder(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            for (String line : lines){
                String[] l = line.split("-");
                this.addCave(l[0], l[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Counts the total number of different paths allowing to go from the start cave to the end
     * cave. If repeat is set to true, allows a path to visit one small cave twice ; all small
     * caves can only be visited once otherwise. */
    public int countPaths(String start, List<String> visited, boolean repeat){
        if (Objects.equals(start, "end")){
            return 1;
        }
        int total = 0;
        for (String next : this.caves.get(start)){
            List<String> aux = start.toLowerCase().equals(start)
                    ? Stream.concat(visited.stream(), Stream.of(start)).collect(Collectors.toList())
                    : visited;
            if (visited.contains(next) && repeat){
                total += countPaths(next, aux, false);
            }
            else if (!visited.contains(next)) {
                total += countPaths(next, aux, repeat);
            }
        }
        return total;
    }

    public static void main(String[] args) {
        PathFinder count = new PathFinder("day12.txt");
        System.out.println(count.countPaths("start", new ArrayList<>(), false));
        System.out.println(count.countPaths("start", new ArrayList<>(), true));
    }
}
