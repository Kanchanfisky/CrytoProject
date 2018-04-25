
import com.google.gson.GsonBuilder;
import sun.plugin2.message.Message;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class AlgorithmsAndSignature {

    public static String useSHA256(String input){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(input.getBytes("UTF-8"));
            StringBuffer buffer = new StringBuffer();
            for (int i =0; i< hashValue.length; i++){
                String hex = Integer.toHexString(0xff & hashValue[i]);// byte is a signed type in java . So & with 0xff makes it unsigned ( positive)
                if(hex.length()==1) buffer.append('0');
                buffer.append(hex);

            }
            return buffer.toString();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // applies ECDSA signature and returns the result ( as bytes)
    public static byte[] applyECDSASig(PrivateKey privateKey, String input){
        Signature dsa;
        byte [] output = new byte[0];
        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte [] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSignature = dsa.sign();
            output = realSignature;

        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }

    // verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] sig){
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(sig);

//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (SignatureException e) {
//            e.printStackTrace();
//        }
        } catch(Exception ex){
                throw new RuntimeException(ex);
            }

    }

    // get Encoded string from any key
    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //Tracks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(useSHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    //Short hand helper to turn Object into a json string
    public static String getJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

    //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }


}
