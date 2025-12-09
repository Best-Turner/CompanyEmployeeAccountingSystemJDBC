package org.example.dao;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDao {

    private static final String GET_EMPLOYEE_WITH_DEPARTMENT_NAME_SQL = """
                SELECT e.*, d.name as department_name
                FROM employee e 
                JOIN department d on e.department_id = d.id ORDER BY e.id
            """;
    private final DatabaseConnection databaseConnection;

    public EmployeeDao(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public boolean save(Employee employee) {
        boolean result = false;
        final String sql = "INSERT INTO employee(first_name, last_name, email, department_id, salary, hire_date) VALUES(?,?,?,?,?,?)";
        try (Connection connection = databaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                preparedStatement.setString(1, employee.getFirstName());
                preparedStatement.setString(2, employee.getLastName());
                preparedStatement.setString(3, employee.getEmail());
                preparedStatement.setInt(4, employee.getDepartment().getId());
                preparedStatement.setDouble(5, employee.getSalary());
                preparedStatement.setDate(6, Date.valueOf(employee.getHireDate()));
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    connection.commit();
                    result = true;
                } else {
                    connection.rollback();
                    return result;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<Employee> getById(int id) {
        Employee employee = null;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_WITH_DEPARTMENT_NAME_SQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    employee = extractEmployee(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(employee);
    }

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM employee WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Employee> getEmployees() {
        List<Employee> result = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_WITH_DEPARTMENT_NAME_SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(extractEmployee(resultSet));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateEmployee(int id, Employee updatedEmp) {
        final String sql = """
                UPDATE employee SET 
                first_name = ?, 
                last_name = ?,
                email = ?,
                department_id = ?,
                salary = ?,
                hire_date = ?
                WHERE id = ?               
                """;
        boolean resultUpdate = false;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int tempTransactionIsolation = connection.getTransactionIsolation();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            preparedStatement.setString(1, updatedEmp.getFirstName());
            preparedStatement.setString(2, updatedEmp.getLastName());
            preparedStatement.setString(3, updatedEmp.getEmail());
            preparedStatement.setInt(4, updatedEmp.getDepartment().getId());
            preparedStatement.setDouble(5, updatedEmp.getSalary());
            preparedStatement.setDate(6, Date.valueOf(updatedEmp.getHireDate()));
            preparedStatement.setInt(7, id);

            int resultUpdatedRow = preparedStatement.executeUpdate();
            if (resultUpdatedRow == 1) {
                connection.commit();
                connection.setTransactionIsolation(tempTransactionIsolation);
                resultUpdate = true;
            } else {
                connection.rollback();
                connection.setTransactionIsolation(tempTransactionIsolation);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultUpdate;
    }


    private Employee extractEmployee(ResultSet resultSet) throws SQLException {

        Employee employee;
            int id = resultSet.getInt("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String email = resultSet.getString("email");
            double salary = resultSet.getDouble("salary");
            LocalDate hireDate = resultSet.getDate("hire_date").toLocalDate();
            int departmentId = resultSet.getInt("department_id");
            String departmentName = resultSet.getString("department_name");
            Department department = Department.builder()
                    .id(departmentId)
                    .name(departmentName)
                    .build();
            employee = new Employee(id, firstName, lastName, email, department, salary, hireDate);

        return employee;
    }

    public boolean deleteByEmail(String input) {
        return false;
    }


    private boolean existsByEmail(String email) {
        final String sql = "SELECT EXISTS(SELECT 1 FROM employee WHERE email = ?)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
