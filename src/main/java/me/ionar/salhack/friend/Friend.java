package me.ionar.salhack.friend;

public class Friend {
    public Friend(String name, String alias, String cape) {
        this.name = name;
        this.alias = alias;
        this.cape = cape;
    }

    private final String name;
    private String alias;
    private String cape;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setCape(String cape) {
        this.cape = cape;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getCape() {
        return cape;
    }
}
