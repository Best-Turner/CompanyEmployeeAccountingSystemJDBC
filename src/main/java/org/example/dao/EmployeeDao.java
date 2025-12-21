package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exception.EmployeeNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Employee;
import org.example.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmployeeDao {
    public static final Logger LOGGER = LogManager.getLogger(EmployeeDao.class);

    public boolean save(Employee employee) {
        LOGGER.debug("Попытка сохранить нового employee, {}", employee);

        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            session.persist(employee);
            LOGGER.debug("Employee переходит в статус manage");
            session.getTransaction().commit();
            LOGGER.trace("Транзакция успешна и commit");
        } catch (Exception e) {
            LOGGER.error("Ошибка сохранения работника, {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        LOGGER.info("Employee сохранен с ID: {}", employee.getId());
        return true;
    }


    public Optional<Employee> getById(int id) {
        Employee emp = null;
        LOGGER.debug("Попытка получить сотрудника c ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Сессия Hibernate получена");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция открыта");
            try {
                emp = session.find(Employee.class, id);
                transaction.commit();
                LOGGER.trace("Транзакция закрыта - commit");
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    LOGGER.trace("Откат транзакции из-за ошибки {}", e.getMessage());
                }
                LOGGER.error("Ошибка при выполнении запроса {}", e.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("Ошибка при получении сессии {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        LOGGER.info("Получили сотрудка по ID:", id);
        return Optional.ofNullable(emp);
    }

    public boolean deleteById(int id) {

        LOGGER.debug("Попытка удалить сотрудника c ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Сессия Hibernate получена");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция начата");
            try {
                Employee employee = session.find(Employee.class, id);
                if (employee == null) {
                    LOGGER.warn("Сотрудник с ID: {} не найден", id);
                    throw new EmployeeNotFoundException("Сотрудник с ID = " + id + " не найден");
                }
                session.remove(employee);
                LOGGER.debug("Сотрудник с ID: {} найден и удален", id);
                transaction.commit();
                LOGGER.trace("Транзакция закрыта - commit");
                LOGGER.info("Сотрудник с ID: {} удален", id);
                return true;
            } catch (NotFoundException ex) {
                if (transaction != null && transaction.isActive()) {
                    LOGGER.trace("Откат транзакции из-за ошибки {}", ex.getMessage());
                }
                LOGGER.warn("Ошибка удаления сотрудника с ID: {}, {}", id, ex.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        LOGGER.info("Сотрудник с ID: {} не удален", id);
        return false;
    }

    public List<Employee> getEmployees() {

        LOGGER.debug("Попытка получить список сотрудников");
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция открыта");
            try {
                List<Employee> list = session.createQuery("FROM Employee e ORDER BY e.id", Employee.class).list();
                transaction.commit();
                LOGGER.trace("Транзакция закрыта - commit");
                LOGGER.info("Список сотрудников получен");
                return list;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    LOGGER.debug("Откат транзакции из-за ошибки {}", ex.getMessage());
                }
                throw ex;
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка {}", e.getMessage());
        }
        LOGGER.info("Возврат пустого списка сотрудников из-за ошибки");
        return Collections.emptyList();
    }

    public boolean updateEmployee(int id, Employee updatedEmp) {
        LOGGER.debug("Попытка обновить сотрудника с ID: {}", id);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция открыта");
            try {
                LOGGER.trace("Попытка найти сотрудника с ID: {}", id);
                Employee employee = session.find(Employee.class, id);
                if (employee == null) {
                    LOGGER.warn("Сотрудник с ID: {} не найден", id);
                    throw new EmployeeNotFoundException("Работник с ID = " + id + " не найден");
                }
                LOGGER.debug("Сотрудник с ID: {} найден", id);
                employee.setFirstName(updatedEmp.getFirstName());
                LOGGER.debug("Установили новое имя - {}", updatedEmp.getFirstName());
                employee.setLastName(updatedEmp.getLastName());
                LOGGER.debug("Установили новую фамилию - {}", updatedEmp.getLastName());
                employee.setEmail(updatedEmp.getEmail());
                LOGGER.debug("Установили новый email - {}", updatedEmp.getEmail());
                employee.setDepartment(updatedEmp.getDepartment());
                LOGGER.debug("Установили новый департамент - {}", updatedEmp.getDepartment());
                employee.setHireDate(updatedEmp.getHireDate());
                LOGGER.debug("Установили новую дату устройства на работу - {}", updatedEmp.getHireDate());
                employee.setSalary(updatedEmp.getSalary());
                LOGGER.debug("Установили новую зарплату - {}", updatedEmp.getSalary());
                transaction.commit();
                LOGGER.trace("Транзакция успешна - commit");
            } catch (NotFoundException e) {
                LOGGER.warn("Ошибка получения сотрудника по ID: {}, {}", id, e.getMessage());
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    LOGGER.trace("Откат транзакции из-за ошибки {}", e.getMessage());
                }
                throw e;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    LOGGER.trace("Откат транзакции из-за ошибки {}", e.getMessage());
                    transaction.rollback();
                }
                System.out.println("Ошибка при обновлении сотрудника " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        LOGGER.info("Сотрудник успешно обновлен");
        return true;
    }

    public boolean deleteByEmail(String email) {
        LOGGER.debug("Попытка удалить сотрудника по email: {}", email);
        try (Session session = HibernateSessionFactoryUtil.getSession()) {
            LOGGER.trace("Получение Hibernate сессии");
            Transaction transaction = session.beginTransaction();
            LOGGER.trace("Транзакция открыта");
            try {
                LOGGER.trace("Выполнение запроса на удаление");
                int affectedRow = session.createQuery("DELETE FROM Employee e WHERE e.email = :email")
                        .setParameter("email", email)
                        .executeUpdate();
                if (affectedRow == 0) {
                    LOGGER.warn("Ошибка получения сотрудника по email: {}", email);
                    throw new EmployeeNotFoundException("Сотрудник с email = " + email + " не найден");
                }
                LOGGER.info("Сотрудник с email: {} удален", email);
                return true;
            } catch (Exception e) {
                LOGGER.error("Ошибка при удалении сотрудника по email: {}, {}", email, e.getMessage());
                if (transaction != null && transaction.isActive()) {
                    LOGGER.trace("Откат транзакции из-за ошибки {}", e.getMessage());
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
