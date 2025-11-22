# 🧁 Sistem Manajemen Toko Roti  
CRUD Roti • Pelanggan • Transaksi • CSV • JasperReport

Aplikasi **Sistem Manajemen Toko Roti** ini dibuat menggunakan **Java Swing**, **JPA (Hibernate/EclipseLink)**, dan **PostgreSQL**.  
Aplikasi ini menyediakan fitur lengkap untuk mengelola:

- **Data Roti**  
- **Data Pelanggan**  
- **Data Transaksi** (relasi antara roti & pelanggan)  
- Import & Export CSV  
- Cetak laporan menggunakan JasperReport  

---

## 🚀 Fitur Utama

### 1. CRUD Data Roti
- Tambah data roti  
- Edit roti  
- Hapus roti  
- Tabel data real-time  
- Relasi aman (hapus roti tidak menghapus pelanggan atau transaksi)

---

### 2. CRUD Data Pelanggan
- Tambah pelanggan  
- Edit pelanggan  
- Hapus pelanggan  
- Import CSV pelanggan  
- Validasi otomatis (nama, nomor HP, duplikasi)

Penghapusan pelanggan **tidak berpengaruh pada transaksi yang sudah ada**.

---

### 3. CRUD Data Transaksi
- Pilih roti  
- Pilih pelanggan  
- Hitung total harga otomatis  
- Update stok roti  
- Relasi JPA sudah dinormalisasi (ManyToOne)

---

## 🆕 Fitur Tambahan

### ✔ Import CSV
Mendukung:
- CSV Pelanggan  
- CSV Transaksi  

Fitur:
- Validasi kolom  
- Deteksi kesalahan format  
- Import batch via JPA transaction  

---

### ✔ Export CSV
Setiap tabel dapat diekspor ke `.csv` menggunakan:
- **JnaFileChooser** → Memilih lokasi file  
- Penulisan otomatis header + isi tabel  

---

### ✔ Laporan JasperReport
Aplikasi mampu mencetak:
- Laporan Roti  
- Laporan Pelanggan  
- Laporan Transaksi  

Menggunakan file:
- `.jrxml`
- `.jasper`

---

## 🗄 Struktur Database (Sudah Dinormalisasi)

### **Tabel: data_roti**
| Kolom | Tipe |
|-------|------|
| id_roti (PK) | VARCHAR |
| nama_roti   | VARCHAR |
| harga       | INT |
| stok        | INT |

### **Tabel: pelanggan**
| Kolom | Tipe |
|-------|------|
| id_pelanggan (PK) | VARCHAR |
| nama              | VARCHAR |
| alamat            | VARCHAR |
| no_hp             | VARCHAR |

### **Tabel: transaksi**
| Kolom | Tipe |
|-------|------|
| id_transaksi (PK) | VARCHAR |
| tanggal           | DATE |
| jumlah_beli       | INT |
| total_harga       | INT |
| id_roti (FK)      | VARCHAR |
| id_pelanggan (FK) | VARCHAR |

Relasi:
- **1 roti → banyak transaksi**  
- **1 pelanggan → banyak transaksi**  

---

## 🛠 Teknologi yang Digunakan

| Komponen | Teknologi |
|----------|-----------|
| Bahasa   | Java Swing |
| Database | PostgreSQL |
| ORM      | JPA / Hibernate / EclipseLink |
| Report   | JasperReport |
| File     | CSV |
| Dialog   | JnaFileChooser |

---

## 📎 Cara Menjalankan

1. Clone repository  
2. Import ke NetBeans  
3. Buat database PostgreSQL sesuai struktur  
4. Sesuaikan `persistence.xml`  
5. Jalankan aplikasi

---

## ✍️ Penulis

**TITHA AULIYA KHOTIM**  
Mahasiswa Sistem Informasi  
Semester 3  
Universitas Islam Negeri Sunan Ampel Surabaya  
