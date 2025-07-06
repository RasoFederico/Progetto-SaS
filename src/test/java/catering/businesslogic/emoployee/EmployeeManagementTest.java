package catering.businesslogic.emoployee;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.Employee;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

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
            assertEquals("RSAFRC03A02A124L", e.getTaxId(), "the new employee tax id should match");
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
        }
    }
}
