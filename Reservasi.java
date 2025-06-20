package reservasi_foosen;

import java.time.LocalDateTime;

public class Reservasi {
    private int idReservasi;
    private int idPelanggan;
    private int idJadwal;
    private String statusReservasi;
    private String catatanReservasi;

    public Reservasi(int idReservasi, int idPelanggan, int idJadwal, String statusReservasi, String catatanReservasi) {
        this.idReservasi = idReservasi;
        this.idPelanggan = idPelanggan;
        this.idJadwal = idJadwal;
        this.statusReservasi = statusReservasi;
        this.catatanReservasi = catatanReservasi;
    }

    public Reservasi(int idPelanggan, int idJadwal, String statusReservasi, String catatanReservasi) {
        this.idPelanggan = idPelanggan;
        this.idJadwal = idJadwal;
        this.statusReservasi = statusReservasi;
        this.catatanReservasi = catatanReservasi;
    }

    // Getter & Setter
    public int getIdReservasi() { return idReservasi; }
    public int getIdPelanggan() { return idPelanggan; }
    public int getIdJadwal() { return idJadwal; }
    public String getStatusReservasi() { return statusReservasi; }
    public String getCatatanReservasi() { return catatanReservasi; }

    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }
    public void setIdPelanggan(int idPelanggan) { this.idPelanggan = idPelanggan; }
    public void setIdJadwal(int idJadwal) { this.idJadwal = idJadwal; }
    public void setStatusReservasi(String statusReservasi) { this.statusReservasi = statusReservasi; }
    public void setCatatanReservasi(String catatanReservasi) { this.catatanReservasi = catatanReservasi; }
}
