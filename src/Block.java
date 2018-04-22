/**
 * Created by kanvi on 4/22/2018.
 */
import java.util.Date;
public class Block {

    //private  int index;
    public  String previousHash;
    private long  timestamp;
    private  String data;
    public  String hash; // this will hold the digital signature
    private static int seed;

    public Block(String data,String previousHash ){

        //this.index = index;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.data = data;
        hash = calculateHash();
    }


    public String calculateHash(){
        String hashedValue = SHAAlgorithm.useSHA256(
                previousHash +
                        Long.toString(timestamp)
                        + Integer.toString(seed) +
                        data);
        return hashedValue;
    }

    /**
     *
     * @param difficulty - this is number of 0's they must solve for mining
     *                   Low difficulty like 1 or 2 is easily solvable on most computers
     *                   4-6 is good for testing
     *                   litecoin uses 442,592
     */
    public void blockMining(int difficulty){
        String target = new String(new char[difficulty]).replace('\0','0');
        while(!hash.substring(0,difficulty).equals(target)){
            seed++;
            hash = calculateHash();
        }

        System.out.println("Block Mined : " + hash);
    }


}
