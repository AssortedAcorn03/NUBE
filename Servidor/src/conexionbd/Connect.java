package conexionbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Connect {
    // CORRECTA RUTA JDBC PARA SQLite (nota: no termina con '/')
    public static final String URL = "jdbc:sqlite:C:/Users/alexv/Documents/nube/NUBE/USUARIOS.db";

    public Connection getConnection() {
    try {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection(URL);
        System.out.println("✅ Conexión establecida con SQLite");
        System.out.println("Ruta de la base de datos: " + URL);
        // TEST: imprimir tabla
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios");
        while (rs.next()) {
            System.out.println("Usuario: " + rs.getString("user"));
        }
        rs.close();
        stmt.close();

        return connection;
    } catch (Exception e) {
        System.out.println("❌ Error al conectar con la base de datos: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
    }

}
