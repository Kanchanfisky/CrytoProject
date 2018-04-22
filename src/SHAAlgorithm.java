/**
 * Created by kanvi on 4/22/2018.
 */
import sun.plugin2.message.Message;

import java.security.MessageDigest;
public class SHAAlgorithm {

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


}
