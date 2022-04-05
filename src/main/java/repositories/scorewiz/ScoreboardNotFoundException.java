package repositories.scorewiz;

import models.Scoreboard;

public class ScoreboardNotFoundException extends  RuntimeException{
    public ScoreboardNotFoundException(Scoreboard scoreboard) {
        super("Scoreboard not found: " + scoreboard);
    }
}
