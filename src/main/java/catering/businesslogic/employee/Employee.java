package catering.businesslogic.employee;

import catering.persistence.PersistenceManager;

public class Employee {
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

    public Employee(String nominative, String contact, String address, String taxId, int remainingHolidays, EmployeeRole role) {
        this.permanent = false;
        this.nominative = nominative;
        this.contact = contact;
        this.address = address;
        this.taxId = taxId;
        this.remainingHolidays = remainingHolidays;
        this.role = role;
    }

    public boolean isCook(){
        return role == EmployeeRole.COOK;
    }

    public boolean isServiceStaff(){
        return role == EmployeeRole.SERVICE;
    }

    public boolean save() {
        int roleNumeric = (this.isCook()) ? 0 : 3;

        String query = "INSERT INTO Employee (tax_id, nominative, contact, address, remaining_holidays, permanent, role) VALUES(?, ?, ?, ?, ?, ?, ?)";

        int res=PersistenceManager.executeUpdate(query,this.taxId, this.nominative, this.contact, this.address, this.remainingHolidays, this.permanent, roleNumeric );
        return res != 0;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

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
}
