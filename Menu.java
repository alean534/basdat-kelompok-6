package reservasi_foosen;

public class Menu {
    private int id_menu;
    private String nama_menu;
    private double harga;
    private String kategori;

    public Menu(int id_menu, String nama_menu, double harga, String kategori) {
        this.id_menu = id_menu;
        this.nama_menu = nama_menu;
        this.harga = harga;
        this.kategori = kategori;
    }

    public int getIdMenu() {
        return id_menu;
    }

    public String getNama() {
        return nama_menu;
    }

  
    public double getHarga() {
        return harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setIdMenu(int id_menu) {
        this.id_menu = id_menu;
    }

    public void setNama(String nama_menu) {
        this.nama_menu = nama_menu;
    }


    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
