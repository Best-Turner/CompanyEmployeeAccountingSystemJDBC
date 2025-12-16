package org.example.ui.impl;


import org.example.exception.NotFoundException;
import org.example.ui.HandlerInput;
import org.example.ui.Menu;

import java.util.ArrayList;
import java.util.List;

public class NavigationMenu extends Menu {

    private static final String TEXT_ENTER_0_TO_EXIT = "Введите 0 чтоб выйти из приложения";
    private static final String TEXT_ENTER_0_TO_BACK = "Введите 0 чтоб вернуться назад";
    private static final String TEXT_ENTER_COMMAND = "ВВЕДИТЕ КОМАНДУ ->";
    private static final int PERMISSIBLE_QUANTITY = 3;
    private static boolean isContinue = true;
    private final HandlerInput handler;
    private int countErrors = 0;
    private List<Menu> subMenu;

    public NavigationMenu(String title, HandlerInput handler) {
        super(title);
        this.handler = handler;
        subMenu = new ArrayList<>();
    }

    @Override
    public void execute() {
        while (isContinue) {
            if (countErrors >= PERMISSIBLE_QUANTITY) {
                System.out.println("\nКОЛИЧЕСТВО НЕ КОРРЕКТНОГО ВВОДА ПРЕВЫСИЛО ДОПУСТИМОЕ КОЛИЧЕСТВО," +
                                   " ВЫ ЗАБЛОКИРОВАНЫ! ОБРАТИТЕСЬ К АДМИНИСТРАТОРУ!");
                isContinue = false;
                return;
            }
            System.out.println("==== " + title + "  ====");
            int i = 1;
            for (Menu menu : subMenu) {
                System.out.println("\t" + i++ + ". " + menu.getTitle());
            }
            System.out.println();
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

            try {
                selectMenu.execute();
            } catch (NotFoundException e) {
                System.out.println(e.getBody().getMessage());
                countErrors++;
            }
        }
    }

    public void addSubMenu(Menu menu) {
        menu.setParent(this);
        subMenu.add(menu);
    }
}
