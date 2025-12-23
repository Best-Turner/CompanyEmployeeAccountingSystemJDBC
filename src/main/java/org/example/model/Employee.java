package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(message = "Имя должно быть от 2 до 50 символов")
    private String firstName;
    @Column(name = "last_name")
    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;
    @Email(message = "Неверный формат")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    @ManyToOne
    @JoinColumn(name = "department_id")
    @NotNull(message = "Департамент не может быть null")
    private Department department;
    @NotNull(message = "Зарплата должна быть указана")
    @Positive(message = "Не может быть отрицательного числа")
    @DecimalMin(value = "0.0", inclusive = false, message = "Зарплата должна быть больше 0")
    @DecimalMax(value = "1000000.0", message = "Зарплата не может превышать 1,000,000")
    private Double salary;
    @Column(name = "hire_date")
    @NotNull(message = "Дата приема на работу должна быть указана")
    @PastOrPresent(message = "Дата приема не может быть в будущем")
    private LocalDate hireDate;

    public Employee() {
    }

    public Employee(int id, String firstName, String lastName, String email, Department department, Double salary, LocalDate hireDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
