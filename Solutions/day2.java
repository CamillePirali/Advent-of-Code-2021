import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class DirectionData {
    private final String direction;
    private final int distance;

    public DirectionData(String dir, int dis) {
        this.direction = dir;
        this.distance = dis;
    }

    public String getDirection() {
        return this.direction;
    }

    public int getDistance() {
        return this.distance;
    }
}


class Submarine {

    private int depth = 0;
    private int position = 0;
    private int aim = 0;
    private final List<DirectionData> directions = new ArrayList<>();

    public void giveDirections(String file){
        try {
            // Reads the file and adds its parsed contents to a list
            List<String> lines = Files.readAllLines(Paths.get(file));
            for (String line : lines) {
                String[] data = line.split("\s");
                directions.add(new DirectionData(data[0], Integer.parseInt(data[1])));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int moveSubmarinePart1() {
        for (DirectionData direction : this.directions){
            switch (direction.getDirection()) {
                case "forward" -> this.position += direction.getDistance();
                case "down" -> this.depth += direction.getDistance();
                case "up" -> this.depth -= direction.getDistance();
                default -> System.out.println("no match");
            }
        }
        return this.depth * this.position;
    }

    public int moveSubmarinePart2() {
        for (DirectionData direction : this.directions){
            switch (direction.getDirection()) {
                case "forward" -> {
                    this.position += direction.getDistance();
                    this.depth += this.aim * direction.getDistance();
                }
                case "down" -> this.aim += direction.getDistance();
                case "up" -> this.aim -= direction.getDistance();
                default -> System.out.println("no match");
            }
        }
        return this.depth * this.position;
    }

    public static void main(String[] args) {
        Submarine submarinePart1 = new Submarine();
        submarinePart1.giveDirections("day2.txt");
        int position1 = submarinePart1.moveSubmarinePart1();
        System.out.println("Answer to part 1 : " + position1);

        Submarine submarinePart2 = new Submarine();
        submarinePart2.giveDirections("day2.txt");
        int position2 = submarinePart2.moveSubmarinePart2();
        System.out.println("Answer to part 2 : " + position2);
    }
}
