package models;

public enum MainMenuButtonType {
    VIEW("VIEW"),
    EDIT("EDIT"),
    MOVE("MOVE"),
    DELETE("DELETE");

    private final String content;

    MainMenuButtonType(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
