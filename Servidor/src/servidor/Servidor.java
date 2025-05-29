package servidor;

import conexionbd.Connect;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


class MultiClientHandler extends Thread {
    private final DataInputStream dis;//permite la lectura de datos primitivos desde la conexión de red
    private final DataOutputStream dos;//permite la escritura de datos primitivos desde la conexión de red
    private final Socket socket;//mecanismo para realizar la conexión entre el cliente y el servidor

    private final ConcurrentHashMap<Integer, MultiClientHandler> jugadores; // Lista de jugadores autenticados diferenciados por su Id
    private final int clientId;//identificador unico de cada cliente
    private final ArrayList<String> correos;
    // Variables para almacenar posiciones de jugadores
        private boolean isAuthenticated=false;//bandera para verificar si el usuario esta autenticado
        
        public MultiClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos, 
                                  ConcurrentHashMap<Integer, MultiClientHandler> jugadores, 
                                  int clientId, ArrayList<String> correos) {//inicializa las variables
            this.socket = socket;
            this.dis = dis;
            this.dos = dos;
            this.jugadores = jugadores;
            this.clientId = clientId;
            this.correos = correos;
        }
    
        @Override
        public void run() {//metodo que se ejecuta cuando se inicia el hilo
            String Name=null;
            try {//Se lee el nombre de usuario y la contraseña del login del cliente, se verifica si el usuario esta autenticado
                String username = dis.readUTF();
                String password = dis.readUTF();
                int band = 1;
    
                for (int i = 0; i < correos.size(); i++) {//Verificar si el usuario ya está autenticado
                    if (correos.get(i).equals(username)) {
                        band = 0;
                        break;
                    }
                }
    
                // Verificar credenciales
                if (authenticate(username, password) && band == 1) {
                    correos.add(username);
                    dos.writeUTF("Autenticación exitosa.");
                    dos.writeInt(clientId); // Enviar clientId al cliente  en caso de que sí se encuentre en la base de datos
    
                
                    jugadores.put(clientId, this); // Añadir a la lista de jugadores autenticados
                    System.out.println("Cliente #" + clientId + " conectado: " + socket);
                    

                    try {
                        while (true) {
                            int posX = dis.readInt(); // Recibir posición X
                            int posY = dis.readInt(); // Recibir posición Y
                            String direccion = dis.readUTF(); // Recibir dirección
                            System.out.println("Recibo coordenadas del cliente #" + clientId + " X=" + posX + " Y=" + posY + " Dirección=" + direccion);
            
                            // Enviar esta información a todos los demás clientes
                            broadcastPositionUpdate(clientId, posX, posY, direccion);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al recibir información del Cliente #" + clientId + ": " + e.getMessage());
                    }
                    
                } else {
                    dos.writeUTF("Autenticación fallida.");//Mandar mensaje de errror al cliente
                    System.out.println("Autenticación fallida para: " + username);
                }
                
            } catch (IOException e) {
                System.out.println("Error con Cliente #" + clientId + ": " + e.getMessage());
            } finally {
                closeConnection();//cerrar la conexión
                if (correos.contains(Name)) {
                    correos.remove(Name);
                }
            }
        }
    
        private boolean authenticate(String username, String password) {
    try (Connection conn = new Connect().getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "SELECT * FROM usuarios WHERE user = ? AND password = ?")) {

        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        boolean isValid = rs.next();
        rs.close();

        if (isValid) {
            System.out.println("✅ Usuario autenticado: " + username);
        } else {
            System.out.println("❌ Usuario o contraseña incorrectos: " + username);
        }

        return isValid;

    } catch (Exception e) {
        System.out.println("❌ Error de autenticación:");
        e.printStackTrace(); // Esto es vital para detectar problemas
        return false;
    }
}

    

        // Enviar las posiciones actualizadas a los demás clientes
        private void broadcastPositionUpdate(int senderId, int posX, int posY, String direccion) {
            for (MultiClientHandler client : jugadores.values()) {
                if (client.clientId != senderId) { // Omitir si el cliente es el mismo que envió la actualización
                    try {
                        //Escribir el id del cliente, sus coordenadas y dirección
                        client.dos.writeInt(senderId);
                        client.dos.writeInt(posX);
                        client.dos.writeInt(posY);
                        client.dos.writeUTF(direccion);
                        client.dos.flush();
                        System.out.println("Enviando coordenadas del jugador #" + senderId + " a Cliente #" + client.clientId);//Mensaje de ayuda para comprobar qué se está enviando
                    } catch (IOException e) {
                        System.out.println("Error al enviar actualización de posición a Cliente #" + client.clientId + ": " + e.getMessage());//Mensaje de error
                    }
                }
            }
        }
    
        private void closeConnection() {//Método para cerrar la conexión
            try {
                if (isAuthenticated) {
                jugadores.remove(clientId);//Si el jugador está autenticado, se elimina de la lista de jugadores
                
            }
            if (!socket.isClosed()) {//cerrar el socket en caso de que siga abierto
                socket.close();
            }
            dis.close();//cerrar la entrada y salida de datos
            dos.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar recursos para Cliente #" + clientId + ": " + e.getMessage());//Mensaje de error
        }
    }
}

public class Servidor {//Clase principal del servidor

    private static int clientCount = 0;//iniciar la cuenta de clientes en 0
    private static final ConcurrentHashMap<Integer, MultiClientHandler> jugadores = new ConcurrentHashMap<>();//inicializar la lista de jugadores autenticados

    public static void main(String[] args) {
        ArrayList<String> correos = new ArrayList<>();//inicializar la lista de correos
   

        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println("Servidor iniciado. Escuchando en el puerto 3000.");//Mensaje de inicio del servidor en el puerto 2555

            while (true) {//bucle infinito para aceptar conexiones de clientes
                try {
                    Socket socket = serverSocket.accept();//aceptar la conexión del cliente
                    clientCount++; //incrementar el contador de clientes
                    int clientId = clientCount;//asignar dicho contador como clienteId
                    System.out.println("Nuevo cliente conectado: Cliente #" + clientId + " (" + socket + ")");
                        //inicializar la entrada y salida de datos
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            
                    MultiClientHandler clientHandler = new MultiClientHandler(socket, dis, dos, jugadores, clientId, correos);//Crear el hilo para el manejo de clientes
                    //jugadores.put(clientId, clientHandler);
                    clientHandler.start();//iniciar el hilo del cliente
                } catch (IOException e) {
                    System.out.println("Error al aceptar conexión: " + e.getMessage());//Mensaje de error
                }
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());//Mensaje de error
       }
}

}