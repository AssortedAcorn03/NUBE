//Cliente.java
package cliente;

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

import Main.GamePanel;

public class Cliente extends Frame {
	private TextField usernameField, passwordField;
	private Button loginButton;
	private Label statusLabel;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	public int clientId;

	public Cliente() {
		try {
			// Conectar al servidor antes de mostrar la interfaz
			// InetAddress ip = InetAddress.getByName("10.103.159.219");
			InetAddress ip = InetAddress.getByName("192.168.100.121");
			socket = new Socket(ip, 2555);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error al conectar con el servidor: " + e.getMessage());
			statusLabel.setText("Error de conexión al servidor");
		}

		// Configuración de la ventana
		setLayout(null); // Usamos layout nulo para tener control sobre la posición de los elementos
		setTitle("Cliente - Autenticación");
		setSize(400, 300);

		// Elementos de la interfaz
		Label userLabel = new Label("Usuario:");
		userLabel.setBounds(120, 80, 60, 25);
		add(userLabel);

		usernameField = new TextField(20);
		usernameField.setBounds(190, 80, 120, 25);
		add(usernameField);

		Label passwordLabel = new Label("Contraseña:");
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

		// Acción al presionar el botón de inicio de sesión
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleLoginButtonAction();
			}
		});

		// Evento de tecla Enter para iniciar sesión
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

			// Validar que los campos no estén vacíos
			if (username.isEmpty() || password.isEmpty()) {
				statusLabel.setText("Campos no pueden estar vacíos.");
				return;
			}

			// Enviar credenciales al servidor
			System.out.println("Enviando credenciales al servidor...");
			dos.writeUTF(username);
			dos.writeUTF(password);

			// Leer respuesta del servidor
			String response = dis.readUTF();
			statusLabel.setText(response);
			clientId = dis.readInt(); // Leer el cliente ID
			System.out.println("ID recibido desde el servidor: " + clientId);

			if (response.equals("Autenticación exitosa.")) {
				System.out.println("Autenticación exitosa, iniciando el juego...");
				// Cerrar la ventana de autenticación
				dispose();
				// Iniciar el juego si la autenticación es exitosa
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

	// Método para iniciar el juego después de la autenticación
	private void iniciarJuego(int clientId, DataInputStream dis, DataOutputStream dos) {
		JFrame ventana = new JFrame();
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setResizable(false);
		ventana.setTitle("PCN - Juego");

		GamePanel panelJuego = new GamePanel(clientId, dis, dos);
		// if(dis == null || dos ==null){
		// System.out.println("Error al iniciar el juego Los datos son nulos");
		// }
		// System.out.println("DataInputStream y DataOutPut es enviado de formar
		// correcta");
		ventana.add(panelJuego);
		ventana.pack();

		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);

		panelJuego.iniciarHebraJuego(); // Iniciar el loop del juego
	}

	private void closeConnection() {
		try {
			if (socket != null)
				socket.close();
			if (dis != null)
				dis.close();
			if (dos != null)
				dos.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar la conexión: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		new Cliente();
	}
}
