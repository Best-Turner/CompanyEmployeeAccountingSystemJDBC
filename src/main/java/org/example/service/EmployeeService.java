package org.example.service;

import org.example.dao.EmployeeDao;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Department;
import org.example.model.Employee;

import java.time.LocalDate;
import java.util.Scanner;

public class EmployeeService {

    private static final String GMAIL_DOMAIN = "@gmail.com";
    private static final String MAIL_DOMAIN = "@mail.ru";
    private final Scanner scanner;
    private final EmployeeDao employeeDao;

    public EmployeeService(Scanner scanner, EmployeeDao employeeDao) {
        this.scanner = scanner;
        this.employeeDao = employeeDao;
    }

    public void create() {
        String firstName = getInput("Введите имя");

        String lastName = getInput("Введите фамилию");
        String email = getInput("Введите email");
        int departmentId = Integer.parseInt(getInput("Введите департамент ID"));
        double salary = Double.parseDouble(getInput("Введите зарплату"));
        LocalDate hireDate = LocalDate.now();
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setDepartment(Department.builder()
                .id(departmentId).build());
        employee.setSalary(salary);
        employee.setHireDate(hireDate);
        employeeDao.save(employee);
    }




    public boolean delete() {
        System.out.println("Введите ID или Email сотрудника, которого хотите удалить");
        String input = scanner.nextLine();
        if (isEmail(input)) {
            return deleteByEmail(input);
        } else {
            return deleteById(input);
        }
    }

    private boolean deleteById(String input) {
        boolean result = true;
        try {
            int inputId = Integer.parseInt(input);
            if (!employeeDao.deleteById(inputId)) {
                throw new EmployeeNotFoundException(
                        String.format("Сотрудник с ID - %d не найден", inputId));
            }
            System.out.println("Сотрудник с ID = " + inputId + " удален!!" );
        } catch (NumberFormatException e) {
            result = false;
            System.out.println("Ввели не числовое значение");
        }
        return result;
    }

    private boolean deleteByEmail(String input) {
        if (!employeeDao.deleteByEmail(input)) {
            throw new EmployeeNotFoundException(
                    String.format("Сотрудник с email - %s не найден, или неправильно введен email", input));
        }
        System.out.println("Сотрудник с email '" + input + "' успешно удален");
        return true;
    }

    private boolean isEmail(String input) {
        return input.endsWith(GMAIL_DOMAIN) || input.endsWith(MAIL_DOMAIN);
    }


    private String getInput(String text) {
        System.out.printf("---- %s ----", text);
        return scanner.nextLine();
    }
}
