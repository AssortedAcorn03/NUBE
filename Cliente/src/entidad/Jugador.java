package entidad;

import Main.GamePanel;
import Main.ManejadorTeclas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import tile.ManejadorTiles;

public class Jugador extends Entidad {

    public static final int POS_SUPERIOR_IZQUIERDA = 1;
    public static final int POS_SUPERIOR_DERECHA = 2;
    public static final int POS_INFERIOR_IZQUIERDA = 3;
    public static final int POS_INFERIOR_DERECHA = 4;

    private DataOutputStream dos;
    private DataInputStream dis;
    private final int pantallaX, pantallaY;
    private GamePanel gP;
    private ManejadorTeclas mT;
    private ManejadorTiles mTi;
    public int clientId;

    private List<Disparo> disparos;
    private BufferedImage imagenDisparo;

    private int vidas = 3;
    private BufferedImage corazon3, corazon2, corazon1, corazon0;
    private boolean estaMuerto = false;

    private long tiempoUltimoDisparo = 0;
    private final long intervaloDisparo = 500;

    private boolean escudoActivo = false;
    private boolean velocidadActiva = false;

    private long tiempoEscudoInicio;
    private long tiempoVelocidadInicio;

    private final long duracionPoderes = 10 * 1000;

    private boolean estaEnLava = false;
    private long tiempoInicioLava;
    private final long intervaloQuemadura = 1500;

    public Jugador(GamePanel gP, ManejadorTeclas mT, ManejadorTiles mTi, int clientId, DataOutputStream dos,
            DataInputStream dis) {
        this.gP = gP;
        this.mT = mT;
        this.mTi = mTi;
        this.clientId = clientId;
        this.dos = dos;
        this.dis = dis;
        this.pantallaX = gP.getAnchoPantalla() / 2 - (gP.getTamanioTile() / 2);
        this.pantallaY = gP.getAltoPantalla() / 2 - (gP.getTamanioTile() / 2);

        this.disparos = new ArrayList<>();
        getSpritesJugador();
        asignarPosicion(clientId);
        // cargarImagenDisparo();
        cargarImagenVidas();
        // enviarPosicionContinuamente();
        // recibirPosicionesDeOtrosJugadores();
    }

    private void asignarPosicion(int clientId) {
        System.out.println(clientId + " Este es el clienteId desde la funcion asignarPosicion");
        switch ((clientId % 5)) { // Modulo para asignar la posición en base al clientId
            case POS_SUPERIOR_IZQUIERDA:
                this.mundoX = 145;
                System.out.println(mundoX);
                this.mundoY = 145;
                System.out.println(mundoY);
                this.velocidad = 4;
                this.direccion = "derecha";
                break;
            case POS_SUPERIOR_DERECHA:
                // this.mundoX = 200;
                this.mundoX = 2180;
                System.out.println(mundoX);
                this.mundoY = 145;
                System.out.println(mundoY);
                this.velocidad = 4;
                this.direccion = "izquierda";
                break;
            case POS_INFERIOR_IZQUIERDA:
                this.mundoX = 145;
                System.out.println(mundoX);
                this.mundoY = 2180;
                System.out.println(mundoY);
                this.velocidad = 4;
                this.direccion = "derecha";
                break;
            case POS_INFERIOR_DERECHA:
                this.mundoX = 2180;
                System.out.println(mundoX);
                this.mundoY = 2180;
                System.out.println(mundoY);
                this.velocidad = 4;
                this.direccion = "izquierda";
                break;
        }
    }

    private void enviarPosicion() {
        try {
            dos.writeInt(mundoX);
            dos.writeInt(mundoY);
            dos.writeUTF(direccion);
            dos.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar posición: " + e.getMessage());
        }
    }

