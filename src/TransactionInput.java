/**
 * Created by kanvi on 4/22/2018.
 */

/**
 * This class will be used to reference TransactionOutputs that have not yet been spent.
 * The transactionOutputId will be used to find the relevant TransactionOutput, allowing miners to check your ownership.
 */
public class TransactionInput {

    // reference to TransactionOutput -> transactionid ( hash value)
    public String transOutputId;
    // Contains the Unspent transaction output
    public TransactionOutput UTXO;

    public TransactionInput(String transOuputId){
        this.transOutputId = transOuputId;
    }

}
