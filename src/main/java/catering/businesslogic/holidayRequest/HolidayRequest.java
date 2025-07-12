package catering.businesslogic.holidayRequest;

import catering.businesslogic.employee.Employee;
import catering.persistence.PersistenceManager;

import java.sql.Date;

/**
 * Represents a holiday request made by an employee.
 * It includes the requesting employee, the start and end dates of the holiday, and the request's current state.
 */
public class HolidayRequest {

    /**
     * Enumeration representing the possible states of a holiday request.
     */
    public enum RequestState {
        ACCEPTED, REJECTED, PENDING
    }

    private int id;
    private Employee employee;
    private Date from;
    private Date to;
    private RequestState state;

    /**
     * Constructs a new HolidayRequest with the given employee and date range.
     * The request state is initialized to PENDING.
     *
     * @param employee the employee requesting the holiday
     * @param from     the start date of the requested holiday
     * @param to       the end date of the requested holiday
     */
    public HolidayRequest(Employee employee, Date from, Date to) {
        id = -1;
        this.state = RequestState.PENDING;
        this.employee = employee;
        this.from = from;
        this.to = to;
    }

    /**
     * Default constructor required for certain serialization and framework operations.
     */
    public HolidayRequest() {}

    /**
     * Saves this holiday request to the database.
     *
     * @return true if the save was successful, false otherwise
     */
    public boolean save() {
        String query = "INSERT INTO HolidayRequest (employee, from_date, to_date, state) VALUES (?, ?, ?, ?)";
        int ret = PersistenceManager.executeUpdate(query, this.employee.getTaxId(), this.from, this.to, this.state.ordinal());
        id = PersistenceManager.getLastId();
        return ret != 0;
    }

    /**
     * Updates this holiday request in the database.
     *
     * @return true if the update was successful, false otherwise
     */
    public boolean update() {
        String query = "UPDATE HolidayRequest SET state=?, from_date=?, to_date=? WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, this.state.ordinal(), this.from, this.to, this.id);
        return res != 0;
    }

    /**
     * Deletes this holiday request from the database.
     *
     * @return true if the deletion was successful, false otherwise
     */
    public boolean delete() {
        String query = "DELETE FROM HolidayRequest WHERE id = ?";
        int res = PersistenceManager.executeUpdate(query, this.id);
        return res != 0;
    }

    /**
     * Sets the ID of this holiday request.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of this holiday request.
     *
     * @return the request ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the employee associated with this request.
     *
     * @return the requesting employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Returns the start date of the holiday.
     *
     * @return the start date
     */
    public Date getFrom() {
        return from;
    }

    /**
     * Returns the end date of the holiday.
     *
     * @return the end date
     */
    public Date getTo() {
        return to;
    }

    /**
     * Returns the current state of the request.
     *
     * @return the request state
     */
    public RequestState getState() {
        return state;
    }

    /**
     * Sets the employee for this request.
     *
     * @param employee the employee to associate with the request
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Sets the start date of the holiday.
     *
     * @param from the new start date
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * Sets the end date of the holiday.
     *
     * @param to the new end date
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * Sets the state of the holiday request.
     *
     * @param state the new request state
     */
    public void setState(RequestState state) {
        this.state = state;
    }
}
