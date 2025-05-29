package conexionbd;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    public static final String URL = "jdbc:sqlite:C:\\Users\\16150\\OneDrive\\Documents\\Computacion\\S204-2025 I\\Principios de Cómputo en la Nube\\USUARIOS.db";//la ruta de la base de datos
    public static final String USER = "root";//nombre del usuario de la base de datos
    public static final String PSWD = "Win2002Racedb$";//contraseña para acceder a la base de datos
    
    public Connection getConnection(){
        Connection connection = null;//inicializar la conexión como nula
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");//cargar el driver de la base de datos
            connection = (Connection)DriverManager.getConnection(URL, USER, PSWD);//crear la conexión con la ruta, el usuario y la contraseña
            System.out.println("SE ESTABLECIO LA CONEXION");//mensaje de que se estableció la conexión
        }catch(Exception e) {
            System.out.println("Error " + e.getMessage());//mensaje de error
        }
        return connection;
    }
}