package entidad;

import java.awt.image.BufferedImage;

public class Entidad {

	protected int mundoX, mundoY;
	protected int velocidad;
    protected BufferedImage arriba1, arriba2, abajo1, abajo2, izquierda1, izquierda2, derecha1, derecha2;
    protected String direccion;
    protected int contadorSprites = 0;
    protected int numeroSprite = 1;
    protected int cambioSprite = 10;
}
