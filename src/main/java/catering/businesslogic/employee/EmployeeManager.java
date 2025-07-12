package catering.businesslogic.employee;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.user.UserManager;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages employee-related operations such as adding, updating, promoting,
 * deleting, and retrieving employees from the database.
 * Access is restricted based on the current user's role.
 */
public class EmployeeManager {

    /**
     * Adds a new employee to the system.
     *
     * @param nominative         the full name of the employee
     * @param contact            contact details (phone or email)
     * @param address            the home address
     * @param taxId              unique tax ID for the employee
     * @param remainingHolidays  number of unused holidays
     * @param role               the role of the employee (COOK or SERVICE)
     * @return true if insertion was successful
     * @throws UseCaseLogicException if the current user is not an owner
     */
    public boolean addEmployee(String nominative, String contact, String address, String taxId, int remainingHolidays, Employee.EmployeeRole role) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can add a new employee");
        Employee employee = new Employee(nominative, contact, address, taxId, remainingHolidays, role);
        return employee.save();
    }

    /**
     * Deletes an employee from the system.
     *
     * @param employee the employee object to be deleted
     * @return true if deletion was successful
     * @throws UseCaseLogicException if the current user is not an organizer
     */
    public boolean deleteEmployee(Employee employee) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can delete employee");
        return employee.delete();
    }

    /**
     * Updates an employee's information in the database.
     *
     * @param updatedEmployee the employee object containing updated information
     * @return true if the update was successful
     * @throws UseCaseLogicException if the current user is not an organizer
     */
    public boolean updateEmployee(Employee updatedEmployee) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can update employee's data");
        String query = "UPDATE Employees SET nominative = ?, contact = ?, address = ?, remaining_holidays = ?, role = ? WHERE tax_id = ?";
        int res = PersistenceManager.executeUpdate(
                query,
                updatedEmployee.getNominative(),
                updatedEmployee.getContact(),
                updatedEmployee.getAddress(),
                updatedEmployee.getRemainingHolidays(),
                (updatedEmployee.isCook() ? 0 : 3),
                updatedEmployee.getTaxId()
        );
        return res != 0;
    }

    /**
     * Promotes an employee to permanent status.
     *
     * @param employee the employee to promote
     * @return true if the promotion was successful
     * @throws UseCaseLogicException if the current user is not an organizer
     */
    public boolean promoteEmployee(Employee employee) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only owner can promote employees");
        String query = "UPDATE Employees SET permanent = ? WHERE tax_id = ?";
        int res = PersistenceManager.executeUpdate(query, true, employee.getTaxId());
        return res != 0;
    }

    /**
     * Retrieves a list of employees, optionally filtering by permanent status.
     *
     * @param permanent optional boolean filter (true = only permanent, false = only non-permanent)
     * @return list of employees matching the criteria
     * @throws UseCaseLogicException if the current user is not an organizer
     */
    public List<Employee> getEmployees(boolean... permanent) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can get employees list");

        ArrayList<Employee> list = new ArrayList<>();

        String query;
        if (permanent.length == 0) {
            query = "SELECT * FROM Employees";
        } else if (permanent[0]) {
            query = "SELECT * FROM Employees WHERE permanent = 1";
        } else {
            query = "SELECT * FROM Employees WHERE permanent = 0";
        }

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Employee e = new Employee();
                assignValues(rs, e);
                list.add(e);
            }
        });

        return list;
    }

    /**
     * Retrieves an employee by their tax ID.
     *
     * @param taxId the unique identifier for the employee
     * @return the corresponding Employee object, or null if not found
     */
    public static Employee getEmployee(String taxId) {
        String query = "SELECT * FROM Employees WHERE tax_id = ?";
        Employee[] result = new Employee[1];

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Employee e = new Employee();
                assignValues(rs, e);
                result[0] = e;
            }
        }, taxId);

        return result[0];
    }

    /**
     * Helper method to extract employee data from a ResultSet and assign it to an Employee object.
     *
     * @param rs the result set from a SQL query
     * @param e  the employee object to populate
     * @throws SQLException if an SQL error occurs
     */
    private static void assignValues(ResultSet rs, Employee e) throws SQLException {
        e.setTaxId(rs.getString("tax_id"));
        e.setNominative(rs.getString("nominative"));
        e.setContact(rs.getString("contact"));
        e.setAddress(rs.getString("address"));
        e.setPermanent(rs.getBoolean("permanent"));
        e.setRemainingHolidays(rs.getInt("remaining_holidays"));
        int role = rs.getInt("role") == 0 ? 0 : 1;
        e.setRole(Employee.EmployeeRole.values()[role]);
    }
}
