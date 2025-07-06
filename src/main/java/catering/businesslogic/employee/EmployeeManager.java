package catering.businesslogic.employee;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.user.UserManager;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    public boolean addEmployee(String nominative, String contact, String address, String taxId,int remainingHolidays, Employee.EmployeeRole role) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOwner())
            throw new UseCaseLogicException("Only owner can add a mew employee");
        Employee employee = new Employee(nominative, contact, address, taxId, remainingHolidays, role);
        return employee.save();
    }

    public boolean deleteEmployee(Employee employee) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can delete employee");
        return employee.delete();
    }

    public boolean updateEmployee(Employee updatedEmployee) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can update employee's data");
        String query = "UPDATE Employees SET nominative = ?, contact = ?, address = ?, remaining_holidays = ?, role = ? WHERE tax_id = ? ";
        int res = PersistenceManager.executeUpdate(query, updatedEmployee.getNominative(), updatedEmployee.getContact(), updatedEmployee.getAddress(), updatedEmployee.getRemainingHolidays(),(updatedEmployee.isCook()?0:3), updatedEmployee.getTaxId());
        return res!=0;
    }

    public  boolean promoteEmployee(Employee employee) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only owner can promote employees");
        String query = "UPDATE Employees SET permanent = ? WHERE tax_id = ? ";
        int res = PersistenceManager.executeUpdate(query, true, employee.getTaxId());
        return res!=0;
    }

    public List<Employee> getEmployees(boolean... permanent) throws UseCaseLogicException {
        if(!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can get employees list");
        ArrayList<Employee> list = new ArrayList<>();

        if(permanent.length == 0){
            String query = "SELECT * FROM Employees";
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

        if(permanent[0]){
            String query = "SELECT * FROM Employees WHERE permanent = 1";
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    Employee e = new Employee();
                    assignValues(rs, e);
                    list.add(e);
                }
            });
            return list;
        }else {
            String query = "SELECT * FROM Employees WHERE permanent = 0";
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


    }

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

    private static void assignValues(ResultSet rs, Employee e) throws SQLException {
        e.setTaxId(rs.getString("tax_id"));
        e.setNominative(rs.getString("nominative"));
        e.setContact(rs.getString("contact"));
        e.setAddress(rs.getString("address"));
        e.setPermanent(rs.getBoolean("permanent"));
        e.setRemainingHolidays(rs.getInt("remaining_holidays"));
        int role = (rs.getInt("role")) == 0 ? 0:1;
        e.setRole(Employee.EmployeeRole.values()[role]);
    }
}
