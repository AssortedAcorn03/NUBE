package cliente;

import java.io.*;

public class MessageReceiver extends Thread {
    private DataInputStream dis;

    public MessageReceiver(DataInputStream dis) {
        this.dis = dis;
    }

    @Override
    public void run() {
        try {
            // Recibir mensajes continuamente
            while (true) {
                String receivedMessage = dis.readUTF();
                System.out.println("Mensaje recibido: " + receivedMessage);
            }
        } catch (IOException e) {
            System.out.println("Error recibiendo mensaje: " + e.getMessage());
        }
    }
}
