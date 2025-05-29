package cliente;

import Main.GamePanel;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JFrame;

public class Cliente extends Frame {
	private TextField usernameField, passwordField;
	private Button loginButton;
	private Label statusLabel;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	public int clientId;

	public Cliente() {
		// Configuración de la ventana
		setLayout(null);
		setTitle("Cliente - Autenticación");
		setSize(400, 300);

		Label userLabel = new Label("Username:");
		userLabel.setBounds(120, 80, 60, 25);
		add(userLabel);

		usernameField = new TextField(20);
		usernameField.setBounds(190, 80, 120, 25);
		add(usernameField);

		Label passwordLabel = new Label("Password:");
		passwordLabel.setBounds(120, 120, 60, 25);
		add(passwordLabel);

		passwordField = new TextField(20);
		passwordField.setEchoChar('*');
		passwordField.setBounds(190, 120, 120, 25);
		add(passwordField);

		loginButton = new Button("Iniciar Sesión");
		loginButton.setBounds(160, 160, 100, 30);
		add(loginButton);

		statusLabel = new Label();
		statusLabel.setBounds(150, 200, 200, 25);
		add(statusLabel);

		// Conexión al servidor
		try {
			InetAddress ip = InetAddress.getByName("192.168.0.46"); // Cambiar si es remoto
			socket = new Socket(ip, 3000);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("❌ Error al conectar con el servidor: " + e.getMessage());
			statusLabel.setText("Error de conexión al servidor");
		}

		// Acción del botón
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleLoginButtonAction();
			}
		});

		// Tecla Enter
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					handleLoginButtonAction();
				}
			}
		});
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					handleLoginButtonAction();
				}
			}
		});

		// Cerrar la ventana
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeConnection();
				dispose();
				System.exit(0);
			}
		});

		setVisible(true);
	}

	private void handleLoginButtonAction() {
		try {
			String username = usernameField.getText();
			String password = passwordField.getText();

			if (username.isEmpty() || password.isEmpty()) {
				statusLabel.setText("Campos no pueden estar vacíos.");
				return;
			}

			System.out.println("Enviando credenciales al servidor...");
			dos.writeUTF(username);
			dos.writeUTF(password);

			String response = dis.readUTF();
			statusLabel.setText(response);
			clientId = dis.readInt();

			System.out.println("Respuesta del servidor: " + response);
			System.out.println("ID recibido desde el servidor: " + clientId);

			if (response.equals("Autenticación exitosa.")) {
				System.out.println("Autenticación exitosa, iniciando el juego...");
				dispose();
				iniciarJuego(clientId, dis, dos);
			} else {
				System.out.println("Autenticación fallida: " + response);
			}
		} catch (IOException ex) {
			statusLabel.setText("Error de conexión");
			System.out.println("Error durante la autenticación: " + ex.getMessage());
			closeConnection();
		}
	}

	private void iniciarJuego(int clientId, DataInputStream dis, DataOutputStream dos) {
		JFrame ventana = new JFrame();
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setResizable(false);
		ventana.setTitle("PCN - Juego");

		GamePanel panelJuego = new GamePanel(clientId, dis, dos);
		ventana.add(panelJuego);
		ventana.pack();

		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);

		panelJuego.iniciarHebraJuego();
	}

	private void closeConnection() {
		try {
			if (socket != null) socket.close();
			if (dis != null) dis.close();
			if (dos != null) dos.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar la conexión: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new Cliente();
	}
}
