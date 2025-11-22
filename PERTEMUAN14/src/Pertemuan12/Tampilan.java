/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Pertemuan12;

import Pertemuan12.InsertDATAROTI;
import Pertemuan12.InsertTRANSAKSI;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import jnafilechooser.api.JnaFileChooser;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author HP
 */
public class Tampilan extends javax.swing.JFrame {

    private EntityManagerFactory emf;
    private EntityManager em;

    String idrotiLama, namarotiLama, stokLama, hargaLama, idpelangganLama;
    String namaLama, alamatLama, nohpLama;
    String idTransaksiLama, tanggalLama, jumlahLama, totalLama;
    String idpelangganLamaTransaksi, idrotiLamaTransaksi;

    public void connect() {
        try {
            emf = Persistence.createEntityManagerFactory("PERTEMUAN14PU");
            em = emf.createEntityManager();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

    public Tampilan() {
        initComponents();
        connect();
        showTable();
        showTablePelanggan();
        showTableTransaksi();
        setLocationRelativeTo(null);

        tabeldataroti.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabeldataroti.getSelectedRow();

                idrotiLama = tabeldataroti.getValueAt(row, 0).toString();
                namarotiLama = tabeldataroti.getValueAt(row, 1).toString();
                hargaLama = tabeldataroti.getValueAt(row, 2).toString();
                stokLama = tabeldataroti.getValueAt(row, 3).toString();
                idpelangganLama = tabeldataroti.getValueAt(row, 4).toString();

            }
        });

        tabelpelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabelpelanggan.getSelectedRow();

