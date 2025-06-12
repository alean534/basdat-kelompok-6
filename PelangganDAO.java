package reservasi_foosen;

import java.sql.*;

public class PelangganDAO {
    private Connection conn;

    public PelangganDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Pelanggan p) throws SQLException {
        String sql = "INSERT INTO pelanggan (nama, telepon) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNama());
            stmt.setString(2, p.getNoTlp());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public Pelanggan getById(int id) {
        String sql = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Pelanggan(
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama"),
                    rs.getString("telepon")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

