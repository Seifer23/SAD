package EditableBufferedReaderMVCMultiline;

public class Line{

    private int maxChar; //mida màxima de la línia
    private boolean replace; //true si es vol sobreescriure
    public int posX; //posició del teclat
    public char[] line; //línia guardada
    public int numChar; //número de caràcters guardats;

    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int START = 2;
    public final static int END = 3;

    public Line(int maxChar) {
        
        this.maxChar = maxChar; 
        line = new char[maxChar];
        posX = 0;
        replace = false;

    }

    public int addChar(char newChar) throws IndexOutOfBoundsException {

        if(numChar == maxChar && !replace)
            throw new IndexOutOfBoundsException();
        
        if(replace){

            if(posX == numChar){
                if(numChar == maxChar)
                    throw new IndexOutOfBoundsException();
                line[posX] = newChar;
                posX = numChar += 1;
            } else{
                line[posX] = newChar;
                posX++; 
            }
        }else {
            if(posX == numChar){
                line[posX] = newChar;
                posX = numChar += 1;
            } else {
                for(int i = maxChar - 1; i>posX; i--){
                    line[i] = line[i-1];
                }
                line[posX] = newChar;
                posX++;
                numChar++;
            }
        }
        return posX;
    }

    public int deleteChar(boolean delete) throws IndexOutOfBoundsException{

        if(numChar == 0)
            throw new IndexOutOfBoundsException();

        if(delete){ //si es és borrar normal
            for(int i = posX-1; i<numChar-1; i++)
                line[i] = line[i+1];
            if(posX != 0)
                posX--;
        } else { //si és suprimir
            for(int i = posX; i<numChar-1; i++)
                line[i] = line[i+1];;
        }
        numChar--;
        if(posX>numChar)
        posX=numChar;

        return posX;
    }

    public int move(int direction) throws IndexOutOfBoundsException{
        switch (direction) {
            case LEFT: //0
                if(posX == 0)
                    throw new IndexOutOfBoundsException();
                posX--;    
                break;
            case RIGHT: //1
                if(posX == numChar)
                    throw new IndexOutOfBoundsException(); 
                posX++;
                break;
            case END:
                posX = numChar;
                break;
            case START:
                posX = 0;
                break;
            default: //cal refer el suport per el ratolí en multilínia
                break;
        }
        return posX;
    }

    public String toString(){
        
        String linia = "";

        for(int i = 0; i < numChar; i++){
            linia += line[i];
        }
        return linia;

    }

    public void switchOverwrite(){
        replace = !replace;
    }

    public void setNumChar(int newPosX) {
        this.posX = newPosX;
    }

    

}