                idpelangganLama = tabelpelanggan.getValueAt(row, 0).toString();
                namaLama = tabelpelanggan.getValueAt(row, 1).toString();
                alamatLama = tabelpelanggan.getValueAt(row, 2).toString();
                nohpLama = tabelpelanggan.getValueAt(row, 3).toString();

            }
        });
    }

    public void exportToCSV(JTable table, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {

            TableModel model = table.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.append(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    writer.append(";");
                }
            }
            writer.append("\n");

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.append(value == null ? "" : value.toString());
                    if (col < model.getColumnCount() - 1) {
                        writer.append(";");
                    }
                }
                writer.append("\n");
            }

            writer.flush();
            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor CSV: " + e.getMessage());
        }
    }

    public void showTable() {
        try {
            em.clear();

            List<DataRoti> hasil = em.createNamedQuery("DataRoti.findAll", DataRoti.class)
                    .getResultList();

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID Roti", "Nama Roti", "Stok", "Harga", "Nama Pelanggan"}, 0
            );

            for (DataRoti p : hasil) {
                model.addRow(new Object[]{
                    p.getIdRoti(),
                    p.getNamaRoti(),
                    p.getStok(),
                    p.getHarga(),
                    p.getIdPelanggan()
                });
            }

            tabeldataroti.setModel(model);

            // Rata tengah semua kolom
            for (int i = 0; i < tabeldataroti.getColumnCount(); i++) {
                tabeldataroti.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil data: " + e.getMessage());
        }

    }

    private void imporCsvKeDatabaseDataRoti() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File CSV");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this,
                        "File yang dipilih bukan file CSV!\nSilakan pilih file dengan ekstensi .csv",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                br.readLine(); // lewati baris header

                em.getTransaction().begin();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");

                    if (data.length == 5) {
                        DataRoti roti = new DataRoti();
                        roti.setIdRoti(data[0].trim());
                        roti.setNamaRoti(data[1].trim());
                        roti.setStok(Integer.parseInt(data[2].trim()));
                        roti.setHarga(Integer.parseInt(data[3].trim()));

                        String idPelanggan = data[4].trim();
                        Pelanggan pelanggan = em.find(Pelanggan.class, idPelanggan);

                        if (pelanggan != null) {
                            roti.setIdPelanggan(pelanggan);
                            em.persist(roti);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Pelanggan dengan ID '" + idPelanggan + "' tidak ditemukan.\nBaris ini dilewati.",
                                    "Kesalahan Data Pelanggan",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Format CSV tidak sesuai di baris: " + line,
                                "Kesalahan Format CSV",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "Data roti berhasil diimpor dari CSV!");
                showTable();
                showTablePelanggan();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal impor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void showTablePelanggan() {
        try {
            List<Pelanggan> hasil = em.createNamedQuery("Pelanggan.findAll", Pelanggan.class)
                    .getResultList();

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID Pelanggan", "Nama Pelanggan", "Alamat", "No Telepon"}, 0
            );

            for (Pelanggan p : hasil) {
                model.addRow(new Object[]{
                    p.getIdPelanggan(),
                    p.getNamaPelanggan(),
                    p.getAlamat(),
                    p.getNoTelepon(),});
            }

            tabelpelanggan.setModel(model);

            for (int i = 0; i < tabelpelanggan.getColumnCount(); i++) {
                tabelpelanggan.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil data: " + e.getMessage());
        }
    }

    public void showTableTransaksi() {
        List<Transaksi> hasil = em.createNamedQuery("Transaksi.findAll", Transaksi.class).getResultList();

        DefaultTableModel model = new DefaultTableModel(new String[]{
            "ID Transaksi", "Tanggal", "Nama Pelanggan", "Nama Roti", "Jumlah", "Total Harga"
        }, 0);

        for (Transaksi t : hasil) {
            model.addRow(new Object[]{
                t.getIdTransaksi(),
                t.getTanggal(),
                t.getIdPelanggan().getNamaPelanggan(),
                t.getIdRoti().getNamaRoti(),
                t.getJumlahBeli(),
                t.getTotalHarga()
            });
        }

        tabeltransaksi.setModel(model);
    }

    private void imporCsvKeDatabasePelanggan() {
        JnaFileChooser fileChooser = new JnaFileChooser();
        fileChooser.setTitle("Import Data Pelanggan");
        Window Window = null;
        boolean action = fileChooser.showOpenDialog(Window);

        if (action) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this,
                        "File yang dipilih bukan file CSV!\nSilakan pilih file dengan ekstensi .csv",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                br.readLine();

                em.getTransaction().begin();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");

                    if (data.length == 4) {
                        Pelanggan p = new Pelanggan();
                        p.setIdPelanggan(data[0].trim());
                        p.setNamaPelanggan(data[1].trim());
                        p.setAlamat(data[2].trim());
                        p.setNoTelepon(data[3].trim());

                        em.persist(p);
                    } else {
                        // Kalau jumlah kolom tidak sesuai
                        JOptionPane.showMessageDialog(this,
                                "Format CSV tidak sesuai di baris: " + line,
                                "Kesalahan Format CSV",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diimpor dari CSV!");
                showTablePelanggan(); // tampilkan data terbaru

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal impor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void imporCsvKeDatabaseTransaksi() {
        JnaFileChooser fileChooser = new JnaFileChooser();
        fileChooser.setTitle("Import Data Transaksi");
        Window window = null;
        boolean action = fileChooser.showOpenDialog(window);

        if (action) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this,
                        "File yang dipilih bukan file CSV!\nSilakan pilih file dengan ekstensi .csv",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                br.readLine();
                em.getTransaction().begin();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");

                    if (data.length == 6) {

                        Pelanggan pelanggan = em.find(Pelanggan.class, data[4].trim());
                        DataRoti roti = em.find(DataRoti.class, data[5].trim());

                        if (pelanggan == null || roti == null) {
                            JOptionPane.showMessageDialog(this,
                                    "Data relasi tidak ditemukan!\nBaris: " + line,
                                    "Error Relasi",
                                    JOptionPane.WARNING_MESSAGE);
                            continue;
                        }

                        Transaksi t = new Transaksi();
                        t.setIdTransaksi(data[0].trim());

                        Date tanggal = java.sql.Date.valueOf(data[1].trim());
                        t.setTanggal(tanggal);

                        t.setJumlahBeli(Integer.parseInt(data[2].trim()));
                        t.setTotalHarga(Integer.parseInt(data[3].trim()));
                        t.setIdPelanggan(pelanggan);
                        t.setIdRoti(roti);

                        em.persist(t);

                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Format CSV tidak sesuai di baris: " + line,
                                "Kesalahan Format CSV",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "Data transaksi berhasil diimpor dari CSV!");
                showTableTransaksi(); // refresh tabel

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal impor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelpelanggan = new javax.swing.JTable();
        jButtonTAMBAH1 = new javax.swing.JButton();
        jButtonHAPUS1 = new javax.swing.JButton();
        jButtonPERBARUI1 = new javax.swing.JButton();
        jButtonCETAKPELANGGAN = new javax.swing.JButton();
        jButtonUPLOAD1 = new javax.swing.JButton();
        jButtonUNDUHFILE = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonTAMBAH = new javax.swing.JButton();
        jButtonHAPUS = new javax.swing.JButton();
        jButtonPERBARUI = new javax.swing.JButton();
        jButtonCETAKDATAROTI = new javax.swing.JButton();
        jButtonUPLOAD = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabeldataroti = new javax.swing.JTable();
        jButtonUNDUH = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabeltransaksi = new javax.swing.JTable();
        jButtonTAMBAHTRANSAKSI = new javax.swing.JButton();
        jButtonPERBARUI2 = new javax.swing.JButton();
        jButtonHAPUSTRANSAKSI = new javax.swing.JButton();
        jButtonUNDUHFILE1 = new javax.swing.JButton();
        jButtonCETAKPELANGGAN1 = new javax.swing.JButton();
        jButtonUPLOAD2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Constantia", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("TOKO ROTI");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, -1, -1));

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel4.setBackground(new java.awt.Color(255, 204, 102));

        tabelpelanggan.setBackground(new java.awt.Color(255, 204, 102));
        tabelpelanggan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tabelpelanggan.setForeground(new java.awt.Color(0, 0, 0));
        tabelpelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tabelpelanggan);

        jButtonTAMBAH1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonTAMBAH1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonTAMBAH1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonTAMBAH1.setText("TAMBAH");
        jButtonTAMBAH1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTAMBAH1ActionPerformed(evt);
            }
        });

        jButtonHAPUS1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonHAPUS1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonHAPUS1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonHAPUS1.setText("HAPUS");
        jButtonHAPUS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHAPUS1ActionPerformed(evt);
            }
        });

        jButtonPERBARUI1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonPERBARUI1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonPERBARUI1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonPERBARUI1.setText("PERBARUI");
        jButtonPERBARUI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPERBARUI1ActionPerformed(evt);
            }
        });

        jButtonCETAKPELANGGAN.setBackground(new java.awt.Color(255, 255, 204));
        jButtonCETAKPELANGGAN.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonCETAKPELANGGAN.setForeground(new java.awt.Color(0, 0, 0));
        jButtonCETAKPELANGGAN.setText("CETAK");
        jButtonCETAKPELANGGAN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCETAKPELANGGANActionPerformed(evt);
            }
        });

        jButtonUPLOAD1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonUPLOAD1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUPLOAD1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUPLOAD1.setText("MENGUNGGAH");
        jButtonUPLOAD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUPLOAD1ActionPerformed(evt);
            }
        });

        jButtonUNDUHFILE.setBackground(new java.awt.Color(255, 255, 204));
        jButtonUNDUHFILE.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUNDUHFILE.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUNDUHFILE.setText("Unduh File");
        jButtonUNDUHFILE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUNDUHFILEActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jButtonTAMBAH1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonHAPUS1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonPERBARUI1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonUNDUHFILE, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButtonCETAKPELANGGAN, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonUPLOAD1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUPLOAD1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHAPUS1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTAMBAH1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPERBARUI1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCETAKPELANGGAN, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUNDUHFILE, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        jTabbedPane1.addTab("PELANGGAN", jPanel4);

        jPanel1.setBackground(new java.awt.Color(255, 153, 102));

        jButtonTAMBAH.setBackground(new java.awt.Color(255, 102, 51));
        jButtonTAMBAH.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonTAMBAH.setForeground(new java.awt.Color(0, 0, 0));
        jButtonTAMBAH.setText("TAMBAH");
        jButtonTAMBAH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTAMBAHActionPerformed(evt);
            }
        });

        jButtonHAPUS.setBackground(new java.awt.Color(255, 102, 51));
        jButtonHAPUS.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonHAPUS.setForeground(new java.awt.Color(0, 0, 0));
        jButtonHAPUS.setText("HAPUS");
        jButtonHAPUS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHAPUSActionPerformed(evt);
            }
        });

        jButtonPERBARUI.setBackground(new java.awt.Color(255, 102, 51));
        jButtonPERBARUI.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonPERBARUI.setForeground(new java.awt.Color(0, 0, 0));
        jButtonPERBARUI.setText("PERBARUI");
        jButtonPERBARUI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPERBARUIActionPerformed(evt);
            }
        });

        jButtonCETAKDATAROTI.setBackground(new java.awt.Color(255, 102, 51));
        jButtonCETAKDATAROTI.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonCETAKDATAROTI.setForeground(new java.awt.Color(0, 0, 0));
        jButtonCETAKDATAROTI.setText("CETAK");
        jButtonCETAKDATAROTI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCETAKDATAROTIActionPerformed(evt);
            }
        });

        jButtonUPLOAD.setBackground(new java.awt.Color(255, 102, 51));
        jButtonUPLOAD.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUPLOAD.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUPLOAD.setText("MENGUNGGAH");
        jButtonUPLOAD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUPLOADActionPerformed(evt);
            }
        });

        tabeldataroti.setBackground(new java.awt.Color(255, 153, 102));
        tabeldataroti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tabeldataroti.setForeground(new java.awt.Color(0, 0, 0));
        tabeldataroti.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id_roti", "nama_roti", "harga", "stock", "namapelanggan"
            }
        ));
        jScrollPane1.setViewportView(tabeldataroti);

        jButtonUNDUH.setBackground(new java.awt.Color(255, 102, 51));
        jButtonUNDUH.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUNDUH.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUNDUH.setText("Unduh File");
        jButtonUNDUH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUNDUHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jButtonTAMBAH, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonHAPUS, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPERBARUI, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButtonUNDUH, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jButtonCETAKDATAROTI, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonUPLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUPLOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHAPUS, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTAMBAH, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPERBARUI, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCETAKDATAROTI, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUNDUH, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab("DATA ROTI", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 153));
        jPanel3.setForeground(new java.awt.Color(0, 0, 0));

        tabeltransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tabeltransaksi);

        jButtonTAMBAHTRANSAKSI.setBackground(new java.awt.Color(255, 255, 204));
        jButtonTAMBAHTRANSAKSI.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonTAMBAHTRANSAKSI.setForeground(new java.awt.Color(0, 0, 0));
        jButtonTAMBAHTRANSAKSI.setText("TAMBAH");
        jButtonTAMBAHTRANSAKSI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTAMBAHTRANSAKSIActionPerformed(evt);
            }
        });

        jButtonPERBARUI2.setBackground(new java.awt.Color(255, 255, 204));
        jButtonPERBARUI2.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonPERBARUI2.setForeground(new java.awt.Color(0, 0, 0));
        jButtonPERBARUI2.setText("PERBARUI");
        jButtonPERBARUI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPERBARUI2ActionPerformed(evt);
            }
        });

        jButtonHAPUSTRANSAKSI.setBackground(new java.awt.Color(255, 102, 51));
        jButtonHAPUSTRANSAKSI.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonHAPUSTRANSAKSI.setForeground(new java.awt.Color(0, 0, 0));
        jButtonHAPUSTRANSAKSI.setText("HAPUS");
        jButtonHAPUSTRANSAKSI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHAPUSTRANSAKSIActionPerformed(evt);
            }
        });

        jButtonUNDUHFILE1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonUNDUHFILE1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUNDUHFILE1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUNDUHFILE1.setText("Unduh File");
        jButtonUNDUHFILE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUNDUHFILE1ActionPerformed(evt);
            }
        });

        jButtonCETAKPELANGGAN1.setBackground(new java.awt.Color(255, 255, 204));
        jButtonCETAKPELANGGAN1.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonCETAKPELANGGAN1.setForeground(new java.awt.Color(0, 0, 0));
        jButtonCETAKPELANGGAN1.setText("CETAK");
        jButtonCETAKPELANGGAN1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCETAKPELANGGAN1ActionPerformed(evt);
            }
        });

        jButtonUPLOAD2.setBackground(new java.awt.Color(255, 255, 204));
        jButtonUPLOAD2.setFont(new java.awt.Font("Microsoft PhagsPa", 1, 10)); // NOI18N
        jButtonUPLOAD2.setForeground(new java.awt.Color(0, 0, 0));
        jButtonUPLOAD2.setText("MENGUNGGAH");
        jButtonUPLOAD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUPLOAD2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 713, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jButtonTAMBAHTRANSAKSI, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonPERBARUI2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonHAPUSTRANSAKSI, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jButtonUNDUHFILE1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonCETAKPELANGGAN1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonUPLOAD2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTAMBAHTRANSAKSI, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPERBARUI2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHAPUSTRANSAKSI, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUNDUHFILE1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCETAKPELANGGAN1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUPLOAD2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("TRANSAKSI", jPanel3);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 710, 320));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/foto/A rustic, artisanal bakery display, featuring freshly baked bread and pastries, celebrat.jpeg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 370));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUNDUHFILEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUNDUHFILEActionPerformed
        try {
            if (tabelpelanggan.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor!");
                return;
            }

            JnaFileChooser chooser = new JnaFileChooser();
            chooser.setMode(JnaFileChooser.Mode.Files);
            chooser.setTitle("Simpan Data Pelanggan");
            chooser.setDefaultFileName("Data_Pelanggan");
            chooser.setMultiSelectionEnabled(false);
            Window Window = null;
            boolean action = chooser.showOpenDialog(Window);

            if (action) {

                String fileString = chooser.getSelectedFile().getAbsolutePath() + ".csv";

                exportToCSV(tabelpelanggan, fileString);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonUNDUHFILEActionPerformed

    private void jButtonUPLOAD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUPLOAD1ActionPerformed
        imporCsvKeDatabasePelanggan();
    }//GEN-LAST:event_jButtonUPLOAD1ActionPerformed

    private void jButtonCETAKPELANGGANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCETAKPELANGGANActionPerformed
        try {
            String path = "src/pertemuan12/DATAPELANGGAN.jasper";
            HashMap<String, Object> parameters = new HashMap<>();

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/TUGASPBO12", "postgres", "BISMILLAH");

            JasperPrint jprint = JasperFillManager.fillReport(path, parameters, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setSize(800, 600);
            jviewer.setLocationRelativeTo(this);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonCETAKPELANGGANActionPerformed

    private void jButtonPERBARUI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPERBARUI1ActionPerformed
        int row = tabelpelanggan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel!");
            return;
        }

        idpelangganLama = tabelpelanggan.getValueAt(row, 0).toString();
        namaLama = tabelpelanggan.getValueAt(row, 1).toString();
        alamatLama = tabelpelanggan.getValueAt(row, 2).toString();
        nohpLama = tabelpelanggan.getValueAt(row, 3).toString();

        UpdatePELANGGAN dialog = new UpdatePELANGGAN(this, true, idpelangganLama, namaLama, alamatLama, nohpLama);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        showTable();
        showTablePelanggan();
    }//GEN-LAST:event_jButtonPERBARUI1ActionPerformed

    private void jButtonHAPUS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHAPUS1ActionPerformed
        int[] rows = tabelpelanggan.getSelectedRows(); // ambil semua baris yang dipilih
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel!");
            return;
        }

        String[] idpelangganLama = new String[rows.length];
        String[] namaLama = new String[rows.length];
        String[] alamatLama = new String[rows.length];
        String[] nohpLama = new String[rows.length];

        for (int i = 0; i < rows.length; i++) {
            idpelangganLama[i] = tabelpelanggan.getValueAt(rows[i], 0).toString();
            namaLama[i] = tabelpelanggan.getValueAt(rows[i], 1).toString();
            alamatLama[i] = tabelpelanggan.getValueAt(rows[i], 2).toString();
            nohpLama[i] = tabelpelanggan.getValueAt(rows[i], 3).toString();

        }

        DeletePELANGGAN dialog = new DeletePELANGGAN(this, true, idpelangganLama, namaLama, alamatLama, nohpLama);
        dialog.setLocationRelativeTo(this); // tampil di tengah layar
        dialog.setVisible(true);

        showTable();
        showTablePelanggan();

    }//GEN-LAST:event_jButtonHAPUS1ActionPerformed

    private void jButtonTAMBAH1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTAMBAH1ActionPerformed
        InsertPELANGGAN dialog = new InsertPELANGGAN(this, true); //
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        showTablePelanggan();
    }//GEN-LAST:event_jButtonTAMBAH1ActionPerformed

    private void jButtonUNDUHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUNDUHActionPerformed
        try {
            if (tabeldataroti.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor!");
                return;
            }

            JnaFileChooser chooser = new JnaFileChooser();
            chooser.setMode(JnaFileChooser.Mode.Files);
            chooser.setTitle("Simpan Data Roti");
            chooser.setDefaultFileName("Data_Roti");
            chooser.setMultiSelectionEnabled(false);
            Window Window = null;
            boolean action = chooser.showOpenDialog(Window);

            if (action) {

                String fileString = chooser.getSelectedFile().getAbsolutePath() + ".csv";

                exportToCSV(tabeldataroti, fileString);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonUNDUHActionPerformed

    private void jButtonUPLOADActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUPLOADActionPerformed
        imporCsvKeDatabaseDataRoti();
    }//GEN-LAST:event_jButtonUPLOADActionPerformed

    private void jButtonCETAKDATAROTIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCETAKDATAROTIActionPerformed
        try {
            String path = "src/pertemuan12/REPORTDATAROTI.jasper";
            HashMap<String, Object> parameters = new HashMap<>();

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/TUGASPBO12", "postgres", "BISMILLAH");

            JasperPrint jprint = JasperFillManager.fillReport(path, parameters, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setSize(800, 600);
            jviewer.setLocationRelativeTo(this);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonCETAKDATAROTIActionPerformed

    private void jButtonPERBARUIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPERBARUIActionPerformed
        int row = tabeldataroti.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel!");
            return;
        }

        idrotiLama = tabeldataroti.getValueAt(row, 0).toString();
        namarotiLama = tabeldataroti.getValueAt(row, 1).toString();
        hargaLama = tabeldataroti.getValueAt(row, 2).toString();
        stokLama = tabeldataroti.getValueAt(row, 3).toString();
        idpelangganLama = tabeldataroti.getValueAt(row, 4).toString();

        UpdateDATAROTI dialog = new UpdateDATAROTI(this, true, idrotiLama, namarotiLama, stokLama, hargaLama, idpelangganLama);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        showTable();
        showTablePelanggan();
    }//GEN-LAST:event_jButtonPERBARUIActionPerformed

    private void jButtonHAPUSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHAPUSActionPerformed
        int[] rows = tabeldataroti.getSelectedRows(); // ambil semua baris yang dipilih
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel!");
            return;
        }

        String[] idrotiLama = new String[rows.length];
        String[] namarotiLama = new String[rows.length];
        String[] hargaLama = new String[rows.length];
        String[] stokLama = new String[rows.length];
        String[] idpelangganLama = new String[rows.length];

        for (int i = 0; i < rows.length; i++) {
            idrotiLama[i] = tabeldataroti.getValueAt(rows[i], 0).toString();
            namarotiLama[i] = tabeldataroti.getValueAt(rows[i], 1).toString();
            stokLama[i] = tabeldataroti.getValueAt(rows[i], 2).toString();
            hargaLama[i] = tabeldataroti.getValueAt(rows[i], 3).toString();
            idpelangganLama[i] = tabeldataroti.getValueAt(rows[i], 4).toString();
        }

        DeleteDATAROTI dialog = new DeleteDATAROTI(this, true, idrotiLama, namarotiLama, hargaLama, stokLama, idpelangganLama);
        dialog.setLocationRelativeTo(this); // tampil di tengah layar
        dialog.setVisible(true);

        showTable();
        showTablePelanggan();
    }//GEN-LAST:event_jButtonHAPUSActionPerformed

    private void jButtonTAMBAHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTAMBAHActionPerformed
        InsertDATAROTI dialog = new InsertDATAROTI(this, true); // true = modal
        dialog.setLocationRelativeTo(this); // supaya muncul di tengah
        dialog.setVisible(true);

        showTablePelanggan();
        showTable();
    }//GEN-LAST:event_jButtonTAMBAHActionPerformed

    private void jButtonTAMBAHTRANSAKSIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTAMBAHTRANSAKSIActionPerformed
        InsertTRANSAKSI dialog = new InsertTRANSAKSI(this, true); // true = modal
        dialog.setLocationRelativeTo(this); // supaya muncul di tengah
        dialog.setVisible(true);

        showTableTransaksi();
        showTablePelanggan();
        showTable();
    }//GEN-LAST:event_jButtonTAMBAHTRANSAKSIActionPerformed

    private void jButtonPERBARUI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPERBARUI2ActionPerformed
        int row = tabeltransaksi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu di tabel transaksi!");
            return;
        }

        String idTransaksi = tabeltransaksi.getValueAt(row, 0).toString();
        String idRoti = tabeltransaksi.getValueAt(row, 1).toString();
        String idPelanggan = tabeltransaksi.getValueAt(row, 2).toString();
        String jumlah = tabeltransaksi.getValueAt(row, 3).toString();
        String total = tabeltransaksi.getValueAt(row, 4).toString();
        String tanggal = tabeltransaksi.getValueAt(row, 5).toString();

        UpdateTRANSAKSI dialog = new UpdateTRANSAKSI(
                this,
                true,
                idTransaksi,
                tanggal,
                jumlah,
                idPelanggan,
                idRoti,
                total
        );
        dialog.setVisible(true);
        showTable();
        showTablePelanggan();
        showTableTransaksi();
    }//GEN-LAST:event_jButtonPERBARUI2ActionPerformed

    private void jButtonHAPUSTRANSAKSIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHAPUSTRANSAKSIActionPerformed
        int[] rows = tabeltransaksi.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih data transaksi dulu!");
            return;
        }

        String[] idTransaksi = new String[rows.length];
        String[] tanggal = new String[rows.length];
        String[] jumlah = new String[rows.length];
        String[] total = new String[rows.length];
        String[] namaRoti = new String[rows.length];
        String[] namaPelanggan = new String[rows.length];

        for (int i = 0; i < rows.length; i++) {

            idTransaksi[i] = tabeltransaksi.getValueAt(rows[i], 0).toString();
            tanggal[i] = tabeltransaksi.getValueAt(rows[i], 1).toString();
            namaPelanggan[i] = tabeltransaksi.getValueAt(rows[i], 2).toString();
            namaRoti[i] = tabeltransaksi.getValueAt(rows[i], 3).toString();
            jumlah[i] = tabeltransaksi.getValueAt(rows[i], 4).toString();
            total[i] = tabeltransaksi.getValueAt(rows[i], 5).toString();
        }

        DeleteTRANSAKSI dialog = new DeleteTRANSAKSI(
                this,
                true,
                idTransaksi,
                tanggal,
                jumlah,
                total,
                namaRoti,
                namaPelanggan
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        showTableTransaksi();
    }//GEN-LAST:event_jButtonHAPUSTRANSAKSIActionPerformed

    private void jButtonUNDUHFILE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUNDUHFILE1ActionPerformed
        try {
            if (tabeltransaksi.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor!");
                return;
            }

            JnaFileChooser chooser = new JnaFileChooser();
            chooser.setMode(JnaFileChooser.Mode.Files);
            chooser.setTitle("Simpan Data Transaksi");
            chooser.setDefaultFileName("Data_Transaksi");
            chooser.setMultiSelectionEnabled(false);
            Window Window = null;
            boolean action = chooser.showOpenDialog(Window);

            if (action) {

                String fileString = chooser.getSelectedFile().getAbsolutePath() + ".csv";
                //                exportToExcel(jTable1, fileString);
                exportToCSV(tabeltransaksi, fileString);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonUNDUHFILE1ActionPerformed

    private void jButtonCETAKPELANGGAN1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCETAKPELANGGAN1ActionPerformed
        try {
            String path = "src/pertemuan12/DATATRANSAKSI.jasper";
            HashMap<String, Object> parameters = new HashMap<>();

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/TUGASPBO12", "postgres", "BISMILLAH");

            JasperPrint jprint = JasperFillManager.fillReport(path, parameters, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setSize(800, 600);
            jviewer.setLocationRelativeTo(this);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonCETAKPELANGGAN1ActionPerformed

    private void jButtonUPLOAD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUPLOAD2ActionPerformed
        imporCsvKeDatabaseTransaksi();
    }//GEN-LAST:event_jButtonUPLOAD2ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tampilan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tampilan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tampilan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tampilan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tampilan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCETAKDATAROTI;
    private javax.swing.JButton jButtonCETAKPELANGGAN;
    private javax.swing.JButton jButtonCETAKPELANGGAN1;
    private javax.swing.JButton jButtonHAPUS;
    private javax.swing.JButton jButtonHAPUS1;
    private javax.swing.JButton jButtonHAPUSTRANSAKSI;
    private javax.swing.JButton jButtonPERBARUI;
    private javax.swing.JButton jButtonPERBARUI1;
    private javax.swing.JButton jButtonPERBARUI2;
    private javax.swing.JButton jButtonTAMBAH;
    private javax.swing.JButton jButtonTAMBAH1;
    private javax.swing.JButton jButtonTAMBAHTRANSAKSI;
    private javax.swing.JButton jButtonUNDUH;
    private javax.swing.JButton jButtonUNDUHFILE;
    private javax.swing.JButton jButtonUNDUHFILE1;
    private javax.swing.JButton jButtonUPLOAD;
    private javax.swing.JButton jButtonUPLOAD1;
    private javax.swing.JButton jButtonUPLOAD2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tabeldataroti;
    private javax.swing.JTable tabelpelanggan;
    private javax.swing.JTable tabeltransaksi;
    // End of variables declaration//GEN-END:variables

    void refreshTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
