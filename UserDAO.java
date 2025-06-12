package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.id_user, u.username, u.password, r.nama_role " +
                     "FROM user u " +
                     "JOIN user_role ur ON u.id_user = ur.id_user " +
                     "JOIN role r ON ur.id_role = r.id_role " +
                     "WHERE u.username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (dbPassword.equals(password)) {
                    int id = rs.getInt("id_user");
                    String role = rs.getString("nama_role");
                    return new User(id, username, password, role);
                }
            }
        }
        return null;
    }

    public boolean tambahUser(String username, String password, String role) throws SQLException {
        // Cek ID role
        String getRoleIdSQL = "SELECT id_role FROM role WHERE nama_role = ?";
        PreparedStatement getRoleStmt = conn.prepareStatement(getRoleIdSQL);
        getRoleStmt.setString(1, role);
        ResultSet rs = getRoleStmt.executeQuery();

        if (!rs.next()) return false; // role tidak valid

        int idRole = rs.getInt("id_role");

        // Tambah user
        String insertUserSQL = "INSERT INTO user (username, password) VALUES (?, ?)";
        PreparedStatement insertUserStmt = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
        insertUserStmt.setString(1, username);
        insertUserStmt.setString(2, password);
        insertUserStmt.executeUpdate();

        ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int idUser = generatedKeys.getInt(1);

            // Tambah ke user_role
            String insertUserRoleSQL = "INSERT INTO user_role (id_user, id_role) VALUES (?, ?)";
            PreparedStatement insertUserRoleStmt = conn.prepareStatement(insertUserRoleSQL);
            insertUserRoleStmt.setInt(1, idUser);
            insertUserRoleStmt.setInt(2, idRole);
            insertUserRoleStmt.executeUpdate();
            return true;
        }
        return false;
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> daftarUser = new ArrayList<>();

        String sql = "SELECT u.id_user, u.username, r.nama_role " +
                     "FROM user u " +
                     "JOIN user_role ur ON u.id_user = ur.id_user " +
                     "JOIN role r ON ur.id_role = r.id_role " +
                     "ORDER BY u.username";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idUser = rs.getInt("id_user");
                String username = rs.getString("username");
                String role = rs.getString("nama_role");

                // Password tidak diambil untuk keamanan, bisa diisi null atau kosong
                daftarUser.add(new User(idUser, username, "", role));
            }
        }
        return daftarUser;
    }

    public boolean hapusUser(String username) throws SQLException {
        // Cari id_user
        String getUserSQL = "SELECT id_user FROM user WHERE username = ?";
        PreparedStatement getUserStmt = conn.prepareStatement(getUserSQL);
        getUserStmt.setString(1, username);
        ResultSet rs = getUserStmt.executeQuery();

        if (rs.next()) {
            int idUser = rs.getInt("id_user");

            // Hapus dari user_role
            String deleteUserRoleSQL = "DELETE FROM user_role WHERE id_user = ?";
            PreparedStatement deleteUserRoleStmt = conn.prepareStatement(deleteUserRoleSQL);
            deleteUserRoleStmt.setInt(1, idUser);
            deleteUserRoleStmt.executeUpdate();

            // Hapus dari user
            String deleteUserSQL = "DELETE FROM user WHERE id_user = ?";
            PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSQL);
            deleteUserStmt.setInt(1, idUser);
            deleteUserStmt.executeUpdate();

            return true;
        }
        return false;
    }
}
