import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public PublicKey newOwner;// receiver is the newOwner
    public float value;
    // id of transactions this output was created in
    public String parentTransId;


    public TransactionOutput(PublicKey receiver, float value, String parentTransId ){
        this.newOwner = receiver;
        this.value = value;
        this.parentTransId = parentTransId;
        this.id = AlgorithmsAndSignature.useSHA256(AlgorithmsAndSignature.getStringFromKey(receiver) + Float.toString(value) + parentTransId);
    }

    public boolean isMyCoin(PublicKey publicKey ){
        return this.newOwner == publicKey;
    }
}
