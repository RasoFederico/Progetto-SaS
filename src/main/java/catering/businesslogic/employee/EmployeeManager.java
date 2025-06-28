package catering.businesslogic.employee;

import catering.persistence.PersistenceManager;

public class EmployeeManager {
    public boolean addEmployee(String nominative, String contact, String address, String taxId, Employee.EmployeeRole role) {
        Employee employee = new Employee(nominative, contact, address, taxId, 0, role);
        return employee.save();
    }

    public boolean deleteEmployee(Employee employee) {
        String query = "DELETE FROM Employee WHERE tax_id = ?";
        int res = PersistenceManager.executeUpdate(query, employee.getTaxId());
        return res!=0;
    }

    public boolean updateEmployee(Employee updatedEmployee) {
        String query = "UPDATE Employee SET nominative = ?, contact = ?, address = ?, remaining_holidays = ?, role = ? WHERE tax_id = ? ";
        int res = PersistenceManager.executeUpdate(query, updatedEmployee.getNominative(), updatedEmployee.getContact(), updatedEmployee.getAddress(), (updatedEmployee.isCook()?0:3), updatedEmployee.getTaxId());
        return res!=0;
    }

    public  boolean promoteEmployee(Employee employee) {
        String query = "UPDATE Employee SET permanent = ? WHERE tax_id = ? ";
        int res = PersistenceManager.executeUpdate(query, true, employee.getTaxId());
        return res!=0;
    }
}
