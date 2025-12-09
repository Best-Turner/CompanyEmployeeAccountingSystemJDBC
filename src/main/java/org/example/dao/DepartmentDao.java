package org.example.dao;

import org.example.model.Department;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDao {

    private final DatabaseConnection databaseConnection;

    public DepartmentDao(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public Department save(Department department) {
        final String sql = "INSERT INTO department (name, budget) VALUES (?, ?)";
        try (Connection connection = databaseConnection.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                connection.setAutoCommit(false);
                preparedStatement.setString(1, department.getName());
                preparedStatement.setDouble(2, department.getBudget());
                int affectedRow = preparedStatement.executeUpdate();
                if (affectedRow == 0) {
                    connection.rollback();
                    throw new RuntimeException("Creating department failed, no row affected");
                }
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        department = Department.builder()
                                .id(generatedKeys.getInt(1))
                                .name(department.getName())
                                .budget(department.getBudget())
                                .build();
                        connection.commit();
                    } else {
                        connection.rollback();
                        throw new RuntimeException("Creating department failed, no ID obtained");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return department;
    }

    public Optional<Department> getById(int id) {
        Department department = null;
        final String sql = "SELECT * FROM department d WHERE d.id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    department = extractDepartment(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(department);
    }

    public List<Department> departments() {
        List<Department> result = new ArrayList<>();
        final String sql = "SELECT * FROM department";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(extractDepartment(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM department WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, id);
            int affectedRow = preparedStatement.executeUpdate();
            if (affectedRow == 1) {
                connection.commit();
            } else {
                connection.rollback();
            }
            return affectedRow > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean update(int id, Department updatedDepartment) {
        final String sql = "UPDATE department SET name = ?, budget = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            if (id < 0) {
                throw new RuntimeException("ID can not be less than 0");
            }
            if (updatedDepartment.getName() != null) {
                preparedStatement.setString(1, updatedDepartment.getName());
            }
            if (updatedDepartment.getBudget() != null) {
                preparedStatement.setDouble(2, updatedDepartment.getBudget());
            }
            preparedStatement.setInt(3, id);

            int affectedRow = preparedStatement.executeUpdate();
            if (affectedRow == 1) {
                connection.commit();
            } else {
                connection.rollback();
            }
            return affectedRow > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Department extractDepartment(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        double budget = resultSet.getDouble("budget");
        return Department.builder()
                .id(id)
                .name(name)
                .budget(budget)
                .build();
    }
}
