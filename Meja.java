package reservasi_foosen;

public class Meja {
    private int idMeja;
    private String nomorMeja;
    private String status;
    private int kapasitas;
    private String area;

    public Meja() {
        // Konstruktor default
    }

    public Meja(int idMeja, String nomorMeja, String status, int kapasitas, String area) {
        this.idMeja = idMeja;
        this.nomorMeja = nomorMeja;
        this.status = status;
        this.kapasitas = kapasitas;
        this.area = area;
    }

    public int getIdMeja() {
        return idMeja;
    }

    public void setIdMeja(int idMeja) {
        this.idMeja = idMeja;
    }

    public String getNomorMeja() {
        return nomorMeja;
    }

    public void setNomorMeja(String nomorMeja) {
        this.nomorMeja = nomorMeja;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
