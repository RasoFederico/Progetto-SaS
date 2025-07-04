package catering.businesslogic.holidayRequest;

import catering.businesslogic.employee.Employee;
import catering.persistence.PersistenceManager;

import java.sql.Date;
import java.util.Random;

public class HolidayRequest {
    public enum RequestState{
        ACCEPTED, REJECTED, PENDING
    }

    private int id;
    private Employee employee;
    private Date from;
    private Date to;
    private RequestState state;

    public HolidayRequest(Employee employee, Date from, Date to) {
        id=generateId();
        this.state = RequestState.PENDING;
        this.employee = employee;
        this.from = from;
        this.to = to;
    }

    public HolidayRequest(){}

    public boolean save(){
        String query = "INSERT INTO HolidayRequest (employee, from, to, state) VALUES (?, ?, ?, ?)";
        return PersistenceManager.executeUpdate(query, this.employee.getTaxId(), this.from, this.to, this.state.ordinal())!=0;
    }

    public boolean update(){
        String query = "UPDATE TeamMember SET state=? WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, this.state.ordinal(), this.id);
        return res!=0;
    }

    public void setId(int id){
        this.id = id;
    }

    private int generateId(){
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public RequestState getState() {
        return state;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public void setState(RequestState state) {
        this.state = state;
    }
}