    // Enviar y recibir posiciones de manera continua
    public void enviarPosicionContinuamente() {
        new Thread(() -> {
            while (true) {
                try {
                    dos.writeInt(this.mundoX); // Enviar X
                    dos.writeInt(this.mundoY); // Enviar Y
                    dos.writeUTF(this.direccion); // Enviar dirección
                    dos.flush();
                    System.out.println("Enviando mi posición X=" + this.mundoX + " Y=" + this.mundoY + " direccion= "
                            + this.direccion);
                    Thread.sleep(100); // Enviar cada 100ms
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error al enviar posición: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    public void recibirPosicionesDeOtrosJugadores() {
        new Thread(() -> {
            while (true) {
                try {
                    int idJugador = dis.readInt(); // ID del jugador que envía la posición
                    int posX = dis.readInt(); // X recibida
                    int posY = dis.readInt(); // Y recibida
                    String direccion = dis.readUTF(); // Dirección recibida
                    System.out.println("Recibo coordenadas del jugador #" + idJugador + " X=" + posX + " Y=" + posY);

                    // Actualizar en el mapa de jugadores en GamePanel
                    gP.agregarOActualizarJugador(idJugador, posX, posY, direccion);
                } catch (IOException e) {
                    System.out.println("Error al recibir posiciones de otros jugadores: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    public void disminuirVida() {
        if (vidas > 0) {
            if (escudoActivo) {
                pierdeEscudo();
                return;
            }
            vidas--;
        }

        if (vidas == 0) {
            morir();
        }
    }

    private void pierdeEscudo() {
        escudoActivo = false;
        getSpritesJugador();
    }

    private void morir() {
        estaMuerto = true;
    }

    public void getSpritesJugador() {
        try {
            if (escudoActivo) {
                this.arriba1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba1Escudo.png"));
                this.arriba2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba2Escudo.png"));
                this.abajo1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo1Escudo.png"));
                this.abajo2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo2Escudo.png"));
                this.izquierda1 = ImageIO
                        .read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda1Escudo.png"));
                this.izquierda2 = ImageIO
                        .read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda2Escudo.png"));
                this.derecha1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha1Escudo.png"));
                this.derecha2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha2Escudo.png"));
            } else if (estaEnLava) {
                this.arriba1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba1Lava.png"));
                this.arriba2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba2Lava.png"));
                this.abajo1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo1Lava.png"));
                this.abajo2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo2Lava.png"));
                this.izquierda1 = ImageIO
                        .read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda1Lava.png"));
                this.izquierda2 = ImageIO
                        .read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda2Lava.png"));
                this.derecha1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha1Lava.png"));
                this.derecha2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha2Lava.png"));
            } else {
                this.arriba1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba1.png"));
                this.arriba2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverArriba2.png"));
                this.abajo1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo1.png"));
                this.abajo2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverAbajo2.png"));
                this.izquierda1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda1.png"));
                this.izquierda2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverIzquierda2.png"));
                this.derecha1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha1.png"));
                this.derecha2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/moverDerecha2.png"));
            }

        } catch (Exception e) {
            System.out.println("Error al cargar las imágenes del jugador: " + e.getMessage());
        }
    }

    private void cargarImagenDisparo() {
        if (direccion == "arriba") {
            try {
                imagenDisparo = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/disparoArriba.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (direccion == "abajo") {
            try {
                imagenDisparo = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/disparoAbajo.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (direccion == "izquierda") {
            try {
                imagenDisparo = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/disparoIzquierda.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (direccion == "derecha") {
            try {
                imagenDisparo = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/disparoDerecha.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarImagenVidas() {
        try {
            corazon3 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/corazon3.png"));
            corazon2 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/corazon2.png"));
            corazon1 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/corazon1.png"));
            corazon0 = ImageIO.read(getClass().getResourceAsStream("/spritesjugador/corazon0.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return this.mundoX;
    }

    public int getY() {
        return this.mundoY;
    }

    public int getVelocidad() {
        return this.velocidad;
    }

    public void setX(int valor) {
        this.mundoX = valor;
    }

    public void setY(int valor) {
        this.mundoY = valor;
    }

    public void update() {

        if (estaMuerto) {
            return;
        }
        if (velocidadActiva && !estaSlowness(this.mundoX, this.mundoY)) {
            this.velocidad = 12;
        }

        if (mT.getTeclaArriba() || mT.getTeclaAbajo() || mT.getTeclaIzquierda() || mT.getTeclaDerecha()) {
            int nuevaX = this.mundoX;
            int nuevaY = this.mundoY;

            if (mT.getTeclaArriba()) {
                nuevaY -= this.velocidad;
                this.direccion = "arriba";
            } else if (mT.getTeclaAbajo()) {
                nuevaY += this.velocidad;
                this.direccion = "abajo";
            } else if (mT.getTeclaIzquierda()) {
                nuevaX -= this.velocidad;
                this.direccion = "izquierda";
            } else if (mT.getTeclaDerecha()) {
                nuevaX += this.velocidad;
                this.direccion = "derecha";
            }
            enviarPosicion();
            this.mundoX = !estaChocanding(nuevaX, nuevaY) ? nuevaX : this.mundoX;
            this.mundoY = !estaChocanding(nuevaX, nuevaY) ? nuevaY : this.mundoY;

            if (estaSlowness(nuevaX, nuevaY)) {
                this.velocidad = 2;
            } else {
                this.velocidad = 6;
            }

            if (tieneVelocidad(nuevaX, nuevaY) && !velocidadActiva) {
                velocidadActiva = true;
                tiempoVelocidadInicio = System.currentTimeMillis();

                System.out.println("¡Velocidad activada!");
            }

            // Comprobar si el jugador ha tocado un tile de escudo
            if (tieneEscudo(nuevaX, nuevaY) && !escudoActivo) {
                escudoActivo = true;
                getSpritesJugador();
                tiempoEscudoInicio = System.currentTimeMillis();
                System.out.println("¡Escudo activado!");
            }

            // Verificar si ya pasaron 10 segundos desde que se activó la velocidad
            if (velocidadActiva && (System.currentTimeMillis() - tiempoVelocidadInicio > duracionPoderes)) {
                velocidadActiva = false;
                System.out.println("Velocidad terminada.");
            }

            // Verificar si ya pasaron 10 segundos desde que se activó el escudo
            if (escudoActivo && (System.currentTimeMillis() - tiempoEscudoInicio > duracionPoderes)) {
                escudoActivo = false;
                System.out.println("Escudo terminado.");
                getSpritesJugador();
            }

            if (seEstaQuemando(nuevaX, nuevaY)) {
                if (!estaEnLava) {
                    // Si el jugador acaba de entrar en la lava, inicializar el temporizador
                    estaEnLava = true;
                    getSpritesJugador();
                    tiempoInicioLava = System.currentTimeMillis();
                } else {
                    // Si el jugador ya está en la lava, verificar si han pasado 2 segundos
                    long tiempoActual = System.currentTimeMillis();
                    if (tiempoActual - tiempoInicioLava >= intervaloQuemadura) {

                        disminuirVida();
                        tiempoInicioLava = tiempoActual; // Reiniciar el temporizador para seguir quemando cada 2
                                                         // segundos
                    }
                }
            } else {
                estaEnLava = false;
                getSpritesJugador();
            }

            this.contadorSprites++;
            if (this.contadorSprites > this.cambioSprite) {
                this.numeroSprite = this.numeroSprite == 1 ? 2 : 1;
                this.contadorSprites = 0;
            }
        }

        // Activar disparo con la tecla espacio
        if (mT.getTeclaEspacio()) {
            disparar();
        }

        // Actualizar todos los disparos
        actualizarDisparos();

        for (Disparo disparo : disparos) {
            if (colisionaConDisparo(disparo)) {
                disminuirVida(); // Disminuye una vida si colisiona con un disparo
                disparos.remove(disparo); // Elimina el disparo después de la colisión
                break; // Sal del bucle después de la colisión para evitar errores de iteración
            }
        }
    }

    private boolean colisionaConDisparo(Disparo disparo) {
        // Verificar si las coordenadas del disparo coinciden con las del jugador
        Rectangle jugadorHitbox = new Rectangle(mundoX, mundoY, gP.getTamanioTile(), gP.getTamanioTile());
        Rectangle disparoHitbox = new Rectangle(disparo.getX(), disparo.getY());

        return jugadorHitbox.intersects(disparoHitbox);
    }

    private void disparar() {
        long tiempoActual = System.currentTimeMillis();
        int posX = this.mundoX;
        int posY = this.mundoY;

        // Ajuste inicial del disparo según la dirección del jugador
        switch (direccion) {
            case "arriba":
                posY -= gP.getTamanioTile();
                break;
            case "abajo":
                posY += gP.getTamanioTile();
                break;
            case "izquierda":
                posX -= gP.getTamanioTile();
                break;
            case "derecha":
                posX += gP.getTamanioTile();
                break;
        }

        // Crear y agregar un nuevo disparo a la lista
        if (tiempoActual - tiempoUltimoDisparo >= intervaloDisparo) {
            // Crear un nuevo disparo
            cargarImagenDisparo();

            Disparo nuevoDisparo = new Disparo(posX, posY, direccion, imagenDisparo); // Ajusta según cómo creas los
                                                                                      // disparos
            disparos.add(nuevoDisparo);

            // Actualizar el tiempo del último disparo
            tiempoUltimoDisparo = tiempoActual;
        }

    }

    private void actualizarDisparos() {
        // Actualizar cada disparo y eliminar los que están fuera de los límites del
        // mundo
        disparos.removeIf(disparo -> {
            disparo.update();
            // Eliminar disparo si sale de los límites del mundo, no de la pantalla
            return disparo.getX() < 0 || disparo.getX() > gP.getMaxColMundo() * gP.getTamanioTile() ||
                    disparo.getY() < 0 || disparo.getY() > gP.getMaxRenMundo() * gP.getTamanioTile();

        });
    }

    public void draw(Graphics2D g2) {
        if (!estaMuerto) {
            BufferedImage sprite = null;

            switch (this.direccion) {
                case "arriba":
                    sprite = (this.numeroSprite == 1) ? this.arriba1 : this.arriba2;
                    break;
                case "abajo":
                    sprite = (this.numeroSprite == 1) ? this.abajo1 : this.abajo2;
                    break;
                case "izquierda":
                    sprite = (this.numeroSprite == 1) ? this.izquierda1 : this.izquierda2;
                    break;
                case "derecha":
                    sprite = (this.numeroSprite == 1) ? this.derecha1 : this.derecha2;
                    break;
            }

            if (sprite == null) {
                System.out.println("Advertencia: El sprite es null para la dirección: " + this.direccion);
                return; // Salir del método si no hay sprite disponible
            }

            int pantallaX = mundoX - gP.getJugador().mundoX + gP.getAnchoPantalla() / 2;
            int pantallaY = mundoY - gP.getJugador().mundoY + gP.getAltoPantalla() / 2;

            // Dibujar el sprite del jugador
            g2.drawImage(sprite, pantallaX, pantallaY, gP.getTamanioTile(), gP.getTamanioTile(), null);
            g2.setColor(Color.WHITE);
            g2.drawString("Jugador " + clientId, pantallaX, pantallaY - 10);

            // Dibujar todos los disparos
            List<Disparo> disparosAEliminar = new ArrayList<>();

            for (Disparo disparo : disparos) {
                disparo.draw(g2, this.pantallaX, this.pantallaY, this.mundoX, this.mundoY);

                if (disparo.disparoIsCollisioning(disparo.getX(), disparo.getY(), mTi, gP.getTamanioTile())) {
                    disparosAEliminar.add(disparo);
                }
            }

            // Eliminar los disparos que colisionaron
            disparos.removeAll(disparosAEliminar);

            if (mT.getTeclaEspacio()) {
                System.out.println("Espacio presionado");
                disparar();
            }

            BufferedImage corazonActual = null;
            switch (vidas) {
                case 3:
                    corazonActual = corazon3;
                    break;
                case 2:
                    corazonActual = corazon2;
                    break;
                case 1:
                    corazonActual = corazon1;
                    break;
                case 0:
                    corazonActual = corazon0;
                    break;
            }

            // Dibujar el corazón en la parte superior de la pantalla
            if (corazonActual != null) {
                int xPos = gP.getAnchoPantalla() - corazonActual.getWidth() - 10;
                int yPos = 10;
                g2.drawImage(corazonActual, xPos, yPos, null);
            }
        }

        if (estaMuerto) {
            // Aplicar un filtro rojo en toda la pantalla
            Color filtroRojo = new Color(255, 0, 0, 100);
            g2.setColor(filtroRojo);
            g2.fillRect(0, 0, gP.getWidth(), gP.getHeight());

            // Dibujar el mensaje de muerte
            g2.setFont(new Font("Arial", Font.BOLD, 60));
            g2.setColor(Color.WHITE);
            String mensajeMuerte = "Haz muerto";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int x = (gP.getWidth() - fm.stringWidth(mensajeMuerte)) / 2;
            int y = gP.getHeight() / 2;
            g2.drawString(mensajeMuerte, x, y);

            // Opción para cargar una imagen de muerte (comentado)
            // BufferedImage imagenMuerte = cargarImagen("/path/to/muerte.png");
            // int imagenX = (gP.getWidth() - imagenMuerte.getWidth()) / 2;
            // int imagenY = (gP.getHeight() - imagenMuerte.getHeight()) / 2;
            // g2.drawImage(imagenMuerte, imagenX, imagenY, null);
        }
    }

    public boolean estaChocanding(int nuevaX, int nuevaY) {
        int tileSize = gP.getTamanioTile();

        int izquierdaX = nuevaX;
        int derechaX = nuevaX + gP.getTamanioTile() - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + gP.getTamanioTile() - 1;

        int colIzquierda = izquierdaX / tileSize;
        int colDerecha = derechaX / tileSize;
        int renSuperior = superiorY / tileSize;
        int renInferior = inferiorY / tileSize;

        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getColision() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getColision() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getColision() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getColision() ? true : false;

    }

    public boolean estaSlowness(int nuevaX, int nuevaY) {
        int tileSize = gP.getTamanioTile();

        int izquierdaX = nuevaX;
        int derechaX = nuevaX + gP.getTamanioTile() - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + gP.getTamanioTile() - 1;

        int colIzquierda = izquierdaX / tileSize;
        int colDerecha = derechaX / tileSize;
        int renSuperior = superiorY / tileSize;
        int renInferior = inferiorY / tileSize;

        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getSlowness() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getSlowness() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getSlowness() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getSlowness() ? true : false;

    }

    public boolean tieneEscudo(int nuevaX, int nuevaY) {
        int tileSize = gP.getTamanioTile();

        int izquierdaX = nuevaX;
        int derechaX = nuevaX + gP.getTamanioTile() - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + gP.getTamanioTile() - 1;

        int colIzquierda = izquierdaX / tileSize;
        int colDerecha = derechaX / tileSize;
        int renSuperior = superiorY / tileSize;
        int renInferior = inferiorY / tileSize;

        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getEscudo() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getEscudo() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getEscudo() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getEscudo() ? true : false;
    }

    public boolean tieneVelocidad(int nuevaX, int nuevaY) {
        int tileSize = gP.getTamanioTile();

        int izquierdaX = nuevaX;
        int derechaX = nuevaX + gP.getTamanioTile() - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + gP.getTamanioTile() - 1;

        int colIzquierda = izquierdaX / tileSize;
        int colDerecha = derechaX / tileSize;
        int renSuperior = superiorY / tileSize;
        int renInferior = inferiorY / tileSize;

        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getVelocidad() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getVelocidad() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getVelocidad() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getVelocidad() ? true : false;
    }

    public boolean seEstaQuemando(int nuevaX, int nuevaY) {
        int tileSize = gP.getTamanioTile();

        int izquierdaX = nuevaX;
        int derechaX = nuevaX + gP.getTamanioTile() - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + gP.getTamanioTile() - 1;

        int colIzquierda = izquierdaX / tileSize;
        int colDerecha = derechaX / tileSize;
        int renSuperior = superiorY / tileSize;
        int renInferior = inferiorY / tileSize;

        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getQuema() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getQuema() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getQuema() ||
                mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getQuema() ? true : false;
    }

    public void setPosition(int x, int y) {
        this.mundoX = x;
        this.mundoY = y;
    }

    public int getPantallaX() {
        return this.pantallaX;
    }

    public int getPantallaY() {
        return this.pantallaY;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
