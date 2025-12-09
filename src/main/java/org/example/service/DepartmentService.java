package org.example.service;

import org.example.dao.DepartmentDao;
import org.example.exception.DepartmentNotFoundException;
import org.example.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DepartmentService {

    private final Scanner scanner;
    private final DepartmentDao departmentDao;

    public DepartmentService(Scanner scanner, DepartmentDao departmentDao) {
        this.scanner = scanner;
        this.departmentDao = departmentDao;
    }


    public void create() {
        System.out.println("Введите название департамента");
        String inputDepartmentName = scanner.nextLine();
        System.out.println("Введите бюджет департамента");
        String inputBudget = scanner.nextLine();
        double budget = checkInputNumber(inputBudget);
        departmentDao.save(Department.builder()
                .name(inputDepartmentName)
                .budget(budget)
                .build());
        System.out.println("Новый департамент сохранен!");
    }


    public boolean delete() {
        System.out.println("Введите ID департамента для удаления");
        String inputId = scanner.nextLine();
        boolean deleted = departmentDao.deleteById(getIntFromInput(inputId));
        if (deleted) {
            System.out.println("Департамент с ID = " + inputId + " удален");
            return true;
        }
        throw new DepartmentNotFoundException("Департамента с ID = " + inputId + " не найдено");
    }

    public void departments() {
        System.out.println("\n===== Список департаментов =====\n");
        List<Department> departments = departmentDao.departments();
        System.out.printf("%-3s | %3s%n", "ID", "Название");
        System.out.println("--------------");
        departments.forEach(department -> System.out.printf("%-3d | %-13s%n", department.getId(), department.getName()));
    }

    public Department departmentById() {
        System.out.println("Введите ID");
        String input = scanner.nextLine();
        int id = getIntFromInput(input);
        Optional<Department> maybeDepartment = departmentDao.getById(id);
        if (!maybeDepartment.isPresent()) {
            throw new DepartmentNotFoundException("Департамент с ID = " + id + " не найден");
        }
        Department department = maybeDepartment.get();

        System.out.printf("%-3s | %-13s | %10s%n", "ID", "Название", "Бюджет");
        System.out.println("---------------------------------------");

        System.out.printf("%-3d | %-13s | %10.2f%n",
                department.getId(),
                department.getName(),
                department.getBudget());
        return department;
    }

    public void update() {
    }


    private double checkInputNumber(String input) {
        try {
            double number = Double.parseDouble(input);
            if (number < 0) {
                throw new IllegalArgumentException("Значение не может быть отрицательным");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }


    private int getIntFromInput(String input) {
        return (int) checkInputNumber(input);
    }


    public void showCountEmployees() {
    }

    public void showDataEmployees() {
    }
}
