package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ManejadorTeclas implements KeyListener {
    private boolean teclaArriba, teclaAbajo, teclaIzquierda, teclaDerecha, teclaEspacio;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: teclaArriba = true;
                break;
            case KeyEvent.VK_A: teclaIzquierda = true;
                break;
            case KeyEvent.VK_D: teclaDerecha = true;
                break;
            case KeyEvent.VK_S: teclaAbajo = true;
                break;
            case KeyEvent.VK_Z: teclaEspacio = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: teclaArriba = false;
                break;
            case KeyEvent.VK_A: teclaIzquierda = false;
                break;
            case KeyEvent.VK_D: teclaDerecha = false;
                break;
            case KeyEvent.VK_S: teclaAbajo = false;
                break;
            case KeyEvent.VK_Z: teclaEspacio = false;
                break;
        }
    }

    public boolean getTeclaArriba() {
        return this.teclaArriba;
    }

    public boolean getTeclaAbajo() {
        return this.teclaAbajo;
    }

    public boolean getTeclaIzquierda() {
        return this.teclaIzquierda;
    }

    public boolean getTeclaDerecha() {
        return this.teclaDerecha;
    }

    public boolean getTeclaEspacio() {
        return this.teclaEspacio;
    }
}
