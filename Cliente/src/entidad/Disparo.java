package entidad;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import tile.Tile;
import tile.ManejadorTiles;

public class Disparo extends Entidad {
    private int x, y;
    private BufferedImage imagen;
    private boolean activo = true; 
    
    public Disparo(int x, int y, String direccion, BufferedImage imagen) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.imagen = imagen;
        this.velocidad = 1;
    }

    public void update() {
        // Actualiza la posición del disparo según su dirección
        switch (direccion) {
            case "arriba":
                y -= velocidad;
                break;
            case "abajo":
                y += velocidad;
                break;
            case "izquierda":
                x -= velocidad;
                break;
            case "derecha":
                x += velocidad;
                break;
        }

    }

    public void draw(Graphics2D g2, int pantallaX, int pantallaY, int mundoX, int mundoY) {
        int pantallaDisparoX = x - (mundoX - pantallaX);
        int pantallaDisparoY = y - (mundoY - pantallaY);
        g2.drawImage(imagen, pantallaDisparoX, pantallaDisparoY, null);
    }

    public boolean disparoIsCollisioning(int nuevaX, int nuevaY, ManejadorTiles mTi, int tamanioTile) {
        int izquierdaX = nuevaX;
        int derechaX = nuevaX + tamanioTile - 1;
        int superiorY = nuevaY;
        int inferiorY = nuevaY + tamanioTile - 1;
    
        int colIzquierda = izquierdaX / tamanioTile;
        int colDerecha = derechaX / tamanioTile;
        int renSuperior = superiorY / tamanioTile;
        int renInferior = inferiorY / tamanioTile;
    
        return mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colIzquierda)].getColisionDisparo() ||
               mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renSuperior, colDerecha)].getColisionDisparo() ||
               mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colIzquierda)].getColisionDisparo() ||
               mTi.getArregloTiles()[mTi.getCodigoMapaTiles(renInferior, colDerecha)].getColisionDisparo();
    }
    

    public int getX() { return x; }
    public int getY() { return y; }
}
