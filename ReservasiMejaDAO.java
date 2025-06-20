package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservasiMejaDAO {

    private final Connection conn;

    public ReservasiMejaDAO(Connection conn) {
        this.conn = conn;
    }

    public void tambahReservasiMeja(ReservasiMeja reservasiMeja) {
        String sql = "INSERT INTO reservasi_meja (id_reservasi, id_meja) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reservasiMeja.getIdReservasi());
            stmt.setInt(2, reservasiMeja.getIdMeja());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                reservasiMeja.setIdReservasiMeja(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ReservasiMeja> getReservasiMejaByReservasiId(int idReservasi) {
        List<ReservasiMeja> list = new ArrayList<>();
        String sql = "SELECT * FROM reservasi_meja WHERE id_reservasi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReservasi);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReservasiMeja(
                        rs.getInt("id_reservasiMeja"),
                        rs.getInt("id_reservasi"),
                        rs.getInt("id_meja")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
