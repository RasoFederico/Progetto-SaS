package catering.businesslogic.event;

import catering.businesslogic.employee.Employee;
import catering.persistence.PersistenceManager;

public class TeamMember {
    private String role;
    private String note;
    private Employee member;

    public TeamMember(String role) {
        this.member = null;
        this.role = role;
        this.note = null;
    }

    public boolean save(){

        String query = "INSERT INTO team_member (role, note, member) VALUES (?, ?, ?)";

        int res = PersistenceManager.executeUpdate(query, role, note, member.getTaxId());

        return res!=0;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setMember(Employee member) {
        this.member = member;
    }

    public String getRole() {
        return role;
    }

    public String getNote() {
        return note;
    }

    public Employee getMember() {
        return member;
    }
}
