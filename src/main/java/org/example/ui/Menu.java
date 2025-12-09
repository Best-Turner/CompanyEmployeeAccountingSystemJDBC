package org.example.ui;

public abstract class Menu {
    protected Menu parent;
    protected final String title;
    protected Menu(String title) {
        this.title = title;
    }
    public abstract void execute();

    public String getTitle() {
        return title;
    }

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }
}
