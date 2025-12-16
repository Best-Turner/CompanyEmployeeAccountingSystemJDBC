package org.example.ui.impl;

import org.example.service.EmployeeService;
import org.example.ui.HandlerInput;
import org.example.ui.HelperBuildMenu;
import org.example.ui.Menu;

public class HelperBuildEmployeeMenu implements HelperBuildMenu {
    private static final String TITLE_EMPLOYEE_MENU = "Работники";
    private static final String SHOW_ALL_EMPLOYEES = "Показать всех сотрудников";
    private static final String GET_BY_ID = "Найти сотрудника по ID";
    private static final String ADD_NEW_EMPLOYEE= "Добавить нового сотрудника";
    private static final String UPDATE_EMPLOYEE= "Изменить данные сотрудника";
    private static final String DELETE_BY_ID = "Удалить сотрудника по ID";
    private final EmployeeService employeeService;
    private final HandlerInput handlerInput;
    private NavigationMenu navigationEmployeeMenu;

    public HelperBuildEmployeeMenu(EmployeeService employeeService, HandlerInput handlerInput) {
        this.employeeService = employeeService;
        this.handlerInput = handlerInput;

    }

    @Override
    public Menu build() {
        navigationEmployeeMenu = new NavigationMenu(TITLE_EMPLOYEE_MENU, handlerInput);
        addSubMenu();
        return navigationEmployeeMenu;
    }


    private void addSubMenu() {
        navigationEmployeeMenu.addSubMenu(new ActionMenu(GET_BY_ID, employeeService::getById));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(SHOW_ALL_EMPLOYEES, employeeService::getAll));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(ADD_NEW_EMPLOYEE, employeeService::create));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(UPDATE_EMPLOYEE, employeeService::update));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(DELETE_BY_ID, employeeService::delete));
    }
}
