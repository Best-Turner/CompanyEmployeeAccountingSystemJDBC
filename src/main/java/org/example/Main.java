package org.example;

import org.example.dao.DepartmentDao;
import org.example.dao.EmployeeDao;
import org.example.service.DepartmentService;
import org.example.service.EmployeeService;
import org.example.ui.HandlerInput;
import org.example.ui.HelperBuildMenu;
import org.example.ui.impl.ConsoleHandlerInput;
import org.example.ui.impl.HelperBuildDepartmentMenu;
import org.example.ui.impl.HelperBuildEmployeeMenu;
import org.example.ui.impl.NavigationMenu;
import org.example.util.DatabaseConnection;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        Scanner scanner = new Scanner(System.in);

        DepartmentDao departmentDao = new DepartmentDao(connection);
        EmployeeDao employeeDao = new EmployeeDao(connection);

        EmployeeService employeeService = new EmployeeService(scanner, employeeDao);
        DepartmentService departmentService = new DepartmentService(scanner, departmentDao);
        HandlerInput handler = new ConsoleHandlerInput(scanner);

        NavigationMenu mainMenu = new NavigationMenu("Главное меню", handler);

        HelperBuildMenu helperEmployeeMenu = new HelperBuildEmployeeMenu(employeeService, handler);
        HelperBuildMenu helperDepartmentMenuMenu = new HelperBuildDepartmentMenu(departmentService, handler);

        mainMenu.addSubMenu(helperDepartmentMenuMenu.build());
        mainMenu.addSubMenu(helperEmployeeMenu.build());

        mainMenu.execute();

        scanner.close();
    }
}
