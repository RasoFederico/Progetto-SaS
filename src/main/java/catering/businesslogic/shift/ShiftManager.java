package catering.businesslogic.shift;

import catering.businesslogic.employee.Employee;
import catering.businesslogic.user.User;
import catering.util.LogManager;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages shift operations in the CatERing system.
 * Acts as a facade to the Shift class, handling shift creation, retrieval, and
 * booking.
 */
public class ShiftManager {
    private static final Logger LOGGER = LogManager.getLogger(ShiftManager.class);

    /**
     * Constructor initializes by loading all shifts
     */
    public ShiftManager() {
        Shift.loadAllShifts();
    }

    /**
     * Gets all shifts in the system
     * 
     * @return List of all shifts
     */
    public ArrayList<Shift> getShiftTable() {
        return Shift.getShiftTable();
    }

    /**
     * Checks if a user is available for a shift
     * 
     * @param e The employee to check
     * @param s The shift to check
     * @return true if the user is available (not booked) for the shift
     */
    public boolean isAvailable(Employee e, Shift s) {
        return s.isBooked(e);
    }

    /**
     * Creates a new shift with the specified parameters
     * 
     * @param date      The date of the shift
     * @param startTime The start time
     * @param endTime   The end time
     * @return The newly created shift
     */
    public Shift createShift(Date date, Time startTime, Time endTime, String workPlace, boolean isKitchen) {
        LOGGER.info("Creating new shift on " + date + " at " + workPlace);
        return Shift.createShift(date, startTime, endTime);
    }

    /**
     * Loads a shift by its ID
     * 
     * @param id The ID of the shift to load
     * @return The loaded shift or null if not found
     */
    public Shift loadShiftById(int id) {
        LOGGER.info("Loading shift with ID: " + id);
        return Shift.loadItemById(id);
    }

    /**
     * Updates an existing shift
     * 
     * @param shift The shift to update with new values
     */
    public void updateShift(Shift shift) {
        LOGGER.info("Updating shift with ID: " + shift.getId());
        shift.updateShift();
    }

    /**
     * Books a employee for a shift
     * 
     * @param shift The shift to book
     * @param employee  The employee to book for the shift
     */
    public void bookUserForShift(Shift shift, Employee employee) {
        if (isAvailable(employee, shift)) {
            LOGGER.info("Booking employee " + employee.getNominative() + " for shift ID: " + shift.getId());
            shift.addBooking(employee);
        } else {
            LOGGER.warning("User " + employee.getNominative() + " is already booked for shift ID: " + shift.getId());
        }
    }

    /**
     * Removes a employee's booking from a shift
     * 
     * @param shift The shift to remove the booking from
     * @param employee The employee to remove from the shift
     * @return The removed employee or null if not booked
     */
    public Employee removeEmployeeFromShift(Shift shift, Employee employee) {
        LOGGER.info("Removing employee " + employee.getNominative() + " from shift ID: " + shift.getId());
        return shift.removeBookedEmployee(employee);
    }

    /**
     * Gets all employees booked for a shift
     * 
     * @param shift The shift to check
     * @return Map of employee IDs to Employee objects
     */
    public Map<String, Employee> getBookedEmployee(Shift shift) {
        return shift.getBookedEmployees();
    }

    /**
     * Gets shifts for a specific date
     * 
     * @param date The date to filter shifts for
     * @return List of shifts on the specified date
     */
    public List<Shift> getShiftsForDate(Date date) {
        List<Shift> dateShifts = new ArrayList<>();
        for (Shift shift : getShiftTable()) {
            if (shift.getDate().equals(date)) {
                dateShifts.add(shift);
            }
        }
        return dateShifts;
    }
}
