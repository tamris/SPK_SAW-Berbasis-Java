/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package View;

import Koneksi.Koneksi;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author tamar
 */
public class menuNormalisasi extends javax.swing.JPanel {

    /**
     * Creates new form menuNormalisasi
     */
   
    public Statement st;
    public ResultSet rs;
    Connection c = Koneksi.sambung_ke_db();
    public menuNormalisasi() {
        initComponents();
        SAW();
        TampilDataNormalisasi();
        TampilDataPreferensi();
    }
    
    
    private void TampilDataNormalisasi() {
        try {

            st = c.createStatement();

            // Query untuk mendapatkan data dari tabel hasil_normalisasi
            String query = "SELECT hn.id_alternatif, a.nama, hn.norm_ipk, hn.norm_tes_pemrograman, hn.norm_kemampuan_mengajar, hn.norm_nilai_referensi, hn.norm_kerjasama "
                    + "FROM hasil_normalisasi hn "
                    + "JOIN alternatif a ON hn.id_alternatif = a.id";

            rs = st.executeQuery(query);

            // Buat model tabel baru
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("No");
            model.addColumn("Nama");
            model.addColumn("IPK");
            model.addColumn("Tes Pemrograman");
            model.addColumn("Kemampuan Mengajar");
            model.addColumn("Nilai Referensi");
            model.addColumn("Kerjasama");

            // Inisialisasi nomor baris
            int no = 1;

            // Hapus data lama di model
            model.setRowCount(0);

            // Ambil data dari ResultSet dan tambahkan ke model tabel
            while (rs.next()) {
                Object[] data = {
                    no++,
                    rs.getString("nama"),
                    rs.getDouble("norm_ipk"),
                    rs.getDouble("norm_tes_pemrograman"),
                    rs.getDouble("norm_kemampuan_mengajar"),
                    rs.getDouble("norm_nilai_referensi"),
                    rs.getDouble("norm_kerjasama")
                };
                model.addRow(data);
            }

            // Tampilkan model di JTable
            tblDataNormalisasi.setModel(model);

            rs.close();
            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void TampilDataPreferensi() {
        try {
            st = c.createStatement();

            // Query untuk mendapatkan data dari tabel hasil_penilaian, diurutkan berdasarkan nilai_akhir DESC
            String query = "SELECT hp.id_alternatif, a.nama, hp.nilai_akhir "
                    + "FROM hasil_penilaian hp "
                    + "JOIN alternatif a ON hp.id_alternatif = a.id "
                    + "ORDER BY hp.nilai_akhir DESC";

            rs = st.executeQuery(query);

            // Buat model tabel baru
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Nama");
            model.addColumn("Nilai Akhir");
            model.addColumn("Peringkat");

            // Inisialisasi nomor peringkat
            int peringkat = 1;

            // Hapus data lama di model
            model.setRowCount(0);

            // Ambil data dari ResultSet dan tambahkan ke model tabel
            while (rs.next()) {
                Object[] data = {
                    rs.getString("nama"),
                    rs.getDouble("nilai_akhir"),
                      peringkat++
                };
                model.addRow(data);
            }

            // Tampilkan model di JTable
            tblPreferensi.setModel(model);

            // Tutup ResultSet dan Statement
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    /**
     *
     * @param kolom
     * @param opsi 1=min, 2=max
     * @return
     */
    private double min_max(String kolom, int opsi) {
        double ipk = 0;
        try {
            st = c.createStatement();

            String q;
            if (opsi == 1) {
                q = "SELECT MIN(" + kolom + ") AS val FROM kriteria";
            } else {
                q = "SELECT MAX(" + kolom + ") AS val FROM kriteria";
            }
            rs = st.executeQuery(q);

            while (rs.next()) {
                ipk = rs.getDouble("val");
            }
            return ipk;
        } catch (SQLException e) {
        }
        return ipk;
    }


    
    /**
     *
     * @param kolom
     * @param opsi 1=cost, 2=benefit
     * @return
     */
    private String cost_benefit(String kolom) {
        String label = "benefit";
        try {

            st = c.createStatement();
            String q = "SELECT label FROM kriteria_label WHERE kriteria='" + kolom + "'";
            rs = st.executeQuery(q);
            while (rs.next()) {
                label = rs.getString(kolom);
            }
            return label;
        } catch (SQLException e) {
        }
        return label;
    }



    private double bobot(String kolom) {
        double bobot = 0;
        try {
            st = c.createStatement();
            String q = "SELECT bobot FROM kriteria_label WHERE kriteria='" + kolom + "'";
            rs = st.executeQuery(q);
            while (rs.next()) {
                bobot = rs.getDouble("bobot");
            }
            return bobot;

        } catch (SQLException e) {
        }
        return bobot;
    }


    private void SAW() {
        String query = "SELECT * FROM kriteria";
        String insertNormQuery = "INSERT INTO hasil_normalisasi (id_alternatif, norm_ipk, norm_tes_pemrograman, norm_kemampuan_mengajar, norm_nilai_referensi, norm_kerjasama) VALUES (?, ?, ?, ?, ?, ?)";
        String insertQuery = "INSERT INTO hasil_penilaian (id_alternatif, nilai_akhir) VALUES (?, ?)";

        try (Connection koneksi = Koneksi.sambung_ke_db(); Statement stmt = koneksi.createStatement(); PreparedStatement psNorm = koneksi.prepareStatement(insertNormQuery); PreparedStatement ps = koneksi.prepareStatement(insertQuery)) {

            // Menghapus semua data di tabel hasil_penilaian dan hasil_normalisasi
            stmt.executeUpdate("TRUNCATE TABLE hasil_penilaian");
            stmt.executeUpdate("TRUNCATE TABLE hasil_normalisasi");

            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int id_alternatif = rs.getInt("id_alternatif");
                    double ipk = rs.getDouble("ipk");
                    double tes_pemrograman = rs.getDouble("tes_pemrograman");
                    double kemampuan_mengajar = rs.getDouble("kemampuan_mengajar");
                    double nilai_referensi = rs.getDouble("nilai_referensi");
                    double kerjasama = rs.getDouble("kerjasama");

                    // Hitung normalisasi
                    double norm_ipk = cost_benefit("ipk").equals("benefit") ? ipk / min_max("ipk", 2) : min_max("ipk", 1) / ipk;
                    double norm_tes_pemrograman = cost_benefit("tes_pemrograman").equals("benefit") ? tes_pemrograman / min_max("tes_pemrograman", 2) : min_max("tes_pemrograman", 1) / tes_pemrograman;
                    double norm_kemampuan_mengajar = cost_benefit("kemampuan_mengajar").equals("benefit") ? kemampuan_mengajar / min_max("kemampuan_mengajar", 2) : min_max("kemampuan_mengajar", 1) / kemampuan_mengajar;
                    double norm_nilai_referensi = cost_benefit("nilai_referensi").equals("benefit") ? nilai_referensi / min_max("nilai_referensi", 2) : min_max("nilai_referensi", 1) / nilai_referensi;
                    double norm_kerjasama = cost_benefit("kerjasama").equals("benefit") ? kerjasama / min_max("kerjasama", 2) : min_max("kerjasama", 1) / kerjasama;

                    // Hitung nilai akhir menggunakan metode SAW
                    double hasil = (norm_ipk * bobot("IPK"))
                            + (norm_tes_pemrograman * bobot("Tes Pemrograman"))
                            + (norm_kemampuan_mengajar * bobot("Kemampuan Mengajar"))
                            + (norm_nilai_referensi * bobot("Nilai Refrensi"))
                            + (norm_kerjasama * bobot("Kerjasama"));

//                    System.out.println("Nilai akhir untuk alternatif dengan ID " + id_alternatif + ": " + hasil);

                    // Simpan hasil normalisasi ke tabel hasil_normalisasi
                    psNorm.setInt(1, id_alternatif);
                    psNorm.setDouble(2, norm_ipk);
                    psNorm.setDouble(3, norm_tes_pemrograman);
                    psNorm.setDouble(4, norm_kemampuan_mengajar);
                    psNorm.setDouble(5, norm_nilai_referensi);
                    psNorm.setDouble(6, norm_kerjasama);
                    psNorm.executeUpdate();

                    // Simpan hasil ke tabel hasil_penilaian
                    ps.setInt(1, id_alternatif);
                    ps.setDouble(2, hasil);
                    ps.executeUpdate();
                }
            }

            TampilDataNormalisasi();
            TampilDataPreferensi();

        } catch (SQLException e) {
            e.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDataNormalisasi = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPreferensi = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel1.setText("Matrix Normalisasi");

        tblDataNormalisasi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Nama", "IPK", "Tes Pemrograman", "Kemampuan Mengajar", "Nilai Referensi", "Kerjasama"
            }
        ));
        jScrollPane1.setViewportView(tblDataNormalisasi);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel2.setText("Nilai Preferensi");

        tblPreferensi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Nilai Akhir", "Peringkat"
            }
        ));
        jScrollPane2.setViewportView(tblPreferensi);

        jButton1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-print-30.png"))); // NOI18N
        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1076, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118))
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // Create a MessageFormat for the header
            MessageFormat header = new MessageFormat("Calon Asisten Dosen");

            // Create a MessageFormat for the footer
            MessageFormat footer = new MessageFormat("Page {0,number,integer}");

            // Print the JTable with the header and footer
            boolean complete = tblPreferensi.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                // Printing was successful
                JOptionPane.showMessageDialog(null, "Printing Complete", "Print", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Printing was cancelled
                JOptionPane.showMessageDialog(null, "Printing Cancelled", "Print", JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException pe) {
            pe.printStackTrace();
            JOptionPane.showMessageDialog(null, "Printing Failed: " + pe.getMessage(), "Print", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDataNormalisasi;
    private javax.swing.JTable tblPreferensi;
    // End of variables declaration//GEN-END:variables
}
