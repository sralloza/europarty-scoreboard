package exceptions;

public class InvalidPrivateKeyException extends RuntimeException{
    public InvalidPrivateKeyException(){
        super("Invalid Private Key");
    }

    public InvalidPrivateKeyException(String privateKey){
        super("Invalid Private Key: " + privateKey);
    }
}
