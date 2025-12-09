package org.example.ui.impl;

import org.example.ui.Menu;

import java.io.IOException;

public class ActionMenu extends Menu {

    private final Runnable runnable;

    public ActionMenu(String title, Runnable runnable) {
        super(title);
        this.runnable = runnable;
    }

    @Override
    public void execute() {
        System.out.println(title);
        runnable.run();
        System.out.println("\nНажмите Enter для продолжения...");
        try {
            System.in.read();
            System.out.println("---------------------");
        } catch (IOException e) {
        }
    }
}
