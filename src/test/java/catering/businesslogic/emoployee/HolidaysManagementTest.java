package catering.businesslogic.emoployee;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.Employee;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.holidayRequest.HolidayRequest;
import catering.businesslogic.holidayRequest.HolidaysManager;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.List;
import java.util.logging.Logger;

import catering.util.LogManager;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class HolidaysManagementTest {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeManagementTest.class);

    private static CatERing app;
    private User owner;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    @BeforeEach
    void setup(){
        try {
            owner = User.load("Giulia");
            assertNotNull(owner, "owner user should be logged");
            assertTrue(owner.isOwner(), "user should have owner role");

            app.getUserManager().fakeLogin(owner.getUserName());

        } catch (UseCaseLogicException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    @Order(1)
    void holidayRequestApprovalTest(){
        LOGGER.info("Testing accept holiday request");
        HolidaysManager hm = app.getHolidaysManager();

        try {

            List<HolidayRequest> requestsList;
            requestsList = hm.getPendingHolidayRequests();
            assertNotNull(requestsList, "there should be pending holiday requests");

            HolidayRequest request = requestsList.get(0);
            assertNotNull(request, "there should be pending holiday request");
            assertSame(request.getState(), HolidayRequest.RequestState.PENDING, "the request should be pending");

            int requestId = request.getId();

            boolean res = hm.acceptRequest(request);
            assertTrue(res, "holiday request should be accepted");

            requestsList = hm.getHolidayRequests();

            request=null;
            for(HolidayRequest r : requestsList){
                if(r.getId() == requestId){
                    request = r;
                    break;
                }
            }

            assertNotNull(request, "the accepted request should be in the list");
            assertSame(request.getState(), HolidayRequest.RequestState.ACCEPTED, "the request should be accepted");

        }catch (UseCaseLogicException e){
            LOGGER.severe(e.getMessage());
        }

    }

    @Test
    @Order(2)
    void holidayRequestRejectTest(){
        LOGGER.info("Testing reject holiday request");
        HolidaysManager hm = app.getHolidaysManager();

        try {

            List<HolidayRequest> requestsList;
            requestsList = hm.getPendingHolidayRequests();
            assertNotNull(requestsList, "there should be pending holiday requests");

            HolidayRequest request = requestsList.get(0);
            assertNotNull(request, "there should be pending holiday request");
            assertSame(request.getState(), HolidayRequest.RequestState.PENDING, "the request should be pending");

            int requestId = request.getId();

            boolean res = hm.rejectRequest(request);
            assertTrue(res, "holiday request should be rejected");

            requestsList = hm.getHolidayRequests();

            request=null;
            for(HolidayRequest r : requestsList){
                if(r.getId() == requestId){
                    request = r;
                    break;
                }
            }

            assertNotNull(request, "the rejected request should be in the list");
            assertSame(request.getState(), HolidayRequest.RequestState.REJECTED, "the request should be rejected");

        }catch (UseCaseLogicException e){
            LOGGER.severe(e.getMessage());
        }

    }

    @Test
    @Order(3)
    void testAccessDeniedForNonOwner() {
        LOGGER.info("Testing access restriction for non-owner users");
        HolidaysManager hm = app.getHolidaysManager();

        try {

            User normalUser = User.load("Antonio");
            assertNotNull(normalUser, "normal user should exist");
            assertFalse(normalUser.isOwner(), "user should NOT have owner role");

            app.getUserManager().fakeLogin(normalUser.getUserName());


            assertThrows(UseCaseLogicException.class, () -> {
                hm.getHolidayRequests();
            }, "getEmployees should throw exception for non-owner");


        } catch (UseCaseLogicException e) {
            LOGGER.severe("Setup failed: " + e.getMessage());
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }

}
