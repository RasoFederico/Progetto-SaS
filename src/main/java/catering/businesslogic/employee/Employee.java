package catering.businesslogic.employee;

import catering.persistence.PersistenceManager;

/**
 * Represents an employee in the catering business.
 * An employee can either be a cook or service staff, and has associated personal and contract details.
 */
public class Employee {

    /**
     * Enum representing the possible roles of an employee.
     */
    public enum EmployeeRole {
        COOK, SERVICE
    }

    private String nominative;
    private String contact;
    private String address;
    private String taxId;
    private boolean permanent;
    private int remainingHolidays;
    private EmployeeRole role;

    /**
     * Constructs a new Employee with the given details.
     * By default, the employee is considered non-permanent.
     *
     * @param nominative         the full name of the employee
     * @param contact            the contact details (e.g., phone or email)
     * @param address            the address of the employee
     * @param taxId              the unique tax identification number
     * @param remainingHolidays  the number of holidays the employee can still take
     * @param role               the role of the employee (COOK or SERVICE)
     */
    public Employee(String nominative, String contact, String address, String taxId, int remainingHolidays, EmployeeRole role) {
        this.permanent = false;
        this.nominative = nominative;
        this.contact = contact;
        this.address = address;
        this.taxId = taxId;
        this.remainingHolidays = remainingHolidays;
        this.role = role;
    }

    /**
     * Default constructor for framework use or when fields will be set later.
     */
    public Employee() {}

    /**
     * Checks if the employee is a cook.
     *
     * @return true if the employee's role is COOK, false otherwise
     */
    public boolean isCook() {
        return role == EmployeeRole.COOK;
    }

    /**
     * Checks if the employee is part of the service staff.
     *
     * @return true if the employee's role is SERVICE, false otherwise
     */
    public boolean isServiceStaff() {
        return role == EmployeeRole.SERVICE;
    }

    /**
     * Saves this employee to the database.
     * The role is encoded as an integer (0 for COOK, 3 for SERVICE).
     *
     * @return true if the operation succeeded, false otherwise
     */
    public boolean save() {
        int roleNumeric = (this.isCook()) ? 0 : 3;

        String query = "INSERT INTO Employees (tax_id, nominative, contact, address, remaining_holidays, permanent, role) VALUES(?, ?, ?, ?, ?, ?, ?)";
        int res = PersistenceManager.executeUpdate(query, this.taxId, this.nominative, this.contact, this.address, this.remainingHolidays, this.permanent, roleNumeric);
        return res != 0;
    }

    /**
     * Deletes this employee from the database using their tax ID.
     *
     * @return true if the deletion was successful, false otherwise
     */
    public boolean delete() {
        String query = "DELETE FROM Employees WHERE tax_id = ?";
        int res = PersistenceManager.executeUpdate(query, this.taxId);
        return res != 0;
    }

    // === Setters and Getters ===

    /**
     * Sets the employee's permanent contract status.
     *
     * @param permanent true if the employee is permanent, false if temporary
     */
    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    /**
     * Checks if the employee is on a permanent contract.
     *
     * @return true if permanent, false if temporary
     */
    public boolean isPermanent() {
        return permanent;
    }

    public String getNominative() {
        return nominative;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public String getTaxId() {
        return taxId;
    }

    public int getRemainingHolidays() {
        return remainingHolidays;
    }

    public void setNominative(String nominative) {
        this.nominative = nominative;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public void setRemainingHolidays(int remainingHolidays) {
        this.remainingHolidays = remainingHolidays;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    /**
     * Returns a string representation of the employee.
     *
     * @return the employee's full name (nominative)
     */
    public String toString() {
        return this.nominative;
    }
}
