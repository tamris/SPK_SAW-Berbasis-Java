

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Koneksi;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author tamar
 */
public class Koneksi {
      public static Connection sambung_ke_db() {
        try {
            MysqlDataSource m = new MysqlDataSource();
            m.setUser("root");
            m.setPassword("");
            m.setDatabaseName("project_saw");
            m.setServerName("localhost");
            m.setPortNumber(3306);
            m.setServerTimezone("Asia/Jakarta");

            Connection c = m.getConnection();
            System.out.println("Sukses terhubung ke database");
            return c;
            
        } catch (SQLException e) {
            // Penanganan eror
            System.err.println("Gagal terhubung ke database");
            System.err.println("Eror: " + e.getMessage());
        }
        return null;
    }
}
