package reservasi_foosen;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PembayaranDAO {
    private final Connection conn;

    public PembayaranDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(Pembayaran pembayaran) {
        String sql = "INSERT INTO pembayaran (id_reservasi, metode, total_tagihan, tanggal_bayar, status_bayar) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pembayaran.getIdReservasi());
            stmt.setString(2, pembayaran.getMetode());
            stmt.setDouble(3, pembayaran.getTotalTagihan());
            stmt.setTimestamp(4, Timestamp.valueOf(pembayaran.getTanggalBayar()));
            stmt.setString(5, pembayaran.getStatusBayar());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Pembayaran getByReservasi(int idReservasi) {
        String sql = "SELECT * FROM pembayaran WHERE id_reservasi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReservasi);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Pembayaran(
                        rs.getInt("id_reservasi"),
                        rs.getString("metode"),
                        rs.getDouble("total_tagihan"),
                        rs.getTimestamp("tanggal_bayar").toLocalDateTime(),
                        rs.getString("status_bayar")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Pembayaran> getAll() {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT * FROM pembayaran";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pembayaran(
                        rs.getInt("id_reservasi"),
                        rs.getString("metode"),
                        rs.getDouble("total_tagihan"),
                        rs.getTimestamp("tanggal_bayar").toLocalDateTime(),
                        rs.getString("status_bayar")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
