package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalReservasiDAO {
    private final Connection conn;

    public JadwalReservasiDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Date tanggal, Time jamMulai, Time jamSelesai) throws SQLException {
        String sql = "INSERT INTO jadwal_reservasi (tanggal, jam_mulai, jam_selesai) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, tanggal);
            stmt.setTime(2, jamMulai);
            stmt.setTime(3, jamSelesai);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // return id_jadwal
            }
        }
        return -1;
    }

    public List<JadwalReservasi> getAll() throws SQLException {
        List<JadwalReservasi> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal_reservasi ORDER BY tanggal, jam_mulai";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new JadwalReservasi(
                        rs.getInt("id_jadwal"),
                        rs.getDate("tanggal"),
                        rs.getTime("jam_mulai"),
                        rs.getTime("jam_selesai")
                ));
            }
        }
        return list;
    }

    public JadwalReservasi getById(int id) throws SQLException {
        String sql = "SELECT * FROM jadwal_reservasi WHERE id_jadwal = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new JadwalReservasi(
                            rs.getInt("id_jadwal"),
                            rs.getDate("tanggal"),
                            rs.getTime("jam_mulai"),
                            rs.getTime("jam_selesai")
                    );
                }
            }
        }
        return null;
    }
}
