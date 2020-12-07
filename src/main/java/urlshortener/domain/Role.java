package urlshortener.domain;

public class Role {
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_USER = 1;
    private int id;
    private String roleName;

    public Role(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;

    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleById(int id) {
        if(id == ROLE_ADMIN) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }
}
