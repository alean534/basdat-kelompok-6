# 🏕️ PROYEK SISTEM MANAJEMEN RESERVASI MEJA RESTORAN 
Program ini dirancang untuk membantu proses reservasi restoran. Mulai dari kelola keryawan, meja restoran, hingga reservasi.

---

## 🔑 Fitur Utama
AUTENTIKASI: 
  Fungsi ini berguna untuk memberikan akses sesuai dengan rolenya 

ROLE ADMIN:
  Tambah Menu, Hapus Menu, Edit Menu, Lihat Daftar Menu 

ROLE KASIR:
  Buat Reservasi, Konfirmasi Kedatangan, Input Order Menu, Pembayaran, Lihat Menu Tersedia 


---


## 🧑‍🤝‍🧑 Biodata Kelompok 

| Nama Lengkap            | NPM           |
|-------------------------|-------------------------|
|  Devi Vara Pramudyta    |  24082010093 |
|  Nindy Viviva Asri      |  24082010096 |
|  Novita Fitriani        |  24082010124 |
|  Rika Wati              |  24082010131 |


---


## 🔁 CTE Menampilkan Pendapatan Harian Tiap Area Meja (hanya dari reservasi yang lunas)  
WITH TagihanPerReservasi AS (  
    SELECT r.id_reservasi, m.area, p.total_tagihan, jr.tanggal  
    FROM reservasi r  
    JOIN reservasi_meja rm ON r.id_reservasi = rm.id_reservasi  
    JOIN meja m ON rm.id_meja = m.id_meja  
    JOIN pembayaran p ON r.id_reservasi = p.id_reservasi  
    JOIN jadwal_reservasi jr ON r.id_jadwal = jr.id_jadwal  
    WHERE p.status_bayar = 'lunas'  
)  
SELECT tanggal, area, SUM(total_tagihan) AS total_pendapatan  
FROM TagihanPerReservasi  
GROUP BY tanggal, area  
ORDER BY tanggal, area;  

## 🧁 SubQuery Pelanggan dengan Total Pesanan Terbanyak  
SELECT nama, telepon  
FROM pelanggan  
WHERE id_pelanggan = (  
    SELECT r.id_pelanggan  
    FROM reservasi r  
    JOIN order_makanan o ON r.id_reservasi = o.id_reservasi   
    GROUP BY r.id_pelanggan  
    ORDER BY SUM(o.jumlah) DESC  
    LIMIT 1  
);  


---

## 🖥️ CrossTab
-- **Menampilkan jumlah pesanan makanan/minuman per kategori dan per bulan dalam satu tahun.**  
SELECT    
    m.kategori AS kategori,  
    YEAR(j.tanggal) AS tahun,  
    SUM(CASE WHEN MONTH(j.tanggal) = 1 THEN om.jumlah ELSE 0 END) AS January,  
    SUM(CASE WHEN MONTH(j.tanggal) = 2 THEN om.jumlah ELSE 0 END) AS February,  
    SUM(CASE WHEN MONTH(j.tanggal) = 3 THEN om.jumlah ELSE 0 END) AS March,  
    SUM(CASE WHEN MONTH(j.tanggal) = 4 THEN om.jumlah ELSE 0 END) AS April,  
    SUM(CASE WHEN MONTH(j.tanggal) = 5 THEN om.jumlah ELSE 0 END) AS May,  
    SUM(CASE WHEN MONTH(j.tanggal) = 6 THEN om.jumlah ELSE 0 END) AS June,  
    SUM(CASE WHEN MONTH(j.tanggal) = 7 THEN om.jumlah ELSE 0 END) AS July,  
    SUM(CASE WHEN MONTH(j.tanggal) = 8 THEN om.jumlah ELSE 0 END) AS August,  
    SUM(CASE WHEN MONTH(j.tanggal) = 9 THEN om.jumlah ELSE 0 END) AS September,    
    SUM(CASE WHEN MONTH(j.tanggal) = 10 THEN om.jumlah ELSE 0 END) AS October,  
    SUM(CASE WHEN MONTH(j.tanggal) = 11 THEN om.jumlah ELSE 0 END) AS November,  
    SUM(CASE WHEN MONTH(j.tanggal) = 12 THEN om.jumlah ELSE 0 END) AS December  
FROM   
    menu m  
JOIN   
    order_makanan om ON m.id_menu = om.id_menu  
