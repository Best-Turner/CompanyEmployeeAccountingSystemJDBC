package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(message = "Название должно быть от 2 до 50 символов", min = 2, max = 50)
    private String name;
    @NotNull(message = "Значение не может быть пустым")
    @DecimalMin(message = "Бюджет не может быть равным 0", value = "0.01", inclusive = true)
    @DecimalMax(message = "Значение не может быть больше 1_000_000_000", value = "1000000000", inclusive = true)
    private Double budget;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();

    public Department() {
    }

    private Department(int id, String name, Double budget) {
        this();
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getBudget() {
        return budget;
    }

    public static Builder builder() {
        return new Builder();
    }


    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }


    public static class Builder {
        private int id = 0;
        private String name = null;
        private Double budget = null;

        private Builder() {
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder budget(double budget) {
            this.budget = budget;
            return this;
        }

        public Department build() {
            return new Department(id, name, budget);
        }

    }

    @Override
    public String toString() {
        return "Department{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", budget=" + budget +
               '}';
    }
}
