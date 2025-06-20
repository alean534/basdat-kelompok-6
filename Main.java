
package reservasi_foosen;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===============================================");
            System.out.println("   SELAMAT DATANG DI SISTEM RESTORAN FOOSEN ");
            System.out.println("===============================================");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.println("-----------------------------------------------");
            System.out.print("Pilih: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                login(scanner);
            } else if (choice.equals("2")) {
                System.out.println("Terima kasih, program keluar.");
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        scanner.close();
    }

    private static void login(Scanner scanner) {
        System.out.println("\n-----------------------------------------------");
        System.out.println("           *** HALAMAN LOGIN USER ***");
        System.out.println("-----------------------------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            int attempts = 0;

            while (attempts < 3) {
                System.out.print("Username : ");
                String username = scanner.nextLine();
                System.out.print("Password : ");
                String password = scanner.nextLine();
                System.out.println("-----------------------------------------------");

                User user = userDAO.login(username, password);
                if (user != null) {
                    System.out.println("Login berhasil! Selamat datang, " + user.getUsername() + ".");
                    showMenuByRole(user, scanner, conn); // Kirim scanner & conn ke menu role
                    return;
                } else {
                    System.out.println("Login gagal, username atau password salah.");
                    attempts++;
                }
            }

            System.out.println("-----------------------------------------------");
            System.out.println("[ Anda telah melebihi 3 kali percobaan login. Program akan keluar. ]");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showMenuByRole(User user, Scanner scanner, Connection conn) {
    String role = user.getRole().toLowerCase();
    boolean kembali = false;


        try {
            switch (role) {
                case "admin":
                    AdminMenu.showAdminMenu(scanner, conn);
                    kembali = true; // <-- Tambahkan ini agar keluar setelah AdminMenu selesai
                    break;
                case "kasir":
                    KasirMenu.showKasirMenu(scanner, conn);
                    kembali = true;
                    break;
                default:
                    System.out.println("Role tidak dikenal.");
                    kembali = true;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
}

}
