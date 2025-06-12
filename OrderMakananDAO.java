package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderMakananDAO {
    private final Connection conn;

    public OrderMakananDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(OrderMakanan order) {
        String sql = "INSERT INTO order_makanan (id_reservasi, id_menu, jumlah, catatan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getIdReservasi());
            stmt.setInt(2, order.getIdMenu());
            stmt.setInt(3, order.getJumlah());
            stmt.setString(4, order.getCatatan());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertAll(Map<Menu, Integer> pesanan, int idReservasi) {
        for (Map.Entry<Menu, Integer> entry : pesanan.entrySet()) {
            Menu menu = entry.getKey();
            int jumlah = entry.getValue();
            insert(new OrderMakanan(idReservasi, menu.getIdMenu(), jumlah, ""));
        }
    }

    public List<OrderMakanan> getByReservasi(int idReservasi) {
        List<OrderMakanan> list = new ArrayList<>();
        String sql = """
                SELECT om.id_order, om.id_reservasi, om.id_menu, om.jumlah, om.catatan,
                       m.nama_menu, m.harga, m.kategori
                FROM order_makanan om
                JOIN menu m ON om.id_menu = m.id_menu
                WHERE om.id_reservasi = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReservasi);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Menu menu = new Menu(
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori")
                );
                OrderMakanan order = new OrderMakanan(
                        rs.getInt("id_order"),
                        rs.getInt("id_reservasi"),
                        rs.getInt("id_menu"),
                        rs.getInt("jumlah"),
                        rs.getString("catatan"),
                        menu
                );
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateTotalTagihan(int idReservasi) {
        String sql = "{CALL update_total_tagihan(?)}";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, idReservasi);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
