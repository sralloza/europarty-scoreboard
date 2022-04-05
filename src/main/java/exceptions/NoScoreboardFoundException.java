package exceptions;

public class NoScoreboardFoundException extends RuntimeException{
    public NoScoreboardFoundException(){
        super("Could not find any scoreboard");
    }
}
