package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MejaDAO {

    private Connection conn;

    public MejaDAO(Connection conn) {
        this.conn = conn;
    }

    // Ambil semua meja
    public List<Meja> getAllMeja() throws SQLException {
        List<Meja> listMeja = new ArrayList<>();
        String sql = "SELECT id_meja, nomor_meja, status, kapasitas, area FROM meja ORDER BY nomor_meja";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id_meja = rs.getInt("id_meja");
                String nomor_meja = rs.getString("nomor_meja");
                String status = rs.getString("status");
                int kapasitas = rs.getInt("kapasitas");
                String area = rs.getString("area");

                Meja meja = new Meja(id_meja, nomor_meja, status, kapasitas, area);
                listMeja.add(meja);
            }
        }

        return listMeja;
    }

    // Tambah meja baru (mejaId auto increment)
    public boolean tambahMeja(String nomor_meja, String status, int kapasitas, String area) throws SQLException {
        String sql = "INSERT INTO meja (nomor_meja, status, kapasitas, area) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomor_meja);
            ps.setString(2, status);
            ps.setInt(3, kapasitas);
            ps.setString(4, area);

            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public Meja getById(int id) {
        String sql = "SELECT * FROM meja WHERE id_meja = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Meja(
                        rs.getInt("id_meja"),
                        rs.getString("nomor_meja"),
                        rs.getString("status"),
                        rs.getInt("kapasitas"),
                        rs.getString("area")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Hapus meja berdasarkan mejaId
    public boolean hapusMeja(int id_meja) throws SQLException {
        String sql = "DELETE FROM meja WHERE id_meja = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_meja);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public List<Meja> getMejaSesuaiJumlah(int jumlahOrang) {
        List<Meja> daftarMeja = new ArrayList<>();
        String sql = "{CALL get_meja_sesuai_jumlah(?)}";

        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jumlahOrang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Meja meja = new Meja();
                meja.setIdMeja(rs.getInt("id_meja"));
                meja.setNomorMeja(rs.getString("nomor_meja"));
                meja.setKapasitas(rs.getInt("kapasitas"));
                meja.setArea(rs.getString("area"));
                meja.setStatus(rs.getString("status"));
                daftarMeja.add(meja);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return daftarMeja;
    }

    public boolean updateStatusMeja(int idMeja, String status) {
    String sql = "UPDATE meja SET status = ? WHERE id_meja = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, status);
        stmt.setInt(2, idMeja);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


}
