package org.example.service;

import org.example.dao.EmployeeDao;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Department;
import org.example.model.Employee;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EmployeeService {

    private static final String GMAIL_DOMAIN = "@gmail.com";
    private static final String MAIL_DOMAIN = "@mail.ru";
    private static final String EMPLOYEE_EMPTY_MESSAGE = "Работники отсутствуют";
    private static final String EMPLOYEE_NOT_FOUND_MESSAGE = "Сотрудник с ID - %d не найден";
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
                        String.format(EMPLOYEE_NOT_FOUND_MESSAGE, inputId));
            }
            System.out.println("Сотрудник с ID = " + inputId + " удален!!");
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
        System.out.printf("---- %s ----%n", text);
        return scanner.nextLine();
    }

    public void getAll() {
        List<Employee> employees = employeeDao.getEmployees();
        System.out.printf("%-3s | %-10s | %-10s | %3s%n", "ID", "Имя", "Фамилия", "email");
        System.out.println("-------------------------------------");
        if (employees.isEmpty()) {
            System.out.println(EMPLOYEE_EMPTY_MESSAGE);
        } else {
            employees.forEach(emp
                    -> System.out.printf("%-3d | %-10s | %-10s | %3s%n",
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail()));
        }

    }

    public void getById() {
        String input = getInput("Введите ID работника которого хотите подучить");
        int id = parseInputToInt(input);
        Optional<Employee> employeeById = employeeDao.getById(id);
        if (!employeeById.isPresent()) {
            throw new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND_MESSAGE, id));
        }
        System.out.printf("%-3s | %-10s | %-10s | %-10s | %-10s | %-10s | %-5s%n",
                "ID", "Имя", "Фамилия", "Департамент", "Зарплата", "Дата приема на работу", "email");
        System.out.println("----------------------------------------------------------------------------------------");
        Employee emp = employeeById.get();
        System.out.printf("%-3d | %-10s | %-10s | %-11s | %-10.2f | %-21s | %-5s%n",
                emp.getId(), emp.getFirstName(), emp.getLastName(), emp.getDepartment().getName(), emp.getSalary(), emp.getHireDate(),emp.getEmail());
    }

    public void update() {
        String input = getInput("Введите ID работника которого хотите изменить");
        int id = parseInputToInt(input);
        Optional<Employee> byId = employeeDao.getById(id);
        if (!byId.isPresent()) {
            throw new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND_MESSAGE, id));
        }

        Employee emp = changeFieldEmployee(byId.get());
        employeeDao.updateEmployee(id, emp);
        System.out.println("Данные успешно изменены!!!");
    }

    private Employee changeFieldEmployee(Employee employee) {

        Field[] fields = Employee.class.getDeclaredFields();
        int position = 1;
        for (Field field : fields) {
            System.out.println(position + ". " + convertField(field.getName()));
            position++;
        }
        int select = parseInputToInt(getInput("Что хотите изменить?"));
        if (select >= fields.length || select <= 0) {
            throw new IllegalArgumentException("Не верная команда");
        }
        String nameFieldToChange = fields[select - 1].getName();
        System.out.println("Введите новое значение для (" + convertField(nameFieldToChange) + ")");
        try {
            Field field = employee.getClass().getDeclaredField(nameFieldToChange);
            field.setAccessible(true);
            field.set(employee, scanner.nextLine());
            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return employee;
    }

    private String convertField(String name) {
        switch (name) {
            case "id":
                return "ID";

            case "firstName":
                return "Имя";

            case "lastName":
                return "Фамилию";

            case "email":
                return "email";

            case "department":
                return "Департамент";

            case "salary":
                return "Зарплата";

            case "hireDate":
                return "Дата приема на работу";
            default:
                return name;
        }
    }


    private int parseInputToInt(String input) {
        try {
            int employeeId = Integer.parseInt(input);
            if (employeeId <= 0) {
                throw new IllegalArgumentException("ID не может быть меньше или равно 0");
            }
            return employeeId;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
