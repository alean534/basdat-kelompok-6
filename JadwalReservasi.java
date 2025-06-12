package reservasi_foosen;

import java.sql.Date;
import java.sql.Time;

public class JadwalReservasi {
    private int idJadwal;
    private Date tanggal;
    private Time jamMulai;
    private Time jamSelesai;

    public JadwalReservasi(int idJadwal, Date tanggal, Time jamMulai, Time jamSelesai) {
        this.idJadwal = idJadwal;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
    }

    public int getIdJadwal() { return idJadwal; }
    public Date getTanggal() { return tanggal; }
    public Time getJamMulai() { return jamMulai; }
    public Time getJamSelesai() { return jamSelesai; }

    public void setIdJadwal(int idJadwal) { this.idJadwal = idJadwal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public void setJamMulai(Time jamMulai) { this.jamMulai = jamMulai; }
    public void setJamSelesai(Time jamSelesai) { this.jamSelesai = jamSelesai; }
}
