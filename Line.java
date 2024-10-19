public class Line{

    private int maxChar; //mida màxima de la línia
    private boolean replace; //true si es vol sobreescriure
    private int posX; //posició del teclat
    private char[] line; //línia guardada
    private int numChar; //número de caràcters guardats;

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
            System.out.print(newChar);
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
            System.out.print("\033[1@");
            System.out.print(newChar);
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
            System.out.print("\033[1D");
            System.out.print("\033[1P");
        } else { //si és suprimir
            for(int i = posX; i<numChar-1; i++)
                line[i] = line[i+1];
            System.out.print("\033[1P");     
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
                System.out.print("\033[D");
                posX--;    
                break;
            case RIGHT: //1
                if(posX == numChar)
                    break;
                System.out.print("\033[C");   
                posX++;
                break;
            case END:
                System.out.print("\033["+(numChar+1)+"G");
                posX = numChar;
                break;
            case START:
                posX = 0;
                System.out.print("\033[G");
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

    public void switchOverwrite(){
        replace = !replace;
    }

}
