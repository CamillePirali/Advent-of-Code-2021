import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class PacketDecoder {

    private String outerPacket;
    private int start = 0;
    private int versionSum = 0;

    /** Stores the binary representation of the line given as input. */
    PacketDecoder(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            StringBuilder bitRepresentation = new StringBuilder();
            String line = lines.get(0);
            for (int i = 0; i < line.length(); i++){
                String hex = String.valueOf(line.charAt(i));
                StringBuilder bit = new StringBuilder(Integer.toBinaryString(Integer.parseInt(hex, 16)));
                while (bit.length() < 4){
                    bit.insert(0, "0");
                }
                bitRepresentation.append(bit);
            }
            this.outerPacket = bitRepresentation.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Finds the value of a literal packet. */
    public BigInteger readLiteral(){
        // Reads all the groups in the packet
        StringBuilder literal = new StringBuilder();
        boolean lastGroup = false;
        while (!lastGroup){
            literal.append(this.outerPacket.substring(start+1, start+5));
            if (this.outerPacket.charAt(start) == '0'){
                lastGroup = true;
            }
            start += 5;
        }
        return new BigInteger(literal.toString(), 2);
    }

    /** Returns the value of an operator packet (the sum, product or other of each literal contained
     * in the packet). */
    public BigInteger readOperator(int type) {
        BigInteger total = null;
        int lengthTypeID = Integer.parseInt(this.outerPacket.substring(start, start+1));
        start++;
        if (lengthTypeID == 0){
            int length = Integer.parseInt(this.outerPacket.substring(start, start+15), 2);
            start += 15;
            int current = start;
            while (start - current < length){
                BigInteger value = this.readPacket();
                total = this.computeResult(type, value, total);
            }
        }
        else {
            int nPackets = Integer.parseInt(this.outerPacket.substring(start, start+11), 2);
            start += 11;
            for (int j = 0; j < nPackets; j++){
                BigInteger value = this.readPacket();
                total = this.computeResult(type, value, total);
            }
        }
        return total;
    }

    /** Accumulates the values of a packet based on a rule defined by the packet type. */
    public BigInteger computeResult(int type, BigInteger value, BigInteger accumulator) {
        if (accumulator == null){
            return value;
        }
        return switch (type) {
            case 0 -> accumulator.add(value);
            case 1 -> accumulator.multiply(value);
            case 2 -> accumulator.min(value);
            case 3 -> accumulator.max(value);
            case 5 -> accumulator.compareTo(value) > 0 ? BigInteger.ONE : BigInteger.ZERO;
            case 6 -> accumulator.compareTo(value) < 0 ? BigInteger.ONE : BigInteger.ZERO;
            case 7 -> accumulator.compareTo(value) == 0 ? BigInteger.ONE : BigInteger.ZERO;
            default -> null;
        };
    }

    /** Recursively resolves a packet and returns its total value. */
    public BigInteger readPacket(){
        int version = Integer.parseInt(this.outerPacket.substring(start, start+3), 2);
        this.versionSum += version;
        int typeID = Integer.parseInt(this.outerPacket.substring(start+3, start+6), 2);
        start += 6;
        if (typeID == 4){
            return this.readLiteral();
        }
        else {
            return this.readOperator(typeID);
        }
    }

    public static void main(String[] args) {
        PacketDecoder decoder = new PacketDecoder("day16.txt");
        System.out.println("Answer to Part 2 : " + decoder.readPacket());
        System.out.println("Answer to Part 1 : " + decoder.versionSum);
    }
}
