package EditableBufferedReaderMVC;

import EditableBufferedReader.EscapeSeq;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Line extends Observable{

    private int maxChar; //mida màxima de la línia
    private boolean replace; //true si es vol sobreescriure
    private int posX; //posició del teclat
    private StringBuilder line; //línia guardada

    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int START = 2;
    public final static int END = 3;

    public Line(int maxChar) {
        
        this.maxChar = maxChar; 
        line = new StringBuilder(maxChar);
        posX = 0;
        replace = false;

    }

    public void addChar(char newChar) throws IndexOutOfBoundsException {

        if(line.length() == maxChar && !replace)
            throw new IndexOutOfBoundsException();
        
        if(replace && posX < line.length()) {
            line.setCharAt(posX, newChar);
        } else {
            line.insert(posX, newChar);
        }
        posX++;
        
        this.setChanged();
        this.notifyObservers(new EscapeSeq("" + newChar));
    }

    public void deleteChar(boolean delete){

        if(line.length() == 0)
            return;

        if(delete && posX > 0) { // Borrar normal (Backspace)
            posX--;
            line.deleteCharAt(posX);
            this.setChanged();
            this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, posX + 1)));
            this.setChanged();
            this.notifyObservers(new EscapeSeq(EscapeSeq.DEL_SEQ));
        } else if (!delete && posX < line.length()) { // Suprimir (Delete)
            this.setChanged();
            this.notifyObservers(new EscapeSeq(EscapeSeq.DEL_SEQ));
            line.deleteCharAt(posX);
        }
    }

    public void moveLeft() {
        if (posX > 0) {
            posX--;
            this.setChanged();
            this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX + 1))));
        }
    }

    public void moveRight() {
        if (posX < line.length()) {
            posX++;
            this.setChanged();
            this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX + 1))));
        }
    }

    public void moveEnd() {
        posX = line.length();
        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX + 1))));
    }

    public void moveStart() {
        posX = 0;
        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX + 1))));
    }

    public void move(int direction) {
        if (direction < 0) { // si el num. és negatiu (només en el cas del ratolí)
            if (line.length() != 0) {
                if ((-direction) > line.length()) {
                    posX = line.length() + 1;
                } else {
                    posX = -direction;
                }
            }
            this.setChanged();
            this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, posX)));
        }
    }

    public String toString(){
        return line.toString();
    }

    public void switchOverwrite(){
        replace = !replace;
    }

}
