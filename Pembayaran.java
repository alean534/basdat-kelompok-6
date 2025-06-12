package reservasi_foosen;

import java.time.LocalDateTime;

public class Pembayaran {
    private int idReservasi;
    private String metode;
    private double totalTagihan;
    private LocalDateTime tanggalBayar;
    private String statusBayar;

    public Pembayaran(int idReservasi, String metode, double totalTagihan, LocalDateTime tanggalBayar, String statusBayar) {
        this.idReservasi = idReservasi;
        this.metode = metode;
        this.totalTagihan = totalTagihan;
        this.tanggalBayar = tanggalBayar;
        this.statusBayar = statusBayar;
    }

    public Pembayaran() {}

    // Getter dan Setter
    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public String getMetode() { return metode; }
    public void setMetode(String metode) { this.metode = metode; }

    public double getTotalTagihan() { return totalTagihan; }
    public void setTotalTagihan(double totalTagihan) { this.totalTagihan = totalTagihan; }

    public LocalDateTime getTanggalBayar() { return tanggalBayar; }
    public void setTanggalBayar(LocalDateTime tanggalBayar) { this.tanggalBayar = tanggalBayar; }

    public String getStatusBayar() { return statusBayar; }
    public void setStatusBayar(String statusBayar) { this.statusBayar = statusBayar; }
}
