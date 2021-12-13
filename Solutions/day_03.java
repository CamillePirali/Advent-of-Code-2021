import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.lang.Math.abs;

class Submarine {

    private final List<String> diagnosticReport = new ArrayList<>();

    public void addReport (String file){
        try {
            // Reads the file and adds its parsed contents to a list
            List<String> lines = Files.readAllLines(Paths.get(file));
            this.diagnosticReport.addAll(lines);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Reads the report and returns a String for each bit
    * (1 if the most common bit at a given position is 1, 0 otherwise) */
    public String[] readDiagnosticReport (List<String> report, int start, int end) {
        // Checks which element is most frequent at a given position
        int[] bitOccurrence = new int[end - start];
        for (String binary : report){
            for (int i = 0; i < end - start; i++){
                // +1 for binary 1s, -1 for binary 0s
                bitOccurrence[i] += binary.charAt(start + i) == '1' ? 1: -1;
            }
        }
        // Transforms the numbers into bits
        String[] res = new String[bitOccurrence.length];
        for (int i = 0; i < bitOccurrence.length; i++){
            res[i] = bitOccurrence[i] >= 0 ? "1" : "0";
        }
        return res;
    }

    /* Gives the answer to part 1*/
    public int getPowerConsumption() {
        // Get the two metrics
        String[] gamma = readDiagnosticReport(this.diagnosticReport, 0, this.diagnosticReport.get(0).length());
        String[] epsilon = new String[gamma.length];
        for (int i = 0; i < epsilon.length; i++){
            epsilon[i] = Integer.toString(abs(Integer.parseInt(gamma[i]) - 1));
        }

        // Converts values to integers
        int gammaRate = Integer.parseInt(String.join("", gamma), 2);
        int epsilonRate = Integer.parseInt(String.join("", epsilon), 2);

        return gammaRate * epsilonRate;
    }

    /* Filters binary numbers until only one remains, by keeping only those containing at position i
    * either the most or the least frequent digit in all remaining numbers */
    public int filterNumbers(List<String> numbers, boolean mostCommon){
        for (int i = 0; i < numbers.get(0).length(); i++) {
            if (numbers.size() > 1) {
                int finalI = i;
                String[] mostFreq = readDiagnosticReport(numbers, finalI, finalI + 1);
                // Gets either the most or the least frequent element at position i
                int compare = mostCommon ? Integer.parseInt(mostFreq[0]) : Integer.parseInt(mostFreq[0]) == 1 ? 0 : 1;
                numbers = numbers.stream()
                        .filter((s) -> Character.getNumericValue(s.charAt(finalI)) == compare)
                        .collect(Collectors.toList());
            }
            else {
                break;
            }
        }
        return Integer.parseInt(numbers.get(0), 2);
    }

    /* Gives the answer to part 2 */
    public int getLifeSupportRating(){
        int oxygenGenerator = filterNumbers(this.diagnosticReport, true);
        int CO2Scrubber = filterNumbers(this.diagnosticReport, false);
        return oxygenGenerator * CO2Scrubber;
    }

    public static void main(String[] args) {
        Submarine submarine = new Submarine();
        submarine.addReport("day3.txt");
        int part1 = submarine.getPowerConsumption();
        int part2 = submarine.getLifeSupportRating();
        System.out.println("Answer to part 1 : " + part1);
        System.out.println("Answer to part 2 : " + part2);
    }
}
