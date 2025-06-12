package reservasi_foosen;

public class OrderMakanan {
    private int idOrder;
    private int idReservasi;
    private int idMenu;
    private int jumlah;
    private String catatan;
    private Menu menu; // relasi ke objek menu

    public OrderMakanan(int idOrder, int idReservasi, int idMenu, int jumlah, String catatan, Menu menu) {
        this.idOrder = idOrder;
        this.idReservasi = idReservasi;
        this.idMenu = idMenu;
        this.jumlah = jumlah;
        this.catatan = catatan;
        this.menu = menu;
    }

    public OrderMakanan(int idReservasi, int idMenu, int jumlah, String catatan) {
        this.idReservasi = idReservasi;
        this.idMenu = idMenu;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    public int getIdOrder() { return idOrder; }
    public void setIdOrder(int idOrder) { this.idOrder = idOrder; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdMenu() { return idMenu; }
    public void setIdMenu(int idMenu) { this.idMenu = idMenu; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}
