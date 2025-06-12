package reservasi_foosen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    private Connection conn;

    public MenuDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean tambahMenu(String nama_menu, double harga, String kategori) {
        String sql = "INSERT INTO menu (nama_menu, harga, kategori) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama_menu);
            stmt.setDouble(2, harga);
            stmt.setString(3, kategori);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error tambah menu: " + e.getMessage());
            return false;
        }
    }

    public boolean hapusMenu(int id_menu) {
        String sql = "DELETE FROM menu WHERE id_menu = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_menu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error hapus menu: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHargaMenu(int id_menu, double hargaBaru) {
        String sql = "UPDATE menu SET harga = ? WHERE id_menu = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, hargaBaru);
            stmt.setInt(2, id_menu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error update harga: " + e.getMessage());
            return false;
        }
    }

    public List<Menu> getAllMenu() {
        List<Menu> daftarMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Menu m = new Menu(
                    rs.getInt("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getDouble("harga"),
                    rs.getString("kategori")
                );
                daftarMenu.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error ambil menu: " + e.getMessage());
        }
        return daftarMenu;
    }

    public List<Menu> getMenuByKategori(String kategori) {
        List<Menu> daftarMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE kategori = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kategori);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Menu m = new Menu(
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori")
                    );
                    daftarMenu.add(m);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error filter kategori: " + e.getMessage());
        }
        return daftarMenu;
    }
    
    public Menu getById(int id_menu) {
        Menu menu = null;
        String sql = "SELECT * FROM menu WHERE id_menu = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_menu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Menu m = new Menu(
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menu;
    }

}
