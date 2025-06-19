package reservasi_foosen;

import java.sql.*;
import java.util.Scanner;

public class ViewMejaTersedia {

    private final Connection conn;

    public ViewMejaTersedia(Connection conn) {
        this.conn = conn;
    }

    public void tampilkanMejaTersedia(Scanner scanner) {
        try {
            System.out.println("\n---------------------------------------------------------------");
            System.out.println("                CEK KETERSEDIAAN");
            System.out.println("\n---------------------------------------------------------------");
            System.out.print("Tanggal (yyyy-mm-dd): ");
            String tanggalStr = scanner.nextLine();
            System.out.print("Jam Mulai (HH:mm): ");
            String jamStr = scanner.nextLine();

            Date tanggal = Date.valueOf(tanggalStr);
            Time jam = Time.valueOf(jamStr + ":00");

            String sql = """
                SELECT m.id_meja, m.nomor_meja, m.kapasitas, m.area
                FROM meja m
                WHERE m.status = 'tersedia'
                  AND m.id_meja NOT IN (
                      SELECT rm.id_meja
                      FROM reservasi_meja rm
                      JOIN reservasi r ON rm.id_reservasi = r.id_reservasi
                      JOIN jadwal_reservasi j ON r.id_jadwal = j.id_jadwal
                      WHERE j.tanggal = ? 
                        AND ? BETWEEN j.jam_mulai AND j.jam_selesai
                        AND r.status_reservasi NOT IN ('batal', 'selesai')
                  )
                ORDER BY m.nomor_meja
            """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, tanggal);
                stmt.setTime(2, jam);

                ResultSet rs = stmt.executeQuery();

                System.out.println("\n---------------------------------------------------------------");
                System.out.println("                         DAFTAR MEJA TERSEDIA");
                System.out.println("----------------------------------------------------------------");
                System.out.printf("%-6s %-10s %-10s %-10s\n", "ID", "Nomor", "Kapasitas", "Area");
                System.out.println("----------------------------------------------------------------");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("%-6d %-10s %-10d %-10s\n",
                            rs.getInt("id_meja"),
                            rs.getString("nomor_meja"),
                            rs.getInt("kapasitas"),
                            rs.getString("area"));
                }

                if (!found) {
                    System.out.println("Tidak ada meja tersedia pada waktu tersebut.");
                }
            }

        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
