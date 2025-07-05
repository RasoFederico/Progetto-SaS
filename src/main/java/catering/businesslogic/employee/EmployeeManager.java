package catering.businesslogic.employee;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static Employee getEmployee(String taxId) {
        String query = "SELECT * FROM Employees WHERE tax_id = ?";
        Employee e= new Employee();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                e.setTaxId(rs.getString("tax_id"));
                e.setNominative(rs.getString("nominative"));
                e.setContact(rs.getString("contact"));
                e.setAddress(rs.getString("address"));
                e.setPermanent(rs.getBoolean("permanent"));
                e.setRemainingHolidays(rs.getInt("remaining_holidays"));
                e.setRole(Employee.EmployeeRole.values()[rs.getInt("role")]);

                int role = (rs.getInt("role")) == 0 ? 0:1;
                e.setRole(Employee.EmployeeRole.values()[role]);
            }
        }, taxId);
        return e;
    }
}
