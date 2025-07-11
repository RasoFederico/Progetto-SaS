package catering.businesslogic.shift;

import catering.businesslogic.employee.Employee;
import catering.businesslogic.employee.EmployeeManager;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;
import catering.util.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shift {
    private static final Logger LOGGER = LogManager.getLogger(Shift.class);

    private int id;
    private Date date;
    private Time startTime;
    private Time endTime;
    private Map<String, Employee> bookedEmployees;

    private Shift() {
        bookedEmployees = new HashMap<>();
    }

    public Shift(Date date, Time startTime, Time endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        bookedEmployees = new HashMap<>();
    }

    /**
     * Sets the ID of this shift.
     * Used when updating an existing shift.
     * 
     * @param id The ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the end time for this shift
     * 
     * @param endTime The new end time
     */
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ArrayList<Shift> getShiftTable() {
        return loadAllShifts();
    }

    public static ArrayList<Shift> loadAllShifts() {
        String query = "SELECT * FROM Shifts";
        ArrayList<Shift> shiftArrayList = new ArrayList<>();

        LOGGER.info("Loading all shifts from database");

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Shift s = new Shift();
                s.id = rs.getInt("id");

                // Use safe date/time handling for SQLite
                try {
                    String dateStr = rs.getString("date");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        s.date = Date.valueOf(dateStr);
                    }

                    String startTimeStr = rs.getString("start_time");
                    if (startTimeStr != null && !startTimeStr.isEmpty()) {
                        s.startTime = Time.valueOf(startTimeStr);
                    }

                    String endTimeStr = rs.getString("end_time");
                    if (endTimeStr != null && !endTimeStr.isEmpty()) {
                        s.endTime = Time.valueOf(endTimeStr);
                    }
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Error parsing date/time in Shift", ex);
                }

                s.bookedEmployees = loadBookings(s);
                shiftArrayList.add(s);
            }
        });

        // Sort the shifts by date and time
        shiftArrayList.sort((a, b) -> {
            if (a.getDate().before(b.getDate()))
                return -1;
            else if (a.getDate().after(b.getDate()))
                return 1;
            else if (a.getStartTime().before(b.getStartTime()))
                return -1;
            else if (a.getStartTime().after(b.getStartTime()))
                return 1;
            else
                return 0;
        });

        LOGGER.info("Loaded " + shiftArrayList.size() + " shifts");
        return shiftArrayList;
    }

    public static Shift loadItemById(int id) {
        String query = "SELECT * FROM Shifts WHERE id = ?";
        Shift[] shiftHolder = new Shift[1]; // Use array to allow modification in lambda

        LOGGER.fine("Loading shift with ID " + id);

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Shift s = new Shift();
                s.id = rs.getInt("id");

                // Use safe date/time handling for SQLite
                try {
                    String dateStr = rs.getString("date");
                    if (dateStr != null && !dateStr.isEmpty()) {
                        s.date = Date.valueOf(dateStr);
                    }

                    String startTimeStr = rs.getString("start_time");
                    if (startTimeStr != null && !startTimeStr.isEmpty()) {
                        s.startTime = Time.valueOf(startTimeStr);
                    }

                    String endTimeStr = rs.getString("end_time");
                    if (endTimeStr != null && !endTimeStr.isEmpty()) {
                        s.endTime = Time.valueOf(endTimeStr);
                    }
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Error parsing date/time in Shift", ex);
                }

                shiftHolder[0] = s;
            }
        }, id); // Pass id as parameter

        Shift s = shiftHolder[0];
        if (s != null && s.id == id) { // Check if we found the shift
            s.bookedEmployees = loadBookings(s);
            return s;
        }

        LOGGER.warning("Shift with ID " + id + " not found");
        return null; // Return null if shift not found
    }

    private static Map<String, Employee> loadBookings(Shift s) {
        Map<String, Employee> bookings = new HashMap<>();
        String query = "SELECT employee_id FROM ShiftBookings WHERE shift_id = ?";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String employeeId = rs.getString("employee_id");
                Employee e = EmployeeManager.getEmployee(employeeId);
                if (e != null) {
                    bookings.put(employeeId, e);
                }
            }
        }, s.id); // Pass s.id as parameter

        LOGGER.fine("Loaded " + bookings.size() + " bookings for shift ID " + s.id);
        return bookings;
    }

    public static Shift createShift(Date date, Time startTime, Time endTime) {
        Shift s = new Shift();
        s.date = date;
        s.startTime = startTime;
        s.endTime = endTime;
        s.bookedEmployees = new HashMap<>();

        String query = "INSERT INTO Shifts (date, start_time, end_time) VALUES (?, ?, ?)";

        PersistenceManager.executeUpdate(query,
                s.date,
                s.startTime,
                s.endTime);

        s.id = PersistenceManager.getLastId();

        LOGGER.info("Created new shift ID " + s.id + " on " + s.date);
        return s;
    }

    // Save a new shift to the database
    public void saveShift() {
        if (this.id > 0) {
            updateShift(); // If id exists, update instead of insert
            return;
        }

        String query = "INSERT INTO Shifts (date, start_time, end_time) VALUES (?, ?, ?)";
        PersistenceManager.executeUpdate(query,
                date.toString(),
                startTime.toString(),
                endTime.toString());

        this.id = PersistenceManager.getLastId();
    }

    // Update an existing shift
    public void updateShift() {
        if (this.id <= 0) {
            saveShift(); // If no id, insert instead of update
            return;
        }

        String query = "UPDATE Shifts SET date = ?, start_time = ?, end_time = ? WHERE id = ?";
        PersistenceManager.executeUpdate(query,
                date.toString(),
                startTime.toString(),
                endTime.toString(),
                this.id);
    }

    // Save a booking to the database
    public void saveBooking(Employee employee) {
        String query = "INSERT INTO ShiftBookings (shift_id, employee_id) VALUES (?, ?)";
        PersistenceManager.executeUpdate(query, this.id, employee.getTaxId());

        // Update local cache
        bookedEmployees.put(employee.getTaxId(), employee);
    }

    // Remove a booking from the database
    public void removeBooking(Employee employee) {
        String query = "DELETE FROM ShiftBookings WHERE shift_id = ? AND employee_id = ?";
        PersistenceManager.executeUpdate(query, this.id, employee.getTaxId());

        // Update local cache
        bookedEmployees.remove(employee.getTaxId());
    }

    // INSTANCE METHODS

    public Date getDate() {
        return date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void addBooking(Employee e) {
        if (this.bookedEmployees.containsKey(e.getTaxId())) {
            LOGGER.warning("Employee " + e.getNominative() + " is already booked for this shift");
            return;
        }

        String query = "INSERT INTO ShiftBookings (shift_id, employee_id) VALUES (?, ?)";
        PersistenceManager.executeUpdate(query, this.id, e.getTaxId());

        this.bookedEmployees.put(e.getTaxId(), e);
        LOGGER.info("Added booking for user " + e.getNominative() + " to shift ID " + this.id);
    }

    public Employee removeBookedEmployee(Employee e) {
        if (!this.bookedEmployees.containsKey(e.getTaxId())) {
            LOGGER.warning("Employee " + e.getNominative() + " is not booked for this shift");
            return null;
        }

        String query = "DELETE FROM ShiftBookings WHERE shift_id = ? AND employee_id = ?";
        int rowsAffected = PersistenceManager.executeUpdate(query, this.id, e.getTaxId());

        if (rowsAffected > 0) {
            Employee removed = this.bookedEmployees.remove(e.getTaxId());
            LOGGER.info("Removed booking for employee " + e.getNominative() + " from shift ID " + this.id);
            return removed;
        } else {
            LOGGER.warning("Failed to remove booking for employee " + e.getNominative() + " from shift ID " + this.id);
            return null;
        }
    }

    public boolean isBooked(Employee e) {
        return bookedEmployees.containsValue(e);
    }

    public int getId() {
        return id;
    }

    public Map<String, Employee> getBookedEmployees() {
        return new HashMap<>(bookedEmployees); // Return a copy to prevent modification
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(date)
                .append(" | <")
                .append(startTime)
                .append(" - ")
                .append(endTime)
                .append(">");

        if (!bookedEmployees.isEmpty()) {
            for (Employee e : bookedEmployees.values()) {
                sb.append("\n\t - ").append(e.toString());
            }
        }

        return sb.toString();
    }
}
