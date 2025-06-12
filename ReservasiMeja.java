package reservasi_foosen;

public class ReservasiMeja {
    private int idReservasiMeja;
    private int idReservasi;
    private int idMeja;

    public ReservasiMeja(int idReservasi, int idMeja) {
        this.idReservasi = idReservasi;
        this.idMeja = idMeja;
    }

    public ReservasiMeja(int idReservasiMeja, int idReservasi, int idMeja) {
        this.idReservasiMeja = idReservasiMeja;
        this.idReservasi = idReservasi;
        this.idMeja = idMeja;
    }

    public int getIdReservasiMeja() { return idReservasiMeja; }
    public int getIdReservasi() { return idReservasi; }
    public int getIdMeja() { return idMeja; }

    public void setIdReservasiMeja(int idReservasiMeja) { this.idReservasiMeja = idReservasiMeja; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }
    public void setIdMeja(int idMeja) { this.idMeja = idMeja; }
}
