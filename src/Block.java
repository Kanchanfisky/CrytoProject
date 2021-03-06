
import java.util.ArrayList;
import java.util.Date;
public class Block {

    //private  int index;
    public  String hash; // this will hold the digital signature
    public  String previousHash;// digital signature of previous transactions
    private long  timestamp;
    //private  String data;

    private static int nonce;// also called nonce in bitcoin
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.

    public Block(String previousHash ){

        //this.index = index;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        //this.data = data;
        hash = calculateHash();
    }


    public String calculateHash(){
        String hashedValue = AlgorithmsAndSignature.useSHA256(
                previousHash +
                        Long.toString(timestamp)
                        + Integer.toString(nonce) +
                        merkleRoot);
        return hashedValue;
    }

    /**
     *
     * @param difficulty - this is number of 0's they must solve for mining
     *                   Low difficulty like 1 or 2 is easily solvable on most computers
     *                   4-6 is good for testing
     *                   litecoin uses 442,592
     */
    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        merkleRoot = AlgorithmsAndSignature.getMerkleRoot(transactions);
        String target = AlgorithmsAndSignature.getDificultyString(difficulty); //Create a string with difficulty * "0"

        // Just to make it easy - lets just hash substring thats easy
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block has been MINED : " + hash);
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        // previousHash is "0" for genesis block
        if((previousHash != "0")) {
            if((!transaction.isTransactionProcessingPossible())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }


}
