package catering.businesslogic.holidayRequest;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.user.UserManager;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HolidaysManager {
    public boolean acceptRequest(HolidayRequest hr) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can accept a holiday request");
        hr.setState(HolidayRequest.RequestState.ACCEPTED);
        return hr.update();
    }

    public boolean rejectRequest(HolidayRequest hr) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can reject a holiday request");
        hr.setState(HolidayRequest.RequestState.REJECTED);
        return hr.update();
    }

    public List<HolidayRequest> getHolidayRequests() throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only organizer can get holiday requests list");
        ArrayList<HolidayRequest> requests = new ArrayList<>();
        String query ="SELECT * FROM HolidayRequest";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                HolidayRequest hr = new HolidayRequest();
                hr.setId(rs.getInt("id"));
                hr.setState(HolidayRequest.RequestState.values()[rs.getInt("state")]);
                hr.setFrom(rs.getDate("from"));
                hr.setTo(rs.getDate("to"));
                hr.setEmployee(EmployeeManager.getEmployee(rs.getString("employee")));
                requests.add(hr);
            }
        });
        return requests;
    }


    public List<HolidayRequest> getPendingHolidayRequests(String employee) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can get pending holiday requests list");
        ArrayList<HolidayRequest> requests = new ArrayList<>();
        String query ="SELECT * FROM HolidayRequest WHERE state = ?";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                HolidayRequest hr = new HolidayRequest();
                hr.setId(rs.getInt("id"));
                hr.setState(HolidayRequest.RequestState.values()[rs.getInt("state")]);
                hr.setFrom(rs.getDate("from"));
                hr.setTo(rs.getDate("to"));
                hr.setEmployee(EmployeeManager.getEmployee(rs.getString("employee")));
                requests.add(hr);
            }
        }, HolidayRequest.RequestState.PENDING.ordinal());
        return requests;
    }



}