JOIN   
    reservasi r ON om.id_reservasi = r.id_reservasi    
JOIN   
    jadwal_reservasi j ON r.id_jadwal = j.id_jadwal  
GROUP BY   
    m.kategori, YEAR(j.tanggal);  


-- **Laporan jumlah reservasi berdasarkan status (selesai, batal, aktif, dll.) tiap bulan.**  
SELECT
    YEAR(j.tanggal) AS tahun,  
    MONTH(j.tanggal) AS bulan,  
    SUM(CASE WHEN r.status_reservasi = 'selesai' THEN 1 ELSE 0 END) AS selesai,  
    SUM(CASE WHEN r.status_reservasi = 'batal' THEN 1 ELSE 0 END) AS batal,  
    SUM(CASE WHEN r.status_reservasi = 'aktif' THEN 1 ELSE 0 END) AS aktif  
FROM   
    reservasi r  
JOIN   
    jadwal_reservasi j ON r.id_jadwal = j.id_jadwal  
GROUP BY   
    YEAR(j.tanggal), MONTH(j.tanggal);  


-- **Melihat total uang yang diterima dari tiap kategori menu per bulan (jumlah × harga).**  
SELECT    
    m.kategori,  
    YEAR(j.tanggal) AS tahun,  
    SUM(CASE WHEN MONTH(j.tanggal) = 1 THEN om.jumlah * m.harga ELSE 0 END) AS January,  
    SUM(CASE WHEN MONTH(j.tanggal) = 2 THEN om.jumlah * m.harga ELSE 0 END) AS February,  
    SUM(CASE WHEN MONTH(j.tanggal) = 3 THEN om.jumlah * m.harga ELSE 0 END) AS March,  
    SUM(CASE WHEN MONTH(j.tanggal) = 4 THEN om.jumlah * m.harga ELSE 0 END) AS April,  
    SUM(CASE WHEN MONTH(j.tanggal) = 5 THEN om.jumlah * m.harga ELSE 0 END) AS May,  
    SUM(CASE WHEN MONTH(j.tanggal) = 6 THEN om.jumlah * m.harga ELSE 0 END) AS June,  
    SUM(CASE WHEN MONTH(j.tanggal) = 7 THEN om.jumlah * m.harga ELSE 0 END) AS July,  
    SUM(CASE WHEN MONTH(j.tanggal) = 8 THEN om.jumlah * m.harga ELSE 0 END) AS August,  
    SUM(CASE WHEN MONTH(j.tanggal) = 9 THEN om.jumlah * m.harga ELSE 0 END) AS September,  
    SUM(CASE WHEN MONTH(j.tanggal) = 10 THEN om.jumlah * m.harga ELSE 0 END) AS October,  
    SUM(CASE WHEN MONTH(j.tanggal) = 11 THEN om.jumlah * m.harga ELSE 0 END) AS November,  
    SUM(CASE WHEN MONTH(j.tanggal) = 12 THEN om.jumlah * m.harga ELSE 0 END) AS December  
FROM   
    menu m  
JOIN   
    order_makanan om ON m.id_menu = om.id_menu  
JOIN   
    reservasi r ON om.id_reservasi = r.id_reservasi  
JOIN   
    jadwal_reservasi j ON r.id_jadwal = j.id_jadwal  
GROUP BY    
    m.kategori, YEAR(j.tanggal);  


---

## 🎯 CRUD  
**Create: Memasukkan nama user dan passwordnya**  
Insert user (username dan password)  
INSERT INTO user (username, password) VALUES  
('rika', '131'),  
('novi', '124'),  
('devi', '093'),  
('nin', '096');  
  
**Read: Menampilkan data reservasi lengkap**  
SELECT r.id_reservasi, p.nama, jr.tanggal, jr.jam_mulai, r.catatan_reservasi, r.status_reservasi  
FROM reservasi r  
JOIN pelanggan p ON r.id_pelanggan = p.id_pelanggan  
JOIN jadwal_reservasi jr ON r.id_jadwal = jr.id_jadwal;  

**Update: Update harga menu berdasarkan id**  
UPDATE menu  
SET harga = 27000  
WHERE id_menu = 1;  
  
**Delete: Hapus menu yang tidak pernah dipesan**  
DELETE FROM menu  
WHERE id_menu NOT IN (  
    SELECT DISTINCT id_menu  
    FROM order_makanan  
);  

