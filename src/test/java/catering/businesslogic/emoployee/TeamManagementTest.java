package catering.businesslogic.emoployee;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.Employee;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.event.Service;
import catering.businesslogic.event.TeamMember;
import catering.businesslogic.holidayRequest.HolidaysManager;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class TeamManagementTest {
    private static final Logger LOGGER = LogManager.getLogger(EmployeeManagementTest.class);

    private static CatERing app;
    private User organizer;
    private Service testService;

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

            testService = Service.loadByName("Pranzo Buffet Aziendale");
            assertNotNull(testService, "Test service should be loaded");

            app.getUserManager().fakeLogin(organizer.getUserName());

        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testTeamCreation() {
        LOGGER.info("Testing team creation");
        try {
            assertTrue(testService.getTeam().isEmpty(), "This service should have an empty initial team");
            testService.createTeam(List.of("cameriere", "cuoco dessert", "lavapiatti"));
            assertEquals(3, testService.getTeam().size(), "The team size should be 3");
            LOGGER.info("Created team: " + testService.getTeam());
        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testAddEmployeeToTeam() {
        LOGGER.info("Testing adding employee to team");
        EmployeeManager employeeMgr = app.getEmployeeManager();
        try {
            List<Employee> list = employeeMgr.getEmployees();
            boolean cameriere = false;
            boolean cuocoDessert = false;
            boolean lavapiatti = false;
            List<TeamMember> team = testService.getTeam();
            for (Employee e : list) {
                if (e.isServiceStaff() && !cameriere) {
                    cameriere = testService.addEmployeeToTeam(e, "cameriere");
                    assertTrue(cameriere);
                }
                if (e.isCook() && !cuocoDessert) {
                    cuocoDessert = testService.addEmployeeToTeam(e, "cuoco dessert");
                    assertTrue(cuocoDessert);
                }
                if (e.isServiceStaff() && !lavapiatti) {
                    lavapiatti = testService.addEmployeeToTeam(e, "lavapiatti");
                    assertTrue(lavapiatti);
                }
                assertNotNull(team.get(team.size()-1));
            }
            LOGGER.info("Added employees to team: " + team);
        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testSetNotes() {
        LOGGER.info("Testing setting notes to a member");
        try {
            for (TeamMember member : testService.getTeam()) {
                assertNotNull(member, "member should already have been added");
                member.setNote("This is a test note");
                assertNotNull(member.getNote());
            }
            LOGGER.info("Successfully set notes for team members");
        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testTeamAlreadyExists() {
        LOGGER.info("Testing team is already created");

        try {
            assertThrows(UseCaseLogicException.class, () -> {
                testService.createTeam(List.of("role"));
            }, "getEmployees should throw exception for non-owner");


        } catch (Exception e) {
            LOGGER.severe("Setup failed: " + e.getMessage());
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testAccessDeniedForNonOwner() {
        LOGGER.info("Testing access restriction for non-owner users");
        EmployeeManager employeeMgr = app.getEmployeeManager();

        try {
            User normalUser = User.load("Antonio");
            assertNotNull(normalUser, "normal user should exist");
            assertFalse(normalUser.isOrganizer(), "user should NOT have organizer role");

            app.getUserManager().fakeLogin(normalUser.getUserName());

            assertThrows(UseCaseLogicException.class, () -> {
                List<Employee> list = employeeMgr.getEmployees();
                testService.addEmployeeToTeam(list.get(0), "cameriere");
            });
        } catch (UseCaseLogicException e) {
            LOGGER.severe("Setup failed: " + e.getMessage());
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }
}
