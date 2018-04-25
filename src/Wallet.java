/**
 * Created by kanvi on 4/22/2018.
 */
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    // need to sign transcation with private key to create signature
    public PrivateKey privateKey;
    // used to verify signature
    public PublicKey publicKey;
    // unspent transcations
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    //generates public-private Key Pair
    public Wallet(){
        generateKeyPair();
    }

    // / Elliptical curve key-pair
    public void generateKeyPair(){
        try{
            KeyPairGenerator keyGenerator =  KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(("prime192v1"));
            //256 bytes - acceptable
            keyGenerator.initialize(ecSpec,random);
            KeyPair keyPair = keyGenerator.generateKeyPair();
            // set the public and private keys for keypair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: OurCryptoChain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMyCoin(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }
    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient,float value ) {
        if(getBalance() < value) { //gather balance and check funds.
            System.out.println("Fund is not sufficient so transaction discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transOutputId);
        }
        return newTransaction;
    }
}
