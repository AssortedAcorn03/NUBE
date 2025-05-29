package cliente;
import java.io.*;
import java.util.Scanner;

public class MessageSender extends Thread {
    private DataOutputStream dos;
    private Scanner input;

    public MessageSender(DataOutputStream dos, Scanner input) {
        this.dos = dos;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            // Enviar mensajes continuamente
            while (true) {
                String messageToSend = input.nextLine();
                dos.writeUTF(messageToSend);
            }
        } catch (IOException e) {
            System.out.println("Error enviando mensaje: " + e.getMessage());
        }
    }
}
