package org.example.ui.impl;


import org.example.ui.HandlerInput;
import org.example.ui.Menu;

import java.util.ArrayList;
import java.util.List;

public class NavigationMenu extends Menu {

    private final HandlerInput handler;


    private static final String TEXT_ENTER_COMMAND = "Введите команду";
    private static final String TEXT_ENTER_0_TO_BACK = "Введите 0 чтоб вернуться назад";
    private static final String TEXT_ENTER_0_TO_EXIT = "Введите 0 чтоб выйти из приложения";

    private List<Menu> subMenu;

    public NavigationMenu(String title, HandlerInput handler) {
        super(title);
        this.handler = handler;
        subMenu = new ArrayList<>();
    }

    @Override
    public void execute() {
        while (true) {
            System.out.println("==== " + title + "  ====");
            int i = 1;
            for (Menu menu : subMenu) {
                System.out.println("\t" + i++ + ". " + menu.getTitle());
            }
            System.out.println(parent == null ? TEXT_ENTER_0_TO_EXIT : TEXT_ENTER_0_TO_BACK);

            System.out.println("\n" + TEXT_ENTER_COMMAND);
            int inputCommand = handler.process(subMenu.size());

            switch (inputCommand) {
                case -1:
                    System.out.println("Не допустимое значение......");
                    continue;
                case -2:
                    System.out.println("Вы ввели не числовое значение......");
                    continue;
                case 0: {
                    if (this.parent == null) {
                        System.exit(0);
                    } else {
                        return;
                    }
                }
            }

            Menu selectMenu = subMenu.get(inputCommand - 1);
            selectMenu.execute();
        }
    }

    public void addSubMenu(Menu menu) {
        menu.setParent(this);
        subMenu.add(menu);
    }
}
