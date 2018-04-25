import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
/**
 * Created by kanvi on 4/22/2018.
 */

/**
 * Signatures are verified by miners as a new transaction
 * are added to a block
 */
public class Transaction {
    // hash of the transaction
    public String transId;
    //Address of sender
    public PublicKey sender;
    //Address of receiver
    public PublicKey receiver;
    // amount in transcation
    public float value;
    // verify sender and data integrity
    public byte[] signature;

    //Inputs, which are references to previous transactions that prove the sender has funds to send
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

    //Outputs, which shows the amount relevant addresses received in the transaction. ( These outputs are referenced as inputs in new transactions )
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    // constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs ){
        this.sender = from;
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return AlgorithmsAndSignature.useSHA256(
                AlgorithmsAndSignature.getStringFromKey(sender) +
                        AlgorithmsAndSignature.getStringFromKey(receiver) +
                        Float.toString(value) + sequence
        );
    }

    //sign data using private key
    // need to sign more info - inputs/outputs & timestamps
    // for now just the minimum

    public void generateSignature(PrivateKey privateKey){
        String data = AlgorithmsAndSignature.getStringFromKey(sender) + AlgorithmsAndSignature.getStringFromKey(receiver) +
                Float.toString(value);
        signature = AlgorithmsAndSignature.applyECDSASig(privateKey, data);
    }

    // verify signed data for data integrity
    public boolean verifySignature(){
        String data = AlgorithmsAndSignature.getStringFromKey(sender) + AlgorithmsAndSignature.getStringFromKey(receiver) +
                Float.toString(value);
        return AlgorithmsAndSignature.verifyECDSASig(sender,data,signature);
    }

    /**
     * if new transactions could be created -return true
     * @return
     */
    public boolean isTransactionProcessingPossible(){
        if (verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // gather unspent transaction inputs
        for(TransactionInput transInput : inputs){
            transInput.UTXO = OurCryptoChain.UTXOs.get(transInput.transOutputId);
        }

        // check if transaction is valid
        // - what makes transaction valid ? --
//check if transaction is valid:
        if(getInputsValue() < OurCryptoChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transId = calulateHash();
        outputs.add(new TransactionOutput( this.receiver, value,transId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver,transId)); //send the left over 'change' back to sender		

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            OurCryptoChain.UTXOs.put(o.id , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it 
            OurCryptoChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it 
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }







}
