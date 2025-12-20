package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exception.DepartmentNotFoundException;
import org.example.model.Department;
import org.example.model.Employee;
import org.example.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class DepartmentDao {
    private static final Logger LOGGER = LogManager.getLogger(DepartmentDao.class);

    public Department save(Department department) {
//        final String sql = "INSERT INTO department (name, budget) VALUES (?, ?)";
//        try (Connection connection = databaseConnection.getConnection()) {
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                connection.setAutoCommit(false);
//                preparedStatement.setString(1, department.getName());
//                preparedStatement.setDouble(2, department.getBudget());
//                int affectedRow = preparedStatement.executeUpdate();
//                if (affectedRow == 0) {
//                    connection.rollback();
//                    throw new RuntimeException("Creating department failed, no row affected");
//                }
//                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        department = Department.builder()
//                                .id(generatedKeys.getInt(1))
//                                .name(department.getName())
//                                .budget(department.getBudget())
//                                .build();
//                        connection.commit();
//                    } else {
//                        connection.rollback();
//                        throw new RuntimeException("Creating department failed, no ID obtained");
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return department;
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(department);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Ошибка сохранения департамента", e);
            throw e;
        }
        return department;
    }

    public Optional<Department> getById(int id) {
//        Department department = null;
//        final String sql = "SELECT * FROM department d WHERE d.id = ?";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setInt(1, id);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    department = extractDepartment(resultSet);
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return Optional.ofNullable(department);
        LOGGER.debug("Попытка получения департамента по ID: {}", id);
        Department department = null;
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Сессия Hibernate получена");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                LOGGER.debug("Выполнение find для Department c ID: {}", id);
                department = session.find(Department.class, id);
                LOGGER.trace("Получили Department по ID: {}", id);
                transaction.commit();
                LOGGER.trace("Транзакция завершилась удачно - commit");
            } catch (Exception e) {
                LOGGER.error("Ошибка получение департамента по ID: {}", id, e);
                LOGGER.trace("Попытка закрыть транзакцию");
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    LOGGER.debug("Откат транзакции из-за ошибки");
                }
                throw new RuntimeException(e.getMessage());
            }
        }
        LOGGER.debug("Возвращаем результат для ID {}: {}", id, department != null ? "найден" : "не найден");
        return Optional.ofNullable(department);
    }

    public List<Department> departments() {
//        List<Department> result = new ArrayList<>();
//        final String sql = "SELECT * FROM department";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//            while (resultSet.next()) {
//                result.add(extractDepartment(resultSet));
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        LOGGER.debug("Попытка получения списка всех департаментов");
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Сессия Hibernate получена");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                List<Department> departments = session.createQuery("FROM Department d ORDER BY d.id", Department.class).list();
                transaction.commit();
                LOGGER.trace("Транзакция успешна - commit");
                LOGGER.info("Список департаментов получен!");
                return departments;
            } catch (Exception e) {
                LOGGER.error("Ошибка получения списка департаментов, {}", e.getMessage(), e);
                LOGGER.trace("Попытка откатить транзакцию");
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    LOGGER.debug("Откат транзакции из-за ошибки");
                }
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public boolean deleteById(int id) {
//        final String sql = "DELETE FROM department WHERE id = ?";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            connection.setAutoCommit(false);
//            preparedStatement.setInt(1, id);
//            int affectedRow = preparedStatement.executeUpdate();
//            if (affectedRow == 1) {
//                connection.commit();
//            } else {
//                connection.rollback();
//            }
//            return affectedRow > 0;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        LOGGER.debug("Попытка удалить Department по ID: {}", id);
        boolean result = false;
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                LOGGER.debug("Выполнение запроса на удаление департамента с ID: {}", id);
                int deletedRow = session.createQuery("DELETE FROM Department WHERE id = :id")
                        .setParameter("id", id).executeUpdate();
                if (deletedRow == 1) {
                    transaction.commit();
                    LOGGER.trace("Удаление успешно. Транзакция закрыта");
                    result = true;
                } else {
                    LOGGER.warn("Удаление не удалось");
                    transaction.rollback();
                    LOGGER.trace("Откат транзакции");
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при удалении департамента по ID: {}, {}", id, e.getMessage(), e);
                if (transaction != null && transaction.isActive()) {
                    LOGGER.trace("Откат транзакции из-за ошибки");
                    transaction.rollback();
                }
            }
        }
        LOGGER.info("Результат удаления по ID: {} - {}", id, result ? "удален" : "не удален");
        return result;
    }


    public boolean update(int id, Department updatedDepartment) {
//        final String sql = "UPDATE department SET name = ?, budget = ? WHERE id = ?";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            connection.setAutoCommit(false);
//            if (id < 0) {
//                throw new RuntimeException("ID can not be less than 0");
//            }
//            if (updatedDepartment.getName() != null) {
//                preparedStatement.setString(1, updatedDepartment.getName());
//            }
//            if (updatedDepartment.getBudget() != null) {
//                preparedStatement.setDouble(2, updatedDepartment.getBudget());
//            }
//            preparedStatement.setInt(3, id);
//
//            int affectedRow = preparedStatement.executeUpdate();
//            if (affectedRow == 1) {
//                connection.commit();
//            } else {
//                connection.rollback();
//            }
//            return affectedRow > 0;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        LOGGER.debug("Попытка обновить Department по ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                LOGGER.debug("Выполнение запроса на обновление");
                int affectedRow = session.createQuery("UPDATE Department d " +
                                                      "SET d.name = :name, d.budget = :budget" +
                                                      " WHERE d.id = :id")
                        .setParameter("name", updatedDepartment.getName())
                        .setParameter("budget", updatedDepartment.getBudget())
                        .setParameter("id", id)
                        .executeUpdate();
                if (affectedRow != 1) {
                    LOGGER.warn("Департамент с ID: {} не найден", id);
                    throw new DepartmentNotFoundException("Департамент с ID = " + id + " не найден");
                }
                transaction.commit();
                LOGGER.trace("Обновление успешно. Транзакция закрыта");
                return true;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    LOGGER.debug("Откат транзакции из-за ошибки {}", e.getMessage());
                    transaction.rollback();
                }
                LOGGER.error("Ошибка обновления департамента с ID: {}, {}", id, e.getMessage(), e);
                throw new RuntimeException("Ошибка обновления департамента", e);
            }
        }
    }


