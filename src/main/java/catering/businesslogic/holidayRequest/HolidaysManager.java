package catering.businesslogic.holidayRequest;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.user.UserManager;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;
import catering.util.DateUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages operations related to holiday requests,
 * such as accepting, rejecting, and retrieving holiday requests.
 * Only users with owner privileges are allowed to manage holiday requests.
 */
public class HolidaysManager {

    /**
     * Accepts a given holiday request by setting its state to ACCEPTED.
     *
     * @param hr the holiday request to be accepted
     * @return true if the request was successfully updated in the database
     * @throws UseCaseLogicException if the current user is not an owner
     */
    public boolean acceptRequest(HolidayRequest hr) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can accept a holiday request");
        hr.setState(HolidayRequest.RequestState.ACCEPTED);
        return hr.update();
    }

    /**
     * Rejects a given holiday request by setting its state to REJECTED.
     *
     * @param hr the holiday request to be rejected
     * @return true if the request was successfully updated in the database
     * @throws UseCaseLogicException if the current user is not an owner
     */
    public boolean rejectRequest(HolidayRequest hr) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can reject a holiday request");
        hr.setState(HolidayRequest.RequestState.REJECTED);
        return hr.update();
    }

    /**
     * Retrieves all holiday requests from the database.
     *
     * @return a list of all holiday requests
     * @throws UseCaseLogicException if the current user is not an owner
     */
    public List<HolidayRequest> getHolidayRequests() throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only organizer can get holiday requests list");

        ArrayList<HolidayRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM HolidayRequest;";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                HolidayRequest hr = new HolidayRequest();
                hr.setId(rs.getInt("id"));
                hr.setState(HolidayRequest.RequestState.values()[rs.getInt("state")]);
                hr.setFrom(DateUtils.getDateFromResultSet(rs, "from_date"));
                hr.setTo(DateUtils.getDateFromResultSet(rs, "to_date"));
                hr.setEmployee(EmployeeManager.getEmployee(rs.getString("employee")));
                requests.add(hr);
            }
        });

        return requests;
    }

    /**
     * Retrieves all pending holiday requests from the database.
     *
     * @return a list of pending holiday requests
     * @throws UseCaseLogicException if the current user is not an owner
     */
    public List<HolidayRequest> getPendingHolidayRequests() throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can get pending holiday requests list");

        ArrayList<HolidayRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM HolidayRequest WHERE state = ?;";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                HolidayRequest hr = new HolidayRequest();
                hr.setId(rs.getInt("id"));
                hr.setState(HolidayRequest.RequestState.values()[rs.getInt("state")]);
                hr.setFrom(DateUtils.getDateFromResultSet(rs, "from_date"));
                hr.setTo(DateUtils.getDateFromResultSet(rs, "to_date"));
                hr.setEmployee(EmployeeManager.getEmployee(rs.getString("employee")));
                requests.add(hr);
            }
        }, HolidayRequest.RequestState.PENDING.ordinal());

        return requests;
    }
}
