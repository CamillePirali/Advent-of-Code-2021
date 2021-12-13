import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class representing a bingo board, initialised with a list of strings representing
 * a 5x5 grid. */
class BingoBoard {

    private final int[][] board = new int[5][5];
    private final boolean[][] wasFound = new boolean[5][5];
    private final int[] rowsTotals = new int[5];
    private final int[] columnsTotals = new int[5];

    /** Reads a string representation of a board and adds its contents to the object's board. */
    public BingoBoard (List<String> data){
        int i = 0;
        for (String line : data){
            line = line.trim();
            String[] numbers = line.split("\s+");
            int j = 0;
            for (String n : numbers){
                this.board[i][j] = Integer.parseInt(n);
                j++;
            }
            i++;
        }
    }

    /** Adds the number if it is to be found on the board. If the number was a winning number, returns
     * the player's score. Otherwise, returns -1.*/
    public int addNumber(int n){
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                if (this.board[i][j] == n){
                    this.wasFound[i][j] = true;
                    this.rowsTotals[i]++;
                    this.columnsTotals[j]++;
                    if(this.checkIfHasWon()){
                        return computeScore(n);
                    }
                    else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    /** Returns true if one of the lines or columns is complete. */
    public boolean checkIfHasWon() {
        for (int i : this.rowsTotals){
            if (i == 5){
                return true;
            }
        }
        for (int i : this.columnsTotals){
            if (i == 5){
                return true;
            }
        }
        return false;
    }

    /** Computes a winning board's score, by adding up the values of all unmarked numbers and
     * multiplying it by the winning number. */
    public int computeScore(int n) {
        int total = 0;
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                if (!this.wasFound[i][j]){
                    total += this.board[i][j];
                }
            }
        }
        return total * n;
    }

}

/** Class representing a bingo game, with a flexible number of players and numbers drawn.*/
class BingoGame {

    private int[] numbersDrawn;
    private final List<BingoBoard> players = new ArrayList<>();

    /** Creates a new game based on a text file. */
    public void initialiseGame(String file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            String numbers = lines.get(0);
            // Adds the numbers to be drawn to a class variable
            this.numbersDrawn = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();

            // For each board in the game: creates a new player (BingoBoard object) and adds it to the list.
            int i = 1;
            while (i < lines.size()) {
                if (lines.get(i).isEmpty()) {
                    players.add(new BingoBoard(lines.subList(i + 1, i + 6)));
                }
                i += 6;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the game until a player wins, and returns their score. If no player wins,
     * returns -1.
     */
    public int playGameAndWin() {
        for (int number : this.numbersDrawn) {
            for (BingoBoard player : this.players) {
                int res = player.addNumber(number);
                if (res != -1) {
                    return res;
                }
            }
        }
        return -1;
    }

    /**
     * Plays the game until the last player wins, and returns their score.
     */
    public int playGameAndLose() {
        for (int number : this.numbersDrawn) {
            // Creates a list of players who have won (to remove them from the list after the loop).
            List<BingoBoard> winningPlayers = new ArrayList<>();
            for (BingoBoard player : this.players) {
                int res = player.addNumber(number);
                if (res != -1) {
                    // if there is only one player left in the game: returns their winning score
                    if (this.players.size() > 1) {
                        winningPlayers.add(player);
                    } else {
                        return res;
                    }
                }
            }
            // removes the players who have already won
            for (BingoBoard player : winningPlayers) {
                this.players.remove(player);
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        BingoGame gameOne = new BingoGame();
        gameOne.initialiseGame("day4.txt");
        int winningScore = gameOne.playGameAndWin();

        BingoGame gameTwo = new BingoGame();
        gameTwo.initialiseGame("day4.txt");
        int losingScore = gameTwo.playGameAndLose();

        System.out.println("Answer to part 1 : " + winningScore);
        System.out.println("Answer to part 2 : " + losingScore);
    }
}
