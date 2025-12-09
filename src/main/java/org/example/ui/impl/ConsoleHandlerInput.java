package org.example.ui.impl;

import org.example.ui.HandlerInput;

import java.util.Scanner;

public class ConsoleHandlerInput implements HandlerInput {

    private final Scanner scanner;

    public ConsoleHandlerInput(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int process(int length) {
        int numberCommand = 0;
        String inputCommand = scanner.nextLine();
        try {
            numberCommand = Integer.parseInt(inputCommand);
            if (numberCommand > length || numberCommand < 0) {
                numberCommand = -1;
            }
            return numberCommand;
        } catch (NumberFormatException e) {
            numberCommand = -2;
        }
        return numberCommand;
    }
}
