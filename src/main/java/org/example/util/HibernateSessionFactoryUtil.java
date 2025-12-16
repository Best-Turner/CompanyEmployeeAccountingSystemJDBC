package org.example.util;

import org.example.model.Department;
import org.example.model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .addAnnotatedClass(Department.class)
                    .addAnnotatedClass(Employee.class)
                    .buildSessionFactory();
            System.out.println("Соединение установлено");
        } catch (Exception e) {
            System.out.println("что тот пошло не так");
            e.printStackTrace();
        }
    }

    private HibernateSessionFactoryUtil() {
    }


    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static void shutDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
