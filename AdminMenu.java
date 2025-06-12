package reservasi_foosen;

import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminMenu {

    private static UserDAO userDAO;
    private static MenuDAO menuDAO;
    private static ReservasiDAO reservasiDAO;
    private static Connection conn;

    public static void showAdminMenu(Scanner scanner, Connection conn) throws SQLException {
        AdminMenu.conn = conn; // ✅ betulkan penetapan koneksi
        reservasiDAO = new ReservasiDAO(conn);
        userDAO = new UserDAO(conn);
        menuDAO = new MenuDAO(conn);

        while (true) {
            System.out.println("\n===============================================");
            System.out.println("                MENU ADMIN");
            System.out.println("===============================================");
            System.out.println("1. KELOLA USER");
            System.out.println("2. KELOLA MEJA");
            System.out.println("3. KELOLA MENU");
            System.out.println("4. LIHAT RESERVASI");
            System.out.println("5. KELUAR");
            System.out.println("-----------------------------------------------");
            System.out.print("PILIH MENU : ");
            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1":
                    kelolaUser(scanner);
                    break;
                case "2":
                    kelolaMeja(scanner, conn);
                    break;
                case "3":
                    kelolaMenu(scanner);
                    break;
                case "4":
                    lihatReservasi(scanner, conn);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Menu belum tersedia atau tidak valid.");
            }
        }
    }

    private static void kelolaUser(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n-----------------------------------------------");
            System.out.println("                KELOLA USER");
            System.out.println("-----------------------------------------------");
            System.out.println("1. TAMBAH USER");
            System.out.println("2. HAPUS USER");
            System.out.println("3. DAFTAR USER");
            System.out.println("4. KEMBALI KE MENU ADMIN");
            System.out.println("-----------------------------------------------");
            System.out.print("PILIH : ");
            String pilih = scanner.nextLine();

            switch (pilih) {
                case "1":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                TAMBAH USER");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Masukkan username              : ");
                    String username = scanner.nextLine();
                    System.out.print("Masukkan password              : ");
                    String password = scanner.nextLine();
                    System.out.print("Masukkan role (admin/kasir) : ");
                    String role = scanner.nextLine();
                    System.out.println("-----------------------------------------------");

                    boolean success = userDAO.tambahUser(username, password, role);
                    System.out.println(success
                            ? "User berhasil ditambahkan!"
                            : "Gagal menambahkan user. Role mungkin tidak valid.");
                    break;

                case "2":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                HAPUS USER");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Masukkan username yang ingin dihapus : ");
                    String usernameHapus = scanner.nextLine();

                    boolean hapusBerhasil = userDAO.hapusUser(usernameHapus);
                    System.out.println(hapusBerhasil
                            ? "User berhasil dihapus!"
                            : "User tidak ditemukan atau gagal dihapus.");
                    break;

                case "3":
                    List<User> daftarUser = userDAO.getAllUsers();
                    if (daftarUser.isEmpty()) {
                        System.out.println("Belum ada user yang terdaftar.");
                    } else {
                        System.out.println("------------------------------");
                        System.out.println("         DAFTAR USER");
                        System.out.println("------------------------------");
                        System.out.printf("%-20s %-10s\n", "Username", "Role");
                        System.out.println("------------------------------");
                        for (User user : daftarUser) {
                            System.out.printf("%-20s %-10s\n", user.getUsername(), user.getRole());
                        }
                    }
                    break;
                case "4":
                    return;

                default:
                    System.out.println("Aksi tidak valid.");
            }
        }
    }

    private static void kelolaMeja(Scanner scanner, Connection conn) throws SQLException {
        MejaDAO mejaDAO = new MejaDAO(conn);

        while (true) {
            System.out.println("\n-----------------------------------------------");
            System.out.println("                KELOLA MEJA");
            System.out.println("-----------------------------------------------");
            System.out.println("1. DAFTAR MEJA");
            System.out.println("2. TAMBAH MEJA");
            System.out.println("3. HAPUS MEJA");
            System.out.println("4. KEMBALI KE MENU ADMIN");
            System.out.println("-----------------------------------------------");
            System.out.print("PILIH : ");
            String pilih = scanner.nextLine();

            switch (pilih) {
                case "1":
                    System.out.println("\n---------------------------------------------------------------");
                    System.out.println("                         DAFTAR MEJA");
                    System.out.println("---------------------------------------------------------------");
                    System.out.printf("%-6s %-12s %-12s %-12s %-15s\n",
                            "ID", "No.Meja", "Lokasi", "Status", "Kapasitas");
                    System.out.println("---------------------------------------------------------------");
                    for (Meja meja : mejaDAO.getAllMeja()) {
                        System.out.printf("%-6d %-12s %-12s %-12s %-15d\n",
                                meja.getIdMeja(), // int -> %d
                                meja.getNomorMeja(), // String -> %s
                                meja.getArea(), // String -> %s
                                meja.getStatus(), // String -> %s
                                meja.getKapasitas());    // int -> %d

                    }
                    break;

                case "2":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                TAMBAH MEJA");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Masukkan nomor meja baru    : ");
                    String nomor_meja = scanner.nextLine();

                    int kapasitas = 0;
                    while (true) {
                        System.out.print("Masukkan kapasitas meja     : ");
                        try {
                            kapasitas = Integer.parseInt(scanner.nextLine());
                            if (kapasitas > 0) {
                                break;
                            } else {
                                System.out.println("Kapasitas harus lebih dari 0.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Input tidak valid. Masukkan angka.");
                        }
                    }

                    System.out.print("Masukkan lokasi meja        : ");
                    String lokasi = scanner.nextLine();

                    boolean sukses = mejaDAO.tambahMeja(nomor_meja, "tersedia", kapasitas, lokasi);
                    System.out.println(sukses
                            ? "Meja berhasil ditambahkan."
                            : "Gagal menambahkan meja. Nomor meja mungkin sudah ada.");
                    break;

                case "3":
                    int id_meja = 0;
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                HAPUS MEJA");
                    System.out.println("-----------------------------------------------");
                    while (true) {
                        System.out.print("Masukkan ID meja yang ingin dihapus : ");
                        try {
                            id_meja = Integer.parseInt(scanner.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Input tidak valid. Masukkan angka.");
                        }
                    }

                    boolean hapus = mejaDAO.hapusMeja(id_meja);
                    System.out.println(hapus
                            ? "Meja berhasil dihapus."
                            : "Meja tidak ditemukan atau gagal dihapus.");
                    break;

                case "4":
                    return;

                default:
                    System.out.println("Aksi tidak valid.");
            }
        }
    }

    private static void kelolaMenu(Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\n-----------------------------------------------");
            System.out.println("                KELOLA MENU");
            System.out.println("-----------------------------------------------");
            System.out.println("1. TAMBAH MENU");
            System.out.println("2. HAPUS MENU");
            System.out.println("3. EDIT HARGA");
            System.out.println("4. DAFTAR MENU");
            System.out.println("5. KEMBALI KE MENU ADMIN");
            System.out.println("-----------------------------------------------");
            System.out.print("PILIH : ");
            String pilih = scanner.nextLine();

            switch (pilih) {
                case "1":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                TAMBAH MENU");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Nama menu      : ");
                    String nama_menu = scanner.nextLine();
                    System.out.print("Harga          : ");
                    double harga = Double.parseDouble(scanner.nextLine());
                    System.out.print("Kategori(Makanan/Minuman/Camilan)  : ");
                    String kategori = scanner.nextLine();
                    System.out.println(menuDAO.tambahMenu(nama_menu, harga, kategori)
                            ? "Menu berhasil ditambahkan."
                            : "Gagal menambahkan menu.");
                    break;

                case "2":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                HAPUS MENU");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Masukkan kategori (Makanan/Minuman/Camilan) : ");
                    String kategoriHapus = scanner.nextLine();
                    System.out.println("\nDAFTAR MENU KATEGORI: " + kategoriHapus.toUpperCase());
                    System.out.printf("%-5s %-20s %-10s\n", "ID", "Nama Menu", "Harga");
                    System.out.println("-----------------------------------------------");
                    for (Menu m : menuDAO.getMenuByKategori(kategoriHapus)) {
                        System.out.printf("%-5d %-20s %-10.2f\n", m.getIdMenu(), m.getNama(), m.getHarga());
                    }
                    System.out.print("Masukkan ID menu yang akan dihapus : ");
                    int id_menuHapus = Integer.parseInt(scanner.nextLine());
                    System.out.println(menuDAO.hapusMenu(id_menuHapus)
                            ? "Menu berhasil dihapus."
                            : "Gagal menghapus menu.");
                    break;

                case "3":
                    System.out.println("\n-----------------------------------------------");
                    System.out.println("                EDIT MENU");
                    System.out.println("-----------------------------------------------");
                    System.out.print("Masukkan kategori(Makanan/Minuman/Camilan) : ");
                    String kategoriEdit = scanner.nextLine();
                    System.out.println("-----------------------------------------------");
                    System.out.println("\n DAFTAR MENU KATEGORI: " + kategoriEdit.toUpperCase());
                    System.out.printf("%-5s %-20s %-10s\n", "ID", "Nama Menu", "Harga");
                    System.out.println("-----------------------------------------------");
                    for (Menu m : menuDAO.getMenuByKategori(kategoriEdit)) {
                        System.out.printf("%-5d %-20s %-10.2f\n", m.getIdMenu(), m.getNama(), m.getHarga());
                    }
                    System.out.print("ID Menu yang ingin diedit : ");
                    int id_menuEdit = Integer.parseInt(scanner.nextLine());
                    System.out.print("Harga baru               : ");
                    double hargaBaru = Double.parseDouble(scanner.nextLine());
                    System.out.println(menuDAO.updateHargaMenu(id_menuEdit, hargaBaru)
                            ? "Harga berhasil diupdate."
                            : "Gagal update harga.");
                    break;

                case "4":
                    System.out.println("\n----------------------------------------------------------");
                    System.out.println("                       DAFTAR MENU");
                    System.out.println("----------------------------------------------------------");
                    System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Nama Menu", "Harga", "Kategori");
                    System.out.println("----------------------------------------------------------");
                    for (Menu m : menuDAO.getAllMenu()) {
                        System.out.printf("%-5d %-20s %-10.2f %-10s\n",
                                m.getIdMenu(), m.getNama(), m.getHarga(), m.getKategori());
                    }
                    break;

                case "5":
                    return;

                default:
                    System.out.println("Aksi tidak valid.");
            }
        }
    }

    private static void lihatReservasi(Scanner scanner, Connection conn) throws SQLException {
        String sql = """
        SELECT
            r.id_reservasi,
            p.nama AS nama_pelanggan,
            p.telepon AS no_telp, 
            r.created_at AS tanggal_reservasi_dibuat,
            r.status_reservasi,
            m.nomor_meja,
            bayar.total_tagihan,
            bayar.metode,
            bayar.tanggal_bayar AS waktu_kedatangan
        FROM reservasi r
        JOIN pelanggan p ON r.id_pelanggan = p.id_pelanggan
        JOIN reservasi_meja rm ON r.id_reservasi = rm.id_reservasi
        JOIN meja m ON rm.id_meja = m.id_meja
        LEFT JOIN pembayaran bayar ON r.id_reservasi = bayar.id_reservasi
        ORDER BY r.created_at DESC;
        """;

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n-------------------------------------------------------------------------------------------------------------------------");
            System.out.println("                                 DAFTAR RESERVASI");
            System.out.println("------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s %-20s %-13s %-20s %-12s %-8s %-12s %-10s %-20s\n",
                    "ID", "Nama Pelanggan", "No. Telp", "Tgl Dibuat", "Status", "Meja", "Total", "Metode", "Kedatangan");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-13s %-20s %-12s %-8s Rp%-10.0f %-10s %-20s\n",
                        rs.getInt("id_reservasi"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("no_telp"),
                        rs.getTimestamp("tanggal_reservasi_dibuat"),
                        rs.getString("status_reservasi"),
                        rs.getString("nomor_meja"),
                        rs.getDouble("total_tagihan"),
                        rs.getString("metode"),
                        rs.getTimestamp("waktu_kedatangan"));
            }
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan reservasi: " + e.getMessage());
        }
    }

}
