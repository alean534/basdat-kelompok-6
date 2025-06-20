package reservasi_foosen;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservasiDAO {

    private final Connection conn;

    public ReservasiDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Reservasi reservasi) {
        String sql = "INSERT INTO reservasi (id_pelanggan, id_jadwal, catatan_reservasi, status_reservasi) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reservasi.getIdPelanggan());
            stmt.setInt(2, reservasi.getIdJadwal());
            stmt.setString(3, reservasi.getCatatanReservasi());
            stmt.setString(4, reservasi.getStatusReservasi());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    reservasi.setIdReservasi(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // gagal
    }

    public boolean updateStatusReservasi(int idReservasi, String statusBaru) {
        String sql = "UPDATE reservasi SET status_reservasi = ? WHERE id_reservasi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusBaru);
            stmt.setInt(2, idReservasi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Reservasi getByMeja(int idMeja) {
        String sql = "SELECT r.*, j.tanggal, j.jam_mulai "
                + "FROM reservasi r "
                + "JOIN reservasi_meja rm ON r.id_reservasi = rm.id_reservasi "
                + "JOIN jadwal_reservasi j ON r.id_jadwal = j.id_jadwal "
                + "WHERE rm.id_meja = ? ORDER BY r.id_reservasi DESC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMeja);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reservasi(
                        rs.getInt("id_reservasi"),
                        rs.getInt("id_pelanggan"),
                        rs.getInt("id_jadwal"),
                        rs.getString("status_reservasi"),
                        rs.getString("catatan_reservasi")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reservasi getById(int idReservasi) {
        String sql = "SELECT * FROM reservasi WHERE id_reservasi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReservasi);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reservasi(
                        rs.getInt("id_reservasi"),
                        rs.getInt("id_pelanggan"),
                        rs.getInt("id_jadwal"),
                        rs.getString("status_reservasi"),
                        rs.getString("catatan_reservasi")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservasi> getAllReservasi() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.id_reservasi, r.id_pelanggan, r.id_jadwal, r.catatan_reservasi, r.status_reservasi, "
                + "j.tanggal, j.jam_mulai "
                + "FROM reservasi r "
                + "JOIN jadwal_reservasi j ON r.id_jadwal = j.id_jadwal";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Reservasi(
                        rs.getInt("id_reservasi"),
                        rs.getInt("id_pelanggan"),
                        rs.getInt("id_jadwal"),
                        rs.getString("status_reservasi"),
                        rs.getString("catatan_reservasi")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void konfirmasiKedatangan(int idReservasi) {
        String sql = "{CALL konfirmasi_kedatangan(?)}";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, idReservasi);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
