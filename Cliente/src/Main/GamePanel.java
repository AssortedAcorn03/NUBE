package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import entidad.Jugador;
import tile.ManejadorTiles;

public class GamePanel extends JPanel implements Runnable {
    final int tamanioOriginalTile = 16;
    final int escala = 3;
    final int tamanioTile = tamanioOriginalTile * escala;
    final int maxRenPantalla = 15;
    final int maxColPantalla = 26;
    final int anchoPantalla = tamanioTile * maxColPantalla;
    final int altoPantalla = tamanioTile * maxRenPantalla;
    public int clientId;

    // Variables para el manejo de varios jugadores
    private final ConcurrentHashMap<Integer, Jugador> jugadores = new ConcurrentHashMap<>();
    private DataOutputStream dos;
    private DataInputStream dis;
    // Creacion del juego
    Thread hebraJuego;
    ManejadorTeclas mT = new ManejadorTeclas();
    ManejadorTiles mTi = new ManejadorTiles(this);
    Jugador jugador;
    double FPS = 60;

    // Configuración del mundo
    private final int maxRenMundo = 50;
    private final int maxColMundo = 50;

    public GamePanel(int clienteId, DataInputStream dis, DataOutputStream dos) {
        this.clientId = clienteId;
        this.dos = dos;
        this.dis = dis;
        this.jugador = new Jugador(this, mT, mTi, this.clientId, dos, dis);

        jugadores.put(clienteId, jugador);

        this.setPreferredSize(new Dimension(anchoPantalla, altoPantalla));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(mT);
        this.setFocusable(true);

    }

    public void iniciarHebraJuego() {
        hebraJuego = new Thread(this);
        hebraJuego.start();
    }

    @Override
    public void run() {
        double intervaloDibujo = 1000000000 / FPS;
        double delta = 0;
        long ultimaVez = System.nanoTime();
        long tiempoActual;

        while (hebraJuego != null) {
            tiempoActual = System.nanoTime();
            delta += (tiempoActual - ultimaVez) / intervaloDibujo;
            ultimaVez = tiempoActual;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            recibirActualizacionesServidor();

        }
    }

    // Actualizar el estado del juego (jugador local)
    public void update() {
        jugador.update();
    }

    private void recibirActualizacionesServidor() {
        try {
            while (dis.available() > 0) {
                int playerId = dis.readInt();
                int x = dis.readInt();
                int y = dis.readInt();
                String direccion = dis.readUTF();

                if (playerId != clientId) {
                    agregarOActualizarJugador(playerId, x, y, direccion);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al recibir actualizaciones del servidor: " + e.getMessage());
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // Dibujar tiles
        mTi.draw(g2);

        // Dibujar todos los jugadores
        for (Jugador j : jugadores.values()) {
            j.draw(g2);
        }

        g2.dispose();
    }
    // public void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     Graphics2D g2 = (Graphics2D) g;
    //     mTi.draw(g2);
    //     // Dibujar todos los jugadores
    //     for (Jugador j : jugadores.values()) {
    //         int pixelX = j.getX() - jugador.getX() + getAnchoPantalla() / 2;
    //         int pixelY = j.getY() - jugador.getY() + getAltoPantalla() / 2;
    //     //System.out.println("El valor de X y y son: (" + pixelX + "," + pixelY + ")");
    //     //624 y 360
    //     // Dibujar el jugador en la pantalla
    //     j.draw(g2, pixelX, pixelY);
    //     }
    //     g2.dispose();
    // }

    public void agregarOActualizarJugador(int playerId, int x, int y, String direccion) {
        if (!jugadores.containsKey(playerId)) {
            Jugador nuevoJugador = new Jugador(this, mT, mTi, playerId, dos, dis);
            nuevoJugador.setPosition(x, y);
            nuevoJugador.setDireccion(direccion);
            jugadores.put(playerId, nuevoJugador);
            System.out.println("Jugador agregado con ID: " + playerId + " en posición (" + x + ", " + y + ")");
        } else {
            Jugador jugadorExistente = jugadores.get(playerId);
            jugadorExistente.setPosition(x, y);
            jugadorExistente.setDireccion(direccion);
            System.out.println("Posición actualizada para jugador ID: " + playerId + " a (" + x + ", " + y + ")");
        }
        repaint();
    }

    public int getTamanioTile() {
        return this.tamanioTile;
    }

    public int getAltoPantalla() {
        return this.altoPantalla;
    }

    public int getAnchoPantalla() {
        return this.anchoPantalla;
    }

    public int getMaxColPantalla() {
        return this.maxColPantalla;
    }

    public int getMaxRenPantalla() {
        return this.maxRenPantalla;
    }

    public int getMaxColMundo() {
        return this.maxColMundo;
    }

    public int getMaxRenMundo() {
        return this.maxRenMundo;
    }

    public Jugador getJugador() {
        return jugador;
    }
}
