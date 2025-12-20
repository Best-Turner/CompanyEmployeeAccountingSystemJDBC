package org.example.util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Department;
import org.example.model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateSessionFactoryUtil {
    public static final Logger LOGGER = LogManager.getLogger(HibernateSessionFactoryUtil.class);

    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .addAnnotatedClass(Department.class)
                    .addAnnotatedClass(Employee.class)
                    .buildSessionFactory();
            LOGGER.info("Соединение установлено");
        } catch (Exception e) {
            LOGGER.error("Ошибка инициализации SessionFactory", e);
            throw new ExceptionInInitializerError("Не удалось инициализировать Hibernate SessionFactory");
        }
    }

    private HibernateSessionFactoryUtil() {
    }


    public static Session getSession() {
        LOGGER.info("Получение сессии");
        return sessionFactory.openSession();
    }

    public static void shutDown() {
        LOGGER.info("Попытка закрыть SessionFactory");
        if (sessionFactory != null) {
            sessionFactory.close();
            LOGGER.info("SessionFactory закрыта");
        }
    }
}
