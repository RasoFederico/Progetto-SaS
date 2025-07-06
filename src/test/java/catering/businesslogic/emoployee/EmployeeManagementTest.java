package catering.businesslogic.emoployee;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.Employee;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import catering.util.LogManager;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class EmployeeManagementTest {
    private static final Logger LOGGER = LogManager.getLogger(EmployeeManagementTest.class);

    private static CatERing app;
    private User organizer;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    @BeforeEach
    void setup(){
        try {
            organizer = User.load("Giovanni");
            assertNotNull(organizer, "organizer user should be logged");
            assertTrue(organizer.isOrganizer(), "user should have organizer role");

            app.getUserManager().fakeLogin(organizer.getUserName());

        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testEmployeeCreation() {
        LOGGER.info("Testing employee creation");
        EmployeeManager employeeManager = app.getEmployeeManager();
        try{
            boolean creation=employeeManager.addEmployee("Piero", "", "viale langhe 8",
                    "RSAFRC03A02A124L", 10, Employee.EmployeeRole.SERVICE);
            assertTrue(creation, "the new employee should be created");

            Employee e = EmployeeManager.getEmployee("RSAFRC03A02A124L");
            assertNotNull(e, "the new employee should be present in the database");
            assertEquals(e.getTaxId(), "RSAFRC03A02A124L", "the new employee tax id should match");
        }catch (UseCaseLogicException e){
            LOGGER.severe(e.getMessage());
        }
    }

     @Test
    @Order(2)
    void testEmployeeDelete(){
        LOGGER.info("Testing employee deletion");
        EmployeeManager employeeManager = app.getEmployeeManager();
        try {
            List<Employee> employees = employeeManager.getEmployees();
            assertFalse(employees.isEmpty(), "at least one employee should be present in the database");

            Employee e = EmployeeManager.getEmployee(employees.get(0).getTaxId());
            assertNotNull(e, "the employee should be present in the list");

            boolean res = employeeManager.deleteEmployee(e);
            assertTrue(res, "the employee should be deleted");

            e = EmployeeManager.getEmployee(employees.get(0).getTaxId());

            assertNull(e, "the employee should not be found in the database");
        }catch (UseCaseLogicException e){
            LOGGER.severe(e.getMessage());
        }

     }

    @Test
    @Order(3)
    void testEmployeeUpdate(){
        LOGGER.info("Testing employee update");
         EmployeeManager employeeManager = app.getEmployeeManager();
         try {
             List<Employee> employees = employeeManager.getEmployees();
             assertFalse(employees.isEmpty(), "at least one employee should be present in the database");

             Employee e = EmployeeManager.getEmployee(employees.get(0).getTaxId());
             assertNotNull(e, "the employee should be present in the list");

             e.setAddress("piazza degli scopeti, 5");
             e.setContact("333444666");
             boolean res = employeeManager.updateEmployee(e);
             assertTrue(res, "the employee should be updated");

             e = EmployeeManager.getEmployee(employees.get(0).getTaxId());
             assertEquals("piazza degli scopeti, 5",e.getAddress(), "the employee address should match");


     }catch (UseCaseLogicException e){
             LOGGER.severe(e.getMessage());
         }
     }

    @Test
    @Order(4)
    void testAccessDeniedForNonOrganizer() {
        LOGGER.info("Testing access restriction for non-organizer users");
        EmployeeManager employeeManager = app.getEmployeeManager();

        try {

            User normalUser = User.load("Antonio");
            assertNotNull(normalUser, "normal user should exist");
            assertFalse(normalUser.isOrganizer(), "user should NOT have organizer role");

            app.getUserManager().fakeLogin(normalUser.getUserName());


            assertThrows(UseCaseLogicException.class, () -> {
                employeeManager.getEmployees();
            }, "getEmployees should throw exception for non-organizer");


            assertThrows(UseCaseLogicException.class, () -> {
                employeeManager.addEmployee("Luca", "luca@example.com", "Via Roma 1", "ABCDEF12G34H567I", 5, Employee.EmployeeRole.COOK);
            }, "addEmployee should throw exception for non-organizer");


            Employee fake = new Employee("Luca", "luca@example.com", "Via Roma 1", "ABCDEF12G34H567I", 5, Employee.EmployeeRole.COOK);


            assertThrows(UseCaseLogicException.class, () -> {
                employeeManager.deleteEmployee(fake);
            }, "deleteEmployee should throw exception for non-organizer");


            assertThrows(UseCaseLogicException.class, () -> {
                employeeManager.updateEmployee(fake);
            }, "updateEmployee should throw exception for non-organizer");

        } catch (UseCaseLogicException e) {
            LOGGER.severe("Setup failed: " + e.getMessage());
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }



}
