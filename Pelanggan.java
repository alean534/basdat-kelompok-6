package reservasi_foosen;

public class Pelanggan {
    private int id_pelanggan;
    private String nama;
    private String telepon;
    
    public Pelanggan(int id_pelanggan, String nama, String telepon) {
        this.id_pelanggan = id_pelanggan;
        this.nama = nama;
        this.telepon = telepon;
    }


    public Pelanggan(String nama, String telepon) {
        this.nama = nama;
        this.telepon = telepon;
    }

    public String getNama() { return nama; }
    public String getNoTlp() { return telepon; }
}
