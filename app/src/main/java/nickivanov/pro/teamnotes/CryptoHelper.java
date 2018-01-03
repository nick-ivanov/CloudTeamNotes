package nickivanov.pro.teamnotes;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class CryptoHelper {
    private String password;

    public CryptoHelper(String password) {
        this.password = password;
    }

    public String encrypt(String message) {
        try {
            return AESCrypt.encrypt(password, message);
        } catch (GeneralSecurityException e){
            return null;
        }
    }

    public String decrypt(String encryptedMsg) {
        try {
            return AESCrypt.decrypt(password, encryptedMsg);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }
}