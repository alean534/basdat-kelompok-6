package reservasi_foosen;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KasirMenu {

    private static MenuDAO menuDAO;
    private static MejaDAO mejaDAO;
    private static ReservasiDAO reservasiDAO;
    private static OrderMakananDAO orderDAO;
    private static PembayaranDAO pembayaranDAO;
    private static Object reservasiMejaDAO;

    public static void showKasirMenu(Scanner scanner, Connection conn) throws SQLException {
        menuDAO = new MenuDAO(conn);
        mejaDAO = new MejaDAO(conn);
        reservasiDAO = new ReservasiDAO(conn);
        orderDAO = new OrderMakananDAO(conn);
        pembayaranDAO = new PembayaranDAO(conn);

        while (true) {
            System.out.println("\n===== MENU KASIR =====");
            System.out.println("1. Buat Reservasi");
            System.out.println("2. Konfirmasi Kedatangan");
            System.out.println("3. Input Order Menu");
            System.out.println("4. Pembayaran");
            System.out.println("5. Keluar");
            System.out.print("Pilih Menu: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" ->
                    buatReservasi(scanner, conn);
                case "2" ->
                    konfirmasiKedatangan(scanner, conn);
                case "3" ->
                    inputOrderMenu(scanner);
                case "4" ->
                    pembayaran(scanner);
                case "5" -> {
                    System.out.println("Keluar dari sistem kasir.");
                    return;
                }
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void buatReservasi(Scanner scanner, Connection conn) throws SQLException {
        PelangganDAO pelangganDAO = new PelangganDAO(conn);
        JadwalReservasiDAO jadwalDAO = new JadwalReservasiDAO(conn);
        ReservasiMejaDAO reservasiMejaDAO = new ReservasiMejaDAO(conn);

        System.out.print("Nama Pelanggan: ");
        String nama = scanner.nextLine();
        System.out.print("No Telepon    : ");
        String telepon = scanner.nextLine();
        System.out.print("Jumlah Orang  : ");
        int jumlah = Integer.parseInt(scanner.nextLine());

        System.out.print("Tanggal (yyyy-mm-dd): ");
        String tanggalStr = scanner.nextLine();
        System.out.print("Jam Mulai (HH:mm)   : ");
        String jamMulaiStr = scanner.nextLine();
        System.out.print("Jam Selesai (HH:mm) : ");
        String jamSelesaiStr = scanner.nextLine();
        System.out.print("Status (terjadwal): ");
        String status = scanner.nextLine();
        System.out.print("Catatan Reservasi: ");
        String catatan = scanner.nextLine();

        java.sql.Date tanggal = java.sql.Date.valueOf(tanggalStr);
        java.sql.Time jamMulai = java.sql.Time.valueOf(jamMulaiStr + ":00");
        java.sql.Time jamSelesai = java.sql.Time.valueOf(jamSelesaiStr + ":00");

        List<Meja> daftarMeja = mejaDAO.getMejaSesuaiJumlah(jumlah);
        if (daftarMeja.isEmpty()) {
            System.out.println("Tidak ada meja yang sesuai.");
            return;
        }

        System.out.println("\n--- Pilihan Meja Tersedia ---");
        for (Meja meja : daftarMeja) {
            System.out.printf("ID: %d | No: %s | Kapasitas: %d | Area: %s\n",
                    meja.getIdMeja(), meja.getNomorMeja(), meja.getKapasitas(), meja.getArea());
        }
        System.out.print("Pilih ID Meja: ");
        int idMeja = Integer.parseInt(scanner.nextLine());

        int idPelanggan = pelangganDAO.insert(new Pelanggan(nama, telepon));
        int idJadwal = jadwalDAO.insert(tanggal, jamMulai, jamSelesai);
        LocalDateTime tglReservasi = LocalDateTime.parse(tanggalStr + " " + jamMulaiStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Reservasi reservasi = new Reservasi(idPelanggan, idJadwal, tglReservasi, status, catatan);
        int idReservasi = reservasiDAO.insert(reservasi);

        reservasiMejaDAO.tambahReservasiMeja(new ReservasiMeja(idReservasi, idMeja));

        System.out.println("Reservasi berhasil. ID Reservasi: " + idReservasi);

        if (status.equalsIgnoreCase("dipesan")) {
            inputOrderMenu(scanner, idMeja);
        }
    }

    private static void konfirmasiKedatangan(Scanner scanner, Connection conn) {
        String sql = """
        SELECT 
            r.id_reservasi,
            p.nama AS nama_pelanggan,
            r.created_at,
            r.status_reservasi
        FROM reservasi r
        JOIN pelanggan p ON r.id_pelanggan = p.id_pelanggan
        WHERE DATE(r.created_at) = CURDATE() AND r.status_reservasi = 'terjadwal'
        ORDER BY r.created_at ASC
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== Daftar Reservasi Terjadwal Hari Ini ===");
            System.out.printf("%-5s %-20s %-20s %-12s\n", "ID", "Nama", "Waktu Reservasi", "Status");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s %-12s\n",
                        rs.getInt("id_reservasi"),
                        rs.getString("nama_pelanggan"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status_reservasi"));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil data reservasi: " + e.getMessage());
            return;
        }

        System.out.print("\nMasukkan ID reservasi yang ingin dikonfirmasi: ");
        int idReservasi = Integer.parseInt(scanner.nextLine());

        // Panggil stored procedure
        try (CallableStatement cs = conn.prepareCall("{CALL konfirmasi_kedatangan(?)}")) {
            cs.setInt(1, idReservasi);
            cs.execute();
            System.out.println("Kedatangan dikonfirmasi. Status diubah menjadi 'aktif'.");
        } catch (SQLException e) {
            System.out.println("Gagal mengonfirmasi kedatangan: " + e.getMessage());
        }
    }

    private static void inputOrderMenu(Scanner scanner) {
        System.out.print("Masukkan ID Meja: ");
        int idMeja = Integer.parseInt(scanner.nextLine());
        inputOrderMenu(scanner, idMeja);
    }

    private static void inputOrderMenu(Scanner scanner, int idMeja) {
        Map<Menu, Integer> pesanan = new LinkedHashMap<>();

        while (true) {
            System.out.print("Kategori (Makanan/Minuman/Camilan/Completed): ");
            String kategori = scanner.nextLine();

            if (kategori.equalsIgnoreCase("Completed")) {
                if (pesanan.isEmpty()) {
                    System.out.println("Tidak ada pesanan yang dimasukkan.");
                    return;
                }

                Reservasi r = reservasiDAO.getByMeja(idMeja);
                if (r == null) {
                    System.out.println("Reservasi tidak ditemukan untuk meja ini.");
                    return;
                }

                orderDAO.insertAll(pesanan, r.getIdReservasi());
                orderDAO.updateTotalTagihan(r.getIdReservasi());
                System.out.println("Pesanan berhasil disimpan.");
                return;
            }

            List<Menu> daftar = menuDAO.getMenuByKategori(kategori);
            if (daftar.isEmpty()) {
                System.out.println("Kategori tidak ditemukan.");
                continue;
            }

            for (Menu m : daftar) {
                System.out.printf("%d - %s (Rp %.0f)\n", m.getIdMenu(), m.getNama(), m.getHarga());
            }

            System.out.print("Pilih ID Menu: ");
            int idMenu = Integer.parseInt(scanner.nextLine());
            System.out.print("Jumlah: ");
            int jumlah = Integer.parseInt(scanner.nextLine());

            Menu selected = daftar.stream().filter(m -> m.getIdMenu() == idMenu).findFirst().orElse(null);
            if (selected != null) {
                pesanan.put(selected, pesanan.getOrDefault(selected, 0) + jumlah);
            } else {
                System.out.println("ID menu tidak ditemukan.");
            }
        }
    }

    private static void pembayaran(Scanner scanner) {
        System.out.print("Masukkan ID Meja: ");
        int idMeja = Integer.parseInt(scanner.nextLine());

        Reservasi r = reservasiDAO.getByMeja(idMeja);
        if (r == null) {
            System.out.println("Reservasi tidak ditemukan.");
            return;
        }

        if (pembayaranDAO.getByReservasi(r.getIdReservasi()) != null) {
            System.out.println("Reservasi ini sudah dibayar.");
            return;
        }

        List<OrderMakanan> orders = orderDAO.getByReservasi(r.getIdReservasi());
        if (orders.isEmpty()) {
            System.out.println("Belum ada pesanan.");
            return;
        }

        double total = orders.stream()
                .mapToDouble(o -> o.getMenu().getHarga() * o.getJumlah())
                .sum();

        System.out.println("Total Tagihan: Rp. " + total);
        System.out.print("Bayar        : Rp. ");
        double bayar = Double.parseDouble(scanner.nextLine());

        if (bayar < total) {
            System.out.println("Uang tidak cukup.");
            return;
        }

        double kembalian = bayar - total;

        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdReservasi(r.getIdReservasi());
        pembayaran.setMetode("cash");
        pembayaran.setTotalTagihan(total);
        pembayaran.setTanggalBayar(LocalDateTime.now());
        pembayaran.setStatusBayar("lunas");

        pembayaranDAO.insert(pembayaran);
        reservasiDAO.updateStatusReservasi(r.getIdReservasi(), "selesai");

// Update status meja ke "tersedia"
        ReservasiMejaDAO reservasiMejaDAO = new ReservasiMejaDAO(DBConnection.getConnection());
        List<ReservasiMeja> reservasiMejaList = reservasiMejaDAO.getReservasiMejaByReservasiId(r.getIdReservasi());

        for (ReservasiMeja rm : reservasiMejaList) {
            mejaDAO.updateStatusMeja(rm.getIdMeja(), "tersedia");
        }

        System.out.println("Pembayaran berhasil. Kembalian: Rp. " + kembalian);
    }
}
