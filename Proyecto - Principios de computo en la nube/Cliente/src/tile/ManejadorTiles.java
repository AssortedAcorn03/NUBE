package tile;


import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class ManejadorTiles {
    private GamePanel gP;
	private int maxTiles = 10;
	Tile[] arregloTiles;
	private int codigosMapaTiles[][];
    public ManejadorTiles(GamePanel gP){
        this.gP = gP;
        this.arregloTiles = new Tile[maxTiles];
        this.codigosMapaTiles = new int[gP.getMaxRenMundo()][gP.getMaxColMundo()];
        getImagenesTile();
        cargaMapa("/mapas/mundo01.txt");
    }

    public void cargaMapa(String ruta){
        try {
            InputStream mapa = getClass().getResourceAsStream(ruta);
            BufferedReader br = new BufferedReader(new InputStreamReader(mapa));
            int ren = 0, col = 0;
            while (ren < gP.getMaxRenMundo() && col < gP.getMaxColMundo()) {
                String renglonesDatos = br.readLine();
                while (col < gP.getMaxColMundo()) {
                    String codigos[] = renglonesDatos.split(" ");
                    int codigo = Integer.parseInt(codigos[col]);
                    this.codigosMapaTiles[ren][col] = codigo;
                    col++;    
                }
                if(col == gP.getMaxColMundo()){
                    ren++;
                    col=0;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getImagenesTile(){
        try {
            arregloTiles[0] = new Tile();
            arregloTiles[0].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/agua.png"))); //mata
            arregloTiles[0].setQuema(true);
            arregloTiles[1] = new Tile();
            arregloTiles[1].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/arbol.png"))); //ColJ
            arregloTiles[1].setColision(true);
            arregloTiles[2] = new Tile();
            arregloTiles[2].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/arena.png"))); //Slowness
            arregloTiles[2].setSlowness(true);	
            arregloTiles[3] = new Tile();
            arregloTiles[3].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/muro.png"))); //Colision
            arregloTiles[3].setColision(true);
            arregloTiles[3].setColisionDisparo(true);
            arregloTiles[4] = new Tile();
            arregloTiles[4].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/pasto.png"))); //Normal
            arregloTiles[5] = new Tile();
            arregloTiles[5].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/suelo.png"))); //Normal
            arregloTiles[6] = new Tile();
            arregloTiles[6].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/velocidad.png"))); //Colision
            arregloTiles[6].setVelocidad(true);
            arregloTiles[7] = new Tile();
            arregloTiles[7].setImagen(ImageIO.read(getClass().getResourceAsStream("/tiles/escudo.png"))); //Colision
            arregloTiles[7].setEscudo(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void draw(Graphics2D g2){
        int renMundo = 0, colMundo = 0;
		
		while(renMundo < gP.getMaxRenMundo() && colMundo < gP.getMaxColMundo()) {
			int numTile = codigosMapaTiles[renMundo][colMundo];
			int mundoX = colMundo * gP.getTamanioTile();
			int mundoY = renMundo * gP.getTamanioTile();
			int pantallaX = mundoX - gP.getJugador().getX() + gP.getJugador().getPantallaX();
			int pantallaY = mundoY - gP.getJugador().getY() + gP.getJugador().getPantallaY();
			g2.drawImage(arregloTiles[numTile].getImagen(), pantallaX, pantallaY, gP.getTamanioTile(), gP.getTamanioTile(), null);
			colMundo++;
			if(colMundo == gP.getMaxColMundo()) {
				colMundo = 0;
				renMundo++;
			}
		}
    }

    
	public int getCodigoMapaTiles(int ren, int col) {
		return this.codigosMapaTiles[ren][col];
	}

	public void setCodigoMapaTiles(int ren, int col, int valor) {
		this.codigosMapaTiles[ren][col] = valor;
	}

	public Tile[] getArregloTiles() {
		return this.arregloTiles;
	}

	public void setArregloTiles(Tile[] arregloTiles) {
		this.arregloTiles = arregloTiles;
	}
}