//    private Department extractDepartment(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String name = resultSet.getString("name");
//        double budget = resultSet.getDouble("budget");
//        return Department.builder()
//                .id(id)
//                .name(name)
//                .budget(budget)
//                .build();
//    }

    public long getCountEmployeesFromDepartment(int id) {
//        int countEmployees = 0;
//        final String sql = "SELECT count(e.id) as count_employees " +
//                           "FROM department d " +
//                           "JOIN employee e on d.id=e.department_id " +
//                           "WHERE d.id = ?";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setInt(1, id);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    countEmployees = resultSet.getInt("count_employees");
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return countEmployees;

        LOGGER.debug("Попытка получить количество работников в Department по ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            Long countEmployees = session.createQuery("SELECT count(e) " +
                                                      "FROM Employee e " +
                                                      "WHERE e.department.id = :id", Long.class)
                    .setParameter("id", id).uniqueResult();
            session.getTransaction().commit();
            LOGGER.debug("Успешно. Транзакция закрыта");
            LOGGER.info("Получили количество работников");
            return countEmployees != null ? countEmployees : 0L;
        } catch (Exception e) {
            LOGGER.error("Ошибка получения сотрудников по департаменту c ID: {}, {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось получить количество сотрудников", e);
        }
    }

    public List<Employee> getEmployees(int id) {
//        List<Employee> employees = new ArrayList<>();
//        final String sql = "SELECT e.id, e.first_name, e.last_name, e.email " +
//                           "FROM department d " +
//                           "JOIN employee e ON d.id=e.department_id" +
//                           " WHERE d.id = ? " +
//                           "ORDER BY e.id";
//        try (Connection connection = databaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setInt(1, id);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    Employee employee = new Employee();
//                    employee.setId(resultSet.getInt("id"));
//                    employee.setFirstName(resultSet.getString("first_name"));
//                    employee.setLastName(resultSet.getString("last_name"));
//                    employee.setEmail(resultSet.getString("email"));
//                    employees.add(employee);
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return employees;

        List<Employee> employees = null;
        LOGGER.debug("Попытка получить данные работников в Department по ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                LOGGER.debug("Выполнение запроса получения данных сотрудников");
                Query query = session.createQuery("FROM Employee e " +
                                                  "WHERE e.department.id = :departmentId " +
                                                  "ORDER BY e.id");
                employees = query.setParameter("departmentId", id).list();
                transaction.commit();
                LOGGER.debug("Успешно. Транзакция закрыта");
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    LOGGER.debug("Откат транзакции из-за ошибки {}", e.getMessage());
                    transaction.rollback();
                }
                LOGGER.error("Ошибка получения сотрудников по департаменту c ID: {}, {}", id, e.getMessage(), e);
                throw e;
            }
        }
        LOGGER.info("Получили данные ссотрудников по ID: {} департамента", id);
        return employees == null ? Collections.emptyList() : employees;
    }
}
