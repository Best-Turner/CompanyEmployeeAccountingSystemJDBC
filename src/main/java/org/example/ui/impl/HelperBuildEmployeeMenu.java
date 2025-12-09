package org.example.ui.impl;

import org.example.dao.EmployeeDao;
import org.example.service.EmployeeService;
import org.example.ui.HandlerInput;
import org.example.ui.HelperBuildMenu;
import org.example.ui.Menu;

public class HelperBuildEmployeeMenu implements HelperBuildMenu {
    private static final String TITLE_EMPLOYEE_MENU = "Работники";
    private static final String SHOW_ALL_EMPLOYEES = "Показать всех сотрудников";
    private static final String ADD_NEW_EMPLOYEE= "Добавить нового сотрудника";
    private static final String DELETE_BY_ID = "Удалить сотрудника по ID";
    private final EmployeeService employeeService;
    private final EmployeeDao employeeDao;
    private final HandlerInput handlerInput;
    private NavigationMenu navigationEmployeeMenu;

    public HelperBuildEmployeeMenu(EmployeeService employeeService, EmployeeDao employeeDao, HandlerInput handlerInput) {
        this.employeeService = employeeService;
        this.employeeDao = employeeDao;
        this.handlerInput = handlerInput;

    }

    @Override
    public Menu build() {
        navigationEmployeeMenu = new NavigationMenu(TITLE_EMPLOYEE_MENU, handlerInput);
        addSubMenu();
        return navigationEmployeeMenu;
    }


    private void addSubMenu() {
        navigationEmployeeMenu.addSubMenu(new ActionMenu(SHOW_ALL_EMPLOYEES, () -> {
            employeeDao.getEmployees().forEach(emp -> System.out.println(emp.getId() +
                                                                         " " + emp.getFirstName() +
                                                                         " -> " + emp.getDepartment().getName()))
            ;
        }));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(ADD_NEW_EMPLOYEE, employeeService::create));
        navigationEmployeeMenu.addSubMenu(new ActionMenu(DELETE_BY_ID, employeeService::delete));
    }
}
