import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Immutable class representing a rectangular area with defined bounds. */
record Area(int left, int right, int low, int high) {

    /** Returns true if the given coordinates fall within the area, false otherwise. */
    public boolean pointInArea(int x, int y) {
        return (x >= left && x <= right && y >= low && y <= high);
    }

    /** Returns true if the given coordinates are further than the area. */
    public boolean pointPastArea(int x, int y) {
        return (x > this.right || y < this.low);
    }
}

class ProbeLauncher {

    private Area target;
    private int successful = 0;

    /** Reads a file containing a line of the form "target area: x=70..96, y=-179..-124" and matches
     * its contents to the four bounds of an area object. */
    ProbeLauncher(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            // Parses the first line of the file
            String[] targetArea = lines.get(0).split("\s");
            Pattern p = Pattern.compile("[xy]=(-?\\d+)..(-?\\d+)");
            Matcher matchX = p.matcher(targetArea[2]);
            Matcher matchY = p.matcher(targetArea[3]);
            if (matchX.find() && matchY.find()) {
                this.target = new Area(Integer.parseInt(matchX.group(1)),
                        Integer.parseInt(matchX.group(2)),
                        Integer.parseInt(matchY.group(1)),
                        Integer.parseInt(matchY.group(2)));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Imitates the launching of a probe by incrementing its x and y positions in steps, following rules
     * defined in the problem statement. Stops when the probe either falls in the target area, or goes
     * past it (and therefore won't ever be able to reach it). Returns the highest y position reached by
     * a successful launch (part 1), and counts how many launches landed in the target area (part 2). */
    public int launchProbe(int xVelocity, int yVelocity){
        int x = 0;
        int y = 0;
        int maxY = 0;
        while (! this.target.pointPastArea(x, y)){
            x += xVelocity;
            y += yVelocity;
            if (xVelocity > 0){
                xVelocity--;
            }
            else if (xVelocity < 0){
                xVelocity++;
            }
            yVelocity--;
            // Part 1
            maxY = Math.max(maxY, y);
            // Part 2
            if (this.target.pointInArea(x, y)){
                this.successful++;
                return maxY;
            }
        }
        return 0;
    }

    /** Returns the y value of the highest launch that successfully landed in the target area. */
    public int highestSuccessfulLaunch(){
        int maxY = 0;
        for (int x = 0; x <= this.target.right(); x++){
            for (int y = this.target.low(); y < -this.target.low(); y++){
                maxY = Math.max(maxY, this.launchProbe(x, y));
            }
        }
        return maxY;
    }

    public static void main(String[] args) {
        ProbeLauncher launcher = new ProbeLauncher("day17.txt");
        System.out.println("Answer to Part 1 : " + launcher.highestSuccessfulLaunch());
        System.out.println("Answer to Part 2 : " + launcher.successful);
    }
}
