package urlshortener.domain;

public class Role {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;
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
}
