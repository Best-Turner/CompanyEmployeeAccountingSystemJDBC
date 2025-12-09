package org.example.ui.impl;

import org.example.service.DepartmentService;
import org.example.ui.HandlerInput;
import org.example.ui.HelperBuildMenu;
import org.example.ui.Menu;

public class HelperBuildDepartmentMenu implements HelperBuildMenu {

    private static final String TITLE_DEPARTMENT_MENU = "Департаменты";
    private static final String SHOW_ALL_DEPARTMENTS = "Показать все департаменты";
    private static final String ADD_NEW_DEPARTMENT = "Добавить новый департамент";
    private static final String DELETE_DEPARTMENT_BY_ID = "Удалить департамент по ID";
    private static final String UPDATE_DEPARTMENT = "Изменить департамент";
    private static final String GET_INFO_ABOUT_DEPARTMENT = "Получить информацию о департаменте";
    private static final String DETAILED_ABOUT_DEPARTMENT = "Поучить подробную информацию о департаменте";
    private static final String GET_COUNT_EMPLOYEES = "Показать количество сотрудников в департаменте";
    private static final String GET_EMPLOYEES_IN_DEPARTMENT = "Показать данные сотрудников в этом департаменте";
    private final DepartmentService departmentService;
    private final HandlerInput handlerInput;
    private NavigationMenu navigationDepartmentMenu;
    private NavigationMenu manageDepartmentMenu;

    public HelperBuildDepartmentMenu(DepartmentService departmentService, HandlerInput handlerInput) {
        this.departmentService = departmentService;
        this.handlerInput = handlerInput;
    }


    @Override
    public Menu build() {
        navigationDepartmentMenu = new NavigationMenu(TITLE_DEPARTMENT_MENU, handlerInput);
        initManageDepartmentMenu();
        addSubMenu();
        return navigationDepartmentMenu;
    }

    private void addSubMenu() {
        navigationDepartmentMenu.addSubMenu(new ActionMenu(SHOW_ALL_DEPARTMENTS, departmentService::departments));
        navigationDepartmentMenu.addSubMenu(manageDepartmentMenu);
        navigationDepartmentMenu.addSubMenu(new ActionMenu(ADD_NEW_DEPARTMENT, departmentService::create));
        navigationDepartmentMenu.addSubMenu(new ActionMenu(DELETE_DEPARTMENT_BY_ID, departmentService::delete));
        navigationDepartmentMenu.addSubMenu(new ActionMenu(UPDATE_DEPARTMENT, departmentService::update));
    }


    private Menu initManageDepartmentMenu() {
        manageDepartmentMenu = new NavigationMenu(DETAILED_ABOUT_DEPARTMENT, handlerInput);
        manageDepartmentMenu.setParent(navigationDepartmentMenu);
        manageDepartmentMenu.addSubMenu(new ActionMenu(GET_INFO_ABOUT_DEPARTMENT, departmentService::departmentById));
        manageDepartmentMenu.addSubMenu(new ActionMenu(GET_COUNT_EMPLOYEES, departmentService::showCountEmployees));
        manageDepartmentMenu.addSubMenu(new ActionMenu(GET_EMPLOYEES_IN_DEPARTMENT, departmentService::showDataEmployees));
        return manageDepartmentMenu;
    }
}
