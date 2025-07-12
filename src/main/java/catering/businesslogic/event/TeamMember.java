package catering.businesslogic.event;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.user.UserManager;
import catering.persistence.PersistenceManager;

/**
 * Represents a member of a service team in an event.
 * Each team member is associated with a specific service and may have a defined role and notes.
 */
public class TeamMember {

    private int id = 0;
    private int serviceId;
    private String role;
    private String note;
    private String memberTaxId;

    /**
     * Creates a new empty team member slot for a specific role within a service.
     * The member is not assigned yet.
     *
     * @param serviceId the ID of the service the team member belongs to
     * @param role      the role the team member will fulfill
     */
    public TeamMember(int serviceId, String role) {
        this.serviceId = serviceId;
        this.memberTaxId = null;
        this.role = role;
        this.note = null;
    }

    /**
     * Creates a team member with full details, typically used when reconstructing from persistent storage.
     *
     * @param id           the ID of the team member (from database)
     * @param serviceId    the ID of the related service
     * @param role         the role of the team member
     * @param note         any additional notes about the team member
     * @param memberTaxId  the tax ID of the employee assigned to this role
     */
    public TeamMember(int id, int serviceId, String role, String note, String memberTaxId) {
        this.id = id;
        this.serviceId = serviceId;
        this.memberTaxId = memberTaxId;
        this.role = role;
        this.note = note;
    }

    /**
     * Inserts this team member into the database.
     *
     * @return true if the insert was successful, false otherwise
     */
    public boolean insert() {
        String query = "INSERT INTO TeamMember (service_id, role, note, member_tax_id) VALUES (?, ?, ?, ?)";
        int res = PersistenceManager.executeUpdate(query, serviceId, role, note, memberTaxId);
        id = PersistenceManager.getLastId();
        return res != 0;
    }

    /**
     * Updates this team member's details in the database.
     *
     * @return true if the update was successful, false otherwise
     */
    public boolean update() {
        String query = "UPDATE TeamMember SET service_id = ?, role = ?, note = ?, member_tax_id = ? WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, serviceId, role, note, memberTaxId, id);
        return res != 0;
    }

    /**
     * Deletes this team member from the database.
     *
     * @return true if the deletion was successful, false otherwise
     */
    public boolean delete() {
        String query = "DELETE FROM TeamMember WHERE id = ? ";
        int res = PersistenceManager.executeUpdate(query, this.id);
        return res != 0;
    }

    /**
     * Sets the ID of the service this team member is associated with.
     *
     * @param serviceId the service ID to associate
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Sets the role assigned to this team member.
     *
     * @param role the role to assign
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets a note about this team member.
     * Only users with organizer privileges are allowed to perform this action.
     *
     * @param note the note to assign
     * @throws UseCaseLogicException if the current user is not an organizer
     */
    public void setNote(String note) throws UseCaseLogicException {
        if (!UserManager.getInstance().getCurrentUser().isOrganizer())
            throw new UseCaseLogicException("Only organizer can set note");
        this.note = note;
    }

    /**
     * Sets the tax ID of the employee assigned to this team member slot.
     *
     * @param memberTaxId the tax ID of the assigned employee
     */
    public void setMemberTaxId(String memberTaxId) {
        this.memberTaxId = memberTaxId;
    }

    /**
     * Gets the ID of the service this team member belongs to.
     *
     * @return the service ID
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Gets the role assigned to this team member.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the note associated with this team member.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Gets the tax ID of the assigned employee.
     *
     * @return the employee's tax ID
     */
    public String getMemberTaxId() {
        return memberTaxId;
    }

    /**
     * Returns a string representation of this team member.
     * If the member is assigned, their tax ID and role are shown.
     * Otherwise, it indicates an empty slot for the role.
     *
     * @return string description of the team member
     */
    @Override
    public String toString() {
        if (memberTaxId != null)
            return memberTaxId + " with " + role + " role";
        return "empty slot for " + role + " role";
    }
}
