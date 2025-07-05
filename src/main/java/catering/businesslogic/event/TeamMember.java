package catering.businesslogic.event;

import catering.businesslogic.employee.Employee;
import catering.persistence.PersistenceManager;

public class TeamMember {
    private int id = 0;
    private int serviceId;
    private String role;
    private String note;
    private String memberTaxId;

    public TeamMember(int serviceId, String role) {
        this.serviceId = serviceId;
        this.memberTaxId = null;
        this.role = role;
        this.note = null;
    }

    public TeamMember(int id, int serviceId, String role, String note, String memberTaxId) {
        this.id = id;
        this.serviceId = serviceId;
        this.memberTaxId = memberTaxId;
        this.role = role;
        this.note = note;
    }

    public boolean insert(){
        String query = "INSERT INTO TeamMember (service_id, role, note, member_tax_id) VALUES (?, ?, ?, ?)";
        int res = PersistenceManager.executeUpdate(query, serviceId, role, note, memberTaxId);
        id = PersistenceManager.getLastId();
        return res!=0;
    }

    public boolean update() {
        String query = "UPDATE TeamMember SET service_id = ?, role = ?, note = ?, member_tax_id = ? WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, serviceId, role, note, memberTaxId, id);
        return res!=0;
    }

    public boolean delete(){
        String query = "DELETE FROM TeamMember WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, this.id);
        return res!=0;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setMemberTaxId(String memberTaxId) {
        this.memberTaxId = memberTaxId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getRole() {
        return role;
    }

    public String getNote() {
        return note;
    }

    public String getMemberTaxId() {
        return memberTaxId;
    }
}
