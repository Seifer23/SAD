package EditableBufferedReader;

public class Line{

    //TODO: Implementar suport multilínia

    private int maxChar; //mida màxima de la línia
    private boolean replace; //true si es vol sobreescriure
    private int posX; //posició del teclat
    private char[] line; //línia guardada
    private int numChar; //número de caràcters guardats;

    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int START = 2; //start of line
    public final static int END = 3; //end of line

    public Line(int maxChar) {
        
        this.maxChar = maxChar; 
        line = new char[maxChar];//mida màxima->mida visible
        posX = 0;
        replace = false;

    }

    public void addChar(char newChar) throws IndexOutOfBoundsException {

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
    }

    public void deleteChar(boolean delete){

        if(numChar == 0)
            return;

        if(delete){ //si es és borrar normal
            for(int i = posX-1; i<numChar-1; i++)
                line[i] = line[i+1];
            if(posX != 0)
                posX--;
        } else { //si és suprimir
            for(int i = posX; i<numChar-1; i++)
                line[i] = line[i+1];
        }
        numChar--;
        if(posX>numChar)
        posX=numChar;
    }

    public void move(int direction){
        switch (direction) {
            case LEFT: //0
                if(posX == 0)
                    break;
                System.out.print("\033[1D");
                posX--;    
                break;
            case RIGHT: //1
                if(posX == numChar)
                    break;
                System.out.print("\033[1C");   
                posX++;
                break;
            case END:
                System.out.print("\033["+(numChar-posX)+"C");
                posX = numChar;
                break;
            case START:
                posX = 0;
                System.out.print("\r");
                break;
        }
    }

    public String toString(){
        
        String linia = "";

        for(int i = 0; i < numChar; i++){
            linia += line[i];
        }
        return linia;

    }

    public void printLine(){
        
        System.out.print("\033[2K");
        System.out.print("\r");
        String linia = this.toString();
        System.out.print(linia);
        if(numChar - posX > 0)
            System.out.print("\033["+(numChar-posX)+"D");
    }

    public void switchOverwrite(){
        replace = !replace;
    }

    public char[] getLine(){
        return line;
    }

}