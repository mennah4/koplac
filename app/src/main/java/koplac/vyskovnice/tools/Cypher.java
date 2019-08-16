package koplac.vyskovnice.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cyphers with some algorithm the user's password
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Cypher {
    MessageDigest messageDigest;

    public Cypher(String algorithmName){
        try {
            this.messageDigest = MessageDigest.getInstance(algorithmName);
        }
        catch(NoSuchAlgorithmException e){e.printStackTrace();}
    }

    /**
     * Cyphers a string
     * @param s String a cifrar
     * @return String cifrado
     */
    public String stringCypher(String s){
        String result;
        byte[] aux;
        StringBuilder sb;

        messageDigest.update(s.getBytes());
        aux= messageDigest.digest();
        sb=new StringBuilder();
        for(int i=0;i<aux.length;i++){
            sb.append(Integer.toString((aux[i] & 0xff) + 0x100, 16).substring(1));
        }
        result=sb.toString();

        return result;
    }
}
