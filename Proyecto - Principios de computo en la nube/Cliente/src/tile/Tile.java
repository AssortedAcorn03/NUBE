package tile;

import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage imagen;
    private boolean colision = false;
    private boolean slowness = false;
    private boolean colisionDisparo = false;
    private boolean escudo = false;
    private boolean velocidad = false;
    private boolean quema = false;
    public BufferedImage getImagen(){
        return this.imagen;
    }

    public void setImagen(BufferedImage imagen){
        this.imagen = imagen;
    }

    public boolean getColision(){
        return this.colision;
    }

    public void setColision(boolean colision){
        this.colision = colision;
    }

    public boolean getSlowness(){
        return this.slowness;
    }

    public void setSlowness(boolean slowness){
        this.slowness = slowness;
    }

    public boolean getColisionDisparo(){
        return this.colisionDisparo;
    }

    public void setColisionDisparo(boolean colisionDisparo){
        this.colisionDisparo = colisionDisparo;
    }

    public boolean getEscudo(){
        return this.escudo;
    }

    public void setEscudo(boolean escudo){
        this.escudo = escudo;
    }

    public boolean getVelocidad(){
        return this.velocidad;
    }

    public void setVelocidad(boolean velocidad){
        this.velocidad = velocidad;
    }

    public boolean getQuema(){
        return this.quema;
    }

    public void setQuema(boolean quema){
        this.quema = quema;
    }
    
}
