package org.example.service;

import org.example.dao.DepartmentDao;
import org.example.exception.DepartmentNotFoundException;
import org.example.model.Department;
import org.example.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.*;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    private static final String NAME = "IT";
    private static final String INPUT_BUDGET = "50000.50";
    private static final String ID = "1";

    private Department testDepartment;
    private List<Employee> testEmployees;
    @Mock
    private DepartmentDao departmentDao;
    @Mock
    private Scanner scanner;

    @InjectMocks
    DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDepartment = Department.builder()
                .id(1)
                .name("IT Department")
                .budget(100000.0)
                .build();

        testEmployees = Arrays.asList(
                new Employee(1, "John",
                        "Doe", "john.doe@example.com",
                        testDepartment, 50000.0, LocalDate.now()),
                new Employee(2, "Jane", "Smith",
                        "jane.smith@example.com", testDepartment, 60000.0, LocalDate.now())
        );

    }


    @Test
    void createWithValidInputShouldSaveDepartment() {
        // Arrange
        when(scanner.nextLine())
                .thenReturn(NAME)
                .thenReturn(INPUT_BUDGET);
        // Act
        departmentService.create();

        // Assert
        verify(scanner, times(2)).nextLine();

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentDao).save(departmentCaptor.capture());

        Department savedDepartment = departmentCaptor.getValue();
        assertEquals(NAME, savedDepartment.getName());
        assertEquals(Double.parseDouble(INPUT_BUDGET), savedDepartment.getBudget(), 0.001);
    }

    @Test
    void whenDeleteShouldBeDeleteByIdDepartmentAndReturnTrue() {
        final int id = Integer.parseInt(ID);
        // Arrange
        when(scanner.nextLine()).thenReturn(ID);
        when(departmentDao.deleteById(id)).thenReturn(true);

        // Act
        boolean result = departmentService.delete();

        // Assert
        verify(scanner, times(1)).nextLine();
        verify(departmentDao, times(1)).deleteById(id);
        assertTrue(result);
    }


    @Test
    void whenGetDepartmentsThenReturnListDepartments() {

        // Arrange
        List<Department> departments = initListDepartments();
        when(departmentDao.departments()).thenReturn(departments);

        // Act
        departmentService.departments();

        // Assert
        verify(departmentDao, times(1)).departments();
    }

    @Test
    void whenGetDepartmentByNotExistsIdThenThrowDepartmentNotFoundException() {
        // Arrange
        final int notExistsId = 100;
        when(scanner.nextLine()).thenReturn(ID);
        when(departmentDao.getById(notExistsId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.departmentById());
    }

    @Test
    void whenGetDepartmentByIdThenReturnDepartment() {
        // Arrange
        final int id = Integer.parseInt(ID);
        when(scanner.nextLine()).thenReturn(ID);
        when(departmentDao.getById(id)).thenReturn(Optional.ofNullable(testDepartment));

        // Act
        Department result = departmentService.departmentById();

        // Assert
        assertEquals(testDepartment, result);
    }

    @Test
    void testShowCountEmployeesDepartmentNotFound() {
        // Arrange
        when(scanner.nextLine()).thenReturn("999");
        when(departmentDao.getById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.showCountEmployees());

        assertNotNull(exception.getMessage());

        verify(departmentDao, never()).getCountEmployeesFromDepartment(anyInt());
    }


    private List<Department> initListDepartments() {
        List<Department> departments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            departments.add(Department.builder().name("Test - " + i).budget(100 * i).build());
        }
        return departments;
    }

    @Test
    void testGetEmployeesFromDepartmentWithEmployees() {
        // Arrange
        when(scanner.nextLine()).thenReturn("1");
        when(departmentDao.getById(1)).thenReturn(Optional.of(testDepartment));
        when(departmentDao.getEmployees(1)).thenReturn(testEmployees);

        // Capture System.out output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Act
            departmentService.getEmployeesFromDepartment();

            // Assert
            String output = outputStream.toString();

            // Проверяем заголовки
            assertTrue(output.contains("В департаменте " + testDepartment.getName() + " работают:"));
            assertTrue(output.contains("ID"));
            assertTrue(output.contains("Имя"));
            assertTrue(output.contains("Фамилия"));
            assertTrue(output.contains("Email"));

            // Проверяем данные сотрудников
            assertTrue(output.contains("John"));
            assertTrue(output.contains("Doe"));
            assertTrue(output.contains("john.doe@example.com"));
            assertTrue(output.contains("Jane"));
            assertTrue(output.contains("Smith"));
            assertTrue(output.contains("jane.smith@example.com"));

            // Проверяем вызовы DAO
            verify(departmentDao).getById(1);
            verify(departmentDao).getEmployees(1);

        } finally {
            // Восстанавливаем оригинальный System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void testShowCountEmployeesMultipleCalls() {
        // Arrange
        when(scanner.nextLine())
                .thenReturn("1")  // Первый вызов
                .thenReturn("2"); // Второй вызов

        Department department2 = Department.builder()
                .id(2)
                .name("HR Department")
                .budget(80000.0)
                .build();

        when(departmentDao.getById(1)).thenReturn(Optional.of(testDepartment));
        when(departmentDao.getById(2)).thenReturn(Optional.of(department2));

        when(departmentDao.getCountEmployeesFromDepartment(1)).thenReturn(15L);
        when(departmentDao.getCountEmployeesFromDepartment(2)).thenReturn(8L);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Act - два последовательных вызова
            departmentService.showCountEmployees();
            String firstOutput = outputStream.toString();

            // Очищаем поток для второго вызова
            outputStream.reset();
            departmentService.showCountEmployees();
            String secondOutput = outputStream.toString();

            // Assert
            assertTrue(firstOutput.contains("IT Department"));
            assertTrue(firstOutput.contains("15"));

            assertTrue(secondOutput.contains("HR Department"));
            assertTrue(secondOutput.contains("8"));

            // Проверяем количество вызовов
            verify(departmentDao, times(2)).getById(anyInt());
            verify(departmentDao, times(2)).getCountEmployeesFromDepartment(anyInt());

        } finally {
            System.setOut(originalOut);
        }
    }
}