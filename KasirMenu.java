package reservasi_foosen;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KasirMenu {

    private static MenuDAO menuDAO;
    private static MejaDAO mejaDAO;
    private static ReservasiDAO reservasiDAO;
    private static OrderMakananDAO orderDAO;
    private static PembayaranDAO pembayaranDAO;
    private static StackOrder stackOrder = new StackOrder();

    public static void showKasirMenu(Scanner scanner, Connection conn) throws SQLException {
        menuDAO = new MenuDAO(conn);
        mejaDAO = new MejaDAO(conn);
        reservasiDAO = new ReservasiDAO(conn);
        orderDAO = new OrderMakananDAO(conn);
        pembayaranDAO = new PembayaranDAO(conn);

        while (true) {
            System.out.println("\n===============================================");
            System.out.println("                MENU KASIR");
            System.out.println("===============================================");
            System.out.println("1. BUAT RESERVASI");
            System.out.println("2. KONFIRMASI KEDATANGAN");
            System.out.println("3. INPUT ORDER MENU");
            System.out.println("4. PEMBAYARAN");
            System.out.println("5. LIHAT MEJA TERSEDIA");
            System.out.println("6. KELUAR");
            System.out.println("-----------------------------------------------");
            System.out.print("PILIH MENU : ");
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
                    ViewMejaTersedia view = new ViewMejaTersedia(conn);
                    view.tampilkanMejaTersedia(scanner);
                }
                case "6" -> {
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

        System.out.println("\n-----------------------------------------------");
        System.out.println("                BUAT RESERVASI");
        System.out.println("-----------------------------------------------");
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

        String preferensiArea = "";
        if (catatan.toLowerCase().contains("indoor")) {
            preferensiArea = "indoor";
        } else if (catatan.toLowerCase().contains("outdoor")) {
            preferensiArea = "outdoor";
        }

        List<Meja> semuaMeja = mejaDAO.getAllMeja();
        List<Meja> mejaTersedia = cariMejaMulti(semuaMeja, jumlah, preferensiArea);

        if (mejaTersedia.isEmpty()) {
            System.out.println("Tidak ada kombinasi meja yang mencukupi jumlah orang di area " + preferensiArea + ".");
            return;
        }

        System.out.println("\n-----------------------------------------------");
        System.out.println("\n--- Meja Akan Digunakan ---");
        for (Meja meja : mejaTersedia) {
            System.out.printf("ID: %d | No: %s | Kapasitas: %d | Area: %s\n",
                    meja.getIdMeja(), meja.getNomorMeja(), meja.getKapasitas(), meja.getArea());
        }

        int idPelanggan = pelangganDAO.insert(new Pelanggan(nama, telepon));
        int idJadwal = jadwalDAO.insert(tanggal, jamMulai, jamSelesai);

        Reservasi reservasi = new Reservasi(idPelanggan, idJadwal, status, catatan);
        int idReservasi = reservasiDAO.insert(reservasi);

        for (Meja meja : mejaTersedia) {
            reservasiMejaDAO.tambahReservasiMeja(new ReservasiMeja(idReservasi, meja.getIdMeja()));
        }

        System.out.println("\n-----------------------------------------------");
        System.out.println("Reservasi berhasil. ID Reservasi: " + idReservasi);

        if (status.equalsIgnoreCase("dipesan")) {
            inputOrderMenu(scanner, idReservasi);
        }
    }

    private static List<Meja> cariMejaMulti(List<Meja> daftarMeja, int jumlahOrang, String preferensiArea) {
        List<Meja> tersedia = new ArrayList<>();
        for (Meja m : daftarMeja) {
            if ("tersedia".equalsIgnoreCase(m.getStatus())) {
                if (preferensiArea.isEmpty() || m.getArea().equalsIgnoreCase(preferensiArea)) {
                    tersedia.add(m);
                }
            }
        }

        List<Meja> bestCombo = new ArrayList<>();
        int minTotal = Integer.MAX_VALUE;

        int n = tersedia.size();
        for (int i = 0; i < (1 << n); i++) {
            List<Meja> combo = new ArrayList<>();
            int total = 0;
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    Meja meja = tersedia.get(j);
                    combo.add(meja);
                    total += meja.getKapasitas();
                }
            }
            if (total >= jumlahOrang && total < minTotal) {
                bestCombo = combo;
                minTotal = total;
            }
        }

        return bestCombo;
    }

    private static void konfirmasiKedatangan(Scanner scanner, Connection conn) {
        System.out.println("\n-----------------------------------------------------");
        System.out.println("             KONFIRMASI KEDATANGAN");
        System.out.println("------------------------------------------------------");
        System.out.print("Masukkan Tanggal Reservasi (yyyy-mm-dd): ");
        String tanggalStr = scanner.nextLine();

        java.sql.Date tanggalSql;
        try {
            LocalDate tanggal = LocalDate.parse(tanggalStr); // format yyyy-mm-dd
            tanggalSql = java.sql.Date.valueOf(tanggal);
        } catch (Exception e) {
            System.out.println("Format tanggal tidak valid. Gunakan format yyyy-mm-dd.");
            return;
        }

        String sql = """
        SELECT 
            r.id_reservasi,
            p.nama AS nama_pelanggan,
            j.tanggal,
            j.jam_mulai,
            r.status_reservasi
        FROM reservasi r
        JOIN pelanggan p ON r.id_pelanggan = p.id_pelanggan
        JOIN jadwal_reservasi j ON r.id_jadwal = j.id_jadwal
        WHERE j.tanggal = ? AND r.status_reservasi = 'terjadwal'
        ORDER BY j.jam_mulai ASC
    """;

        List<Integer> idReservasiList = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, tanggalSql);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n--------------------------------------------------------------------------");
                System.out.println("     === Daftar Reservasi Terjadwal ===");
                System.out.println("----------------------------------------------------------------------------");
                System.out.printf("%-5s %-20s %-12s %-10s %-12s\n", "ID", "Nama", "Tanggal", "Jam", "Status");
                System.out.println("---------------------------------------------------------------------------");

                while (rs.next()) {
                    int idReservasi = rs.getInt("id_reservasi");
                    idReservasiList.add(idReservasi);
                    System.out.printf("%-5d %-20s %-12s %-10s %-12s\n",
                            idReservasi,
                            rs.getString("nama_pelanggan"),
                            rs.getDate("tanggal"),
                            rs.getTime("jam_mulai"),
                            rs.getString("status_reservasi"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengambil data reservasi: " + e.getMessage());
            return;
        }

        if (idReservasiList.isEmpty()) {
            System.out.println("Tidak ada reservasi terjadwal pada tanggal tersebut.");
            return;
        }

        System.out.print("\nMasukkan ID reservasi yang ingin diubah statusnya: ");
        int idReservasiInput;
        try {
            idReservasiInput = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID tidak valid.");
            return;
        }

        if (!idReservasiList.contains(idReservasiInput)) {
            System.out.println("ID reservasi tidak ditemukan di daftar.");
            return;
        }

        System.out.print("Ubah status ke (aktif/batal): ");
        String statusBaru = scanner.nextLine().toLowerCase();

        if (!statusBaru.equals("aktif") && !statusBaru.equals("batal")) {
            System.out.println("Status tidak valid.");
            return;
        }

// Update status reservasi
        ReservasiDAO reservasiDAO = new ReservasiDAO(conn);
        boolean sukses = reservasiDAO.updateStatusReservasi(idReservasiInput, statusBaru);

        if (sukses) {
            // Jika dibatalkan, kembalikan semua meja jadi tersedia
            if (statusBaru.equals("batal")) {
                System.out.println("Reservasi dibatalkan.");
            } else {
                System.out.println("Status reservasi berhasil diubah menjadi '" + statusBaru + "'.");
            }
        } else {
            System.out.println("Gagal mengubah status reservasi.");
        }

    }

    private static void inputOrderMenu(Scanner scanner) {
        System.out.println("\n-----------------------------------------------");
        System.out.println("                INPUT ORDER MENU");
        System.out.println("-----------------------------------------------");
        System.out.print("Masukkan ID Reservasi: ");
        int idReservasi = Integer.parseInt(scanner.nextLine());
        inputOrderMenu(scanner, idReservasi);
    }

    private static void inputOrderMenu(Scanner scanner, int idReservasi) {
        Map<Menu, Integer> pesanan = new LinkedHashMap<>();

        while (true) {
            System.out.print("Kategori (Makanan/Minuman/Camilan/Undo/Completed): ");
            String kategori = scanner.nextLine();
            System.out.println("-----------------------------------------------");

            if (kategori.equalsIgnoreCase("Completed")) {
                if (pesanan.isEmpty()) {
                    System.out.println("Tidak ada pesanan.");
                    return;
                }

                orderDAO.insertAll(pesanan, idReservasi);
                orderDAO.updateTotalTagihan(idReservasi);
                System.out.println("Pesanan berhasil disimpan.");
                return;
            }

            if (kategori.equalsIgnoreCase("Undo")) {
                OrderMakanan dibatalkan = stackOrder.pop();
                if (dibatalkan != null) {
                    Menu m = dibatalkan.getMenu();
                    int jumlah = dibatalkan.getJumlah();
                    pesanan.put(m, pesanan.getOrDefault(m, 0) - jumlah);
                    if (pesanan.get(m) <= 0) {
                        pesanan.remove(m);
                    }
                    System.out.println("Undo berhasil: " + m.getNama() + " x" + jumlah);
                    System.out.println("-----------------------------------------------\n");
                    if (pesanan.isEmpty()) {
                        System.out.println("Pesanan sekarang kosong.");
                    } else {
                        System.out.println("Pesanan saat ini:");
                        System.out.println("-----------------------------------------------\n");
                        System.out.printf("%-20s %-10s %-10s\n", "Menu", "Jumlah", "Subtotal");
                        System.out.println("-------------------------------------------");
                        for (Map.Entry<Menu, Integer> entry : pesanan.entrySet()) {
                            Menu menu = entry.getKey();
                            int jml = entry.getValue();
                            double subtotal = menu.getHarga() * jml;
                            System.out.printf("%-20s %-10d Rp%-10.0f\n", menu.getNama(), jml, subtotal);
                        }
                        System.out.println("-------------------------------------------");
                        double totalSementara = pesanan.entrySet().stream()
                                .mapToDouble(e -> e.getKey().getHarga() * e.getValue())
                                .sum();
                        System.out.printf("TOTAL SEMENTARA : Rp %.0f\n", totalSementara);
                        System.out.println("-----------------------------------------------");
                    }
                } else {
                    System.out.println("Tidak ada pesanan untuk di-undo.");
                }
                continue;
            }

            List<Menu> daftar = menuDAO.getMenuByKategori(kategori);
            if (daftar.isEmpty()) {
                System.out.println("Kategori tidak ditemukan.");
                continue;
            }

            for (Menu m : daftar) {
                System.out.printf("%d - %s (Rp %.0f)\n", m.getIdMenu(), m.getNama(), m.getHarga());
            }

            System.out.println("-----------------------------------------------");
            System.out.print("Pilih ID Menu: ");
            int idMenu = Integer.parseInt(scanner.nextLine());
            System.out.print("Jumlah: ");
            int jumlah = Integer.parseInt(scanner.nextLine());
            System.out.println("-----------------------------------------------");

            Menu selected = daftar.stream().filter(m -> m.getIdMenu() == idMenu).findFirst().orElse(null);
            if (selected != null) {
                pesanan.put(selected, pesanan.getOrDefault(selected, 0) + jumlah);
                OrderMakanan o = new OrderMakanan(idReservasi, selected.getIdMenu(), jumlah, "");
                o.setMenu(selected);
                stackOrder.push(o);  // simpan ke stack untuk bisa di-undo

                // 🖨️ Cetak ringkasan pesanan saat ini
                System.out.println("Pesanan saat ini:");
                System.out.println("-----------------------------------------------\n");
                System.out.printf("%-20s %-10s %-10s\n", "Menu", "Jumlah", "Subtotal");
                System.out.println("-------------------------------------------");
                for (Map.Entry<Menu, Integer> entry : pesanan.entrySet()) {
                    Menu m = entry.getKey();
                    int jml = entry.getValue();
                    double subtotal = m.getHarga() * jml;
                    System.out.printf("%-20s %-10d Rp%-10.0f\n", m.getNama(), jml, subtotal);
                }
                System.out.println("-------------------------------------------");
                double totalSementara = pesanan.entrySet().stream()
                        .mapToDouble(e -> e.getKey().getHarga() * e.getValue())
                        .sum();
                System.out.printf("TOTAL SEMENTARA : Rp %.0f\n", totalSementara);
                System.out.println("-----------------------------------------------");
            } else {
                System.out.println("ID menu tidak ditemukan.");
            }
        }

    }

    private static void pembayaran(Scanner scanner) {
        System.out.println("\n-----------------------------------------------");
        System.out.println("                PEMBAYARAN");
        System.out.println("-----------------------------------------------");
        System.out.print("Masukkan ID Reservasi: ");
        int idReservasi = Integer.parseInt(scanner.nextLine());

        Reservasi r = reservasiDAO.getById(idReservasi);
        if (r == null) {
            System.out.println("Reservasi tidak ditemukan.");
            return;
        }
        // Tampilkan informasi dasar reservasi
        Pelanggan pelanggan = new PelangganDAO(DBConnection.getConnection()).getById(r.getIdPelanggan());
        JadwalReservasiDAO jadwalDAO = new JadwalReservasiDAO(DBConnection.getConnection());
        LocalDate tanggalReservasi = jadwalDAO.getTanggalById(r.getIdJadwal());
        int jumlahOrang = new ReservasiMejaDAO(DBConnection.getConnection())
                .getTotalKapasitasMejaByReservasiId(idReservasi);

        System.out.println("\n-------------------------------------------");
        System.out.println("              DATA RESERVASI");
        System.out.println("-------------------------------------------");
        System.out.println("ID Reservasi   : " + idReservasi);
        System.out.println("Nama Pelanggan : " + pelanggan.getNama());
        System.out.println("Jumlah Orang   : " + jumlahOrang);
        System.out.println("Tanggal        : " + tanggalReservasi);
        System.out.println("-------------------------------------------\n");

        if (pembayaranDAO.getByReservasi(idReservasi) != null) {
            System.out.println("Reservasi ini sudah dibayar.");
            return;
        }

        List<OrderMakanan> orders = orderDAO.getByReservasi(idReservasi);
        if (orders.isEmpty()) {
            System.out.println("Belum ada pesanan.");
            return;
        }

        double total = orders.stream()
                .mapToDouble(o -> o.getMenu().getHarga() * o.getJumlah())
                .sum();

        System.out.printf("Total Tagihan: Rp %.0f\n", total);
        System.out.print("Bayar        : Rp ");
        double bayar = Double.parseDouble(scanner.nextLine());

        if (bayar < total) {
            System.out.println("Uang tidak cukup.");
            return;
        }

        double kembalian = bayar - total;

        // Ambil tanggal dan jam selesai dari jadwal
        
        LocalDate tanggalDatang = jadwalDAO.getTanggalById(r.getIdJadwal());
        LocalTime jamSelesai = jadwalDAO.getJamSelesaiById(r.getIdJadwal());

        if (tanggalDatang == null || jamSelesai == null) {
            System.out.println("Gagal mengambil tanggal atau jam selesai dari jadwal.");
            return;
        }

        LocalDateTime tanggalBayar = tanggalDatang.atTime(jamSelesai);

        // Simpan ke tabel pembayaran
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdReservasi(idReservasi);
        pembayaran.setMetode("cash");
        pembayaran.setTotalTagihan(total);
        pembayaran.setTanggalBayar(tanggalBayar);
        pembayaran.setStatusBayar("lunas");

        pembayaranDAO.insert(pembayaran);
        reservasiDAO.updateStatusReservasi(idReservasi, "selesai");

        // Update status meja jadi tersedia
        ReservasiMejaDAO reservasiMejaDAO = new ReservasiMejaDAO(DBConnection.getConnection());
        List<ReservasiMeja> reservasiMejaList = reservasiMejaDAO.getReservasiMejaByReservasiId(idReservasi);

        for (ReservasiMeja rm : reservasiMejaList) {
            mejaDAO.updateStatusMeja(rm.getIdMeja(), "tersedia");
        }

        // Cetak struk
        System.out.println("\n========== STRUK PEMBAYARAN ==========");
        String namaPelanggan = new PelangganDAO(DBConnection.getConnection())
                .getById(r.getIdPelanggan()).getNama();
        System.out.println("Nama Pelanggan : " + namaPelanggan);

        System.out.println("Meja Digunakan : ");
        for (ReservasiMeja rm : reservasiMejaList) {
            Meja meja = mejaDAO.getById(rm.getIdMeja());
            System.out.printf("  - No. Meja: %s | Area: %s | Kapasitas: %d\n",
                    meja.getNomorMeja(), meja.getArea(), meja.getKapasitas());
        }

        System.out.println("\nPesanan:");
        System.out.printf("%-20s %-10s %-10s\n", "Menu", "Jumlah", "Subtotal");
        System.out.println("-------------------------------------------");
        for (OrderMakanan o : orders) {
            double subtotal = o.getMenu().getHarga() * o.getJumlah();
            System.out.printf("%-20s %-10d Rp%-10.0f\n", o.getMenu().getNama(), o.getJumlah(), subtotal);
        }

        System.out.println("-------------------------------------------");
        System.out.printf("Total Tagihan : Rp %.0f\n", total);
        System.out.printf("Bayar         : Rp %.0f\n", bayar);
        System.out.printf("Kembalian     : Rp %.0f\n", kembalian);
        System.out.println("Metode        : CASH");
        System.out.println("Status        : LUNAS");
        System.out.printf("Tanggal Bayar : %s\n", tanggalBayar.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("===========================================\n");
    }

}
