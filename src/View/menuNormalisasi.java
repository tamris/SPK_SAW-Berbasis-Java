/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package View;

import Koneksi.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author tamar
 */
public class menuNormalisasi extends javax.swing.JPanel {

    /**
     * Creates new form menuNormalisasi
     */
   
    
    public menuNormalisasi() {
        initComponents();
        SAW();
        TampilDataNormalisasi();
        TampilDataPenilaian();
    }
    
    
    private void TampilDataNormalisasi() {
        try {
            Connection koneksi = Koneksi.sambung_ke_db();
            Statement st = koneksi.createStatement();

            // Query untuk mendapatkan data dari tabel hasil_normalisasi
            String query = "SELECT hn.id_alternatif, a.nama, hn.norm_ipk, hn.norm_tes_pemrograman, hn.norm_kemampuan_mengajar, hn.norm_nilai_referensi, hn.norm_kerjasama "
                    + "FROM hasil_normalisasi hn "
                    + "JOIN alternatif a ON hn.id_alternatif = a.id";

            ResultSet rs = st.executeQuery(query);

            // Buat model tabel baru
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("No");
            model.addColumn("Nama");
            model.addColumn("Norm IPK");
            model.addColumn("Norm Tes Pemrograman");
            model.addColumn("Norm Kemampuan Mengajar");
            model.addColumn("Norm Nilai Referensi");
            model.addColumn("Norm Kerjasama");

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

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void TampilDataPenilaian() {
        try {
            Connection koneksi = Koneksi.sambung_ke_db();
            Statement st = koneksi.createStatement();

            // Query untuk mendapatkan data dari tabel hasil_penilaian
            String query = "SELECT hp.id_alternatif, a.nama, hp.nilai_akhir "
                    + "FROM hasil_penilaian hp "
                    + "JOIN alternatif a ON hp.id_alternatif = a.id";

            ResultSet rs = st.executeQuery(query);

            // Buat model tabel baru
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("No");
            model.addColumn("Nama");
            model.addColumn("Nilai Akhir");

            // Inisialisasi nomor baris
            int no = 1;

            // Hapus data lama di model
            model.setRowCount(0);

            // Ambil data dari ResultSet dan tambahkan ke model tabel
            while (rs.next()) {
                Object[] data = {
                    no++,
                    rs.getString("nama"),
                    rs.getDouble("nilai_akhir")
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
            Connection koneksi = Koneksi.sambung_ke_db();
            Statement stmt = koneksi.createStatement();
            String query;

            if (opsi == 1) {
                query = "SELECT MIN(" + kolom + ") AS val FROM kriteria";
            } else {
                query = "SELECT MAX(" + kolom + ") AS val FROM kriteria";
            }

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                ipk = rs.getDouble("val");
            }
            return ipk;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ipk;
    }
    
    /**
     * 
     * @param kolom
     * @param opsi 1=cost, 2=benefit 
     * @return 
     */
    private String cost_benefit(String kolom){
        String label="benefit";
        try {
            Connection k = Koneksi.sambung_ke_db();
            Statement st = k.createStatement();
            String q = "SELECT label FROM kriteria_label WHERE kriteria='" + kolom + "'";     
            ResultSet r = st.executeQuery(q);            
            while (r.next()) {
                label = r.getString(kolom);                
            }
            return label;
        } catch (SQLException e) {
        }
        return label;
    }
    
    private double bobot(String kolom){
        double bobot = 0;
        try {
            Connection k = Koneksi.sambung_ke_db();
            Statement st = k.createStatement();
            String q = "SELECT bobot FROM kriteria_label WHERE kriteria='"+kolom+"'";
            ResultSet r = st.executeQuery(q);
            while (r.next()) {                 
                bobot = r.getDouble("bobot");
            }
           return bobot;
           
        } catch (SQLException e) {
        }
        return bobot;
    }
    
    
    private void SAW() {
    try {
        Connection koneksi = Koneksi.sambung_ke_db();
        Statement stmt = koneksi.createStatement();
        
        // Menghapus semua data di tabel hasil_penilaian dan hasil_normalisasi
        stmt.executeUpdate("TRUNCATE TABLE hasil_penilaian");
        stmt.executeUpdate("TRUNCATE TABLE hasil_normalisasi");

        String query = "SELECT * FROM kriteria";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int id = rs.getInt("id");
            int id_alternatif = rs.getInt("id_alternatif");
            double ipk = rs.getDouble("ipk");
            double tes_pemrograman = rs.getInt("tes_pemrograman");
            double kemampuan_mengajar = rs.getInt("kemampuan_mengajar");
            double nilai_referensi = rs.getInt("nilai_referensi");
            double kerjasama = rs.getInt("kerjasama");

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

            System.out.println("Nilai akhir untuk alternatif dengan ID " + id_alternatif + ": " + hasil);

            // Simpan hasil normalisasi ke tabel hasil_normalisasi
            String insertNormQuery = "INSERT INTO hasil_normalisasi (id_alternatif, norm_ipk, norm_tes_pemrograman, norm_kemampuan_mengajar, norm_nilai_referensi, norm_kerjasama) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = koneksi.prepareStatement(insertNormQuery)) {
                pstmt.setInt(1, id_alternatif);
                pstmt.setDouble(2, norm_ipk);
                pstmt.setDouble(3, norm_tes_pemrograman);
                pstmt.setDouble(4, norm_kemampuan_mengajar);
                pstmt.setDouble(5, norm_nilai_referensi);
                pstmt.setDouble(6, norm_kerjasama);
                pstmt.executeUpdate();
            }

            // Simpan hasil ke tabel hasil_penilaian
            String insertQuery = "INSERT INTO hasil_penilaian (id_alternatif, nilai_akhir) VALUES (?, ?)";
            try (PreparedStatement pstmt = koneksi.prepareStatement(insertQuery)) {
                pstmt.setInt(1, id_alternatif);
                pstmt.setDouble(2, hasil);
                pstmt.executeUpdate();
            }
        }
        
        TampilDataNormalisasi();
        TampilDataPenilaian();

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
                "No", "Nama", "Nilai Akhir"
            }
        ));
        jScrollPane2.setViewportView(tblPreferensi);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1076, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDataNormalisasi;
    private javax.swing.JTable tblPreferensi;
    // End of variables declaration//GEN-END:variables
}
