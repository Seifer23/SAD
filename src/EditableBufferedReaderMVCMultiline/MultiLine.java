package EditableBufferedReaderMVCMultiline;

import java.util.Observable;
import EditableBufferedReader.EscapeSeq;

@SuppressWarnings("deprecation")
public class MultiLine extends Observable{

    int maxRows, maxChar, posX, posY, numLines;
    Line[] lines;
    boolean replace;

    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int START = 2;
    public final static int END = 3;
    public final static int DOWN = 4;
    public final static int UP = 5;

    public MultiLine(int[] maxRowChar) {
        
        this.maxRows = maxRowChar[0];
        this.maxChar = maxRowChar[1];
        this.lines = new Line[maxRows];
        this.replace = false;
        this.posY = 0; //Array de linies: 0-maxRow-1  | posicions del terminal: 1-maxRow )
        this.numLines = 1;
        this.posX = 0;
        lines[posY] = new Line(maxChar);
    }

    public void addChar(char newChar){
        try{
            posX = lines[posY].addChar(newChar);
        } catch (IndexOutOfBoundsException e){
            return;
        }

        if(!replace){
            this.setChanged();
            this.notifyObservers(new EscapeSeq(EscapeSeq.ADD_SPACE));
        }
        this.setChanged();
        this.notifyObservers(new EscapeSeq(""+newChar));
    }

    public void move(int direction){
        switch (direction) {
            default:
                try{
                    posX = lines[posY].move(direction);
                } catch(IndexOutOfBoundsException e){
                    return;
                }
                break;
        }
        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX+1))));
    }

    public void deleteChar(boolean delete){

        try{
            posX = lines[posY].deleteChar(delete);
        } catch(IndexOutOfBoundsException e){
            return;
        }
        
        if(delete){
            this.setChanged();
            this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, posX+1)));  
        }
        this.setChanged();
        this.notifyObservers(new EscapeSeq(EscapeSeq.DEL_SEQ));
    }

    public void changeLine(int direction){

        int newNumChar;

        switch (direction) {
            case UP:
                if(posY == 0)
                    return;
                posY--;
                this.setChanged();
                newNumChar = lines[posY].numChar;
                if(newNumChar < posX){
                    posX = newNumChar;
                    lines[posY].setNumChar(posX);
                }else{
                    lines[posY].setNumChar(posX);
                };
                break;
            case DOWN:
                if(posY == maxRows-1 || posY == numLines-1){
                    return;
                }else{
                    posY++;
                    this.setChanged();
                    newNumChar = lines[posY].numChar;
                    if(newNumChar < posX){
                        posX = newNumChar;
                        lines[posY].setNumChar(posX);
                    }
                    else
                        lines[posY].setNumChar(posX);
                }   
                break;
        }

        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.CHANGE_LINE, (posY+1), (posX+1))));
    }

    public void changePosition(int newPosX, int newPosY){
        
        if(newPosX > maxChar-1) //adaptació als límits de la terminal
            newPosX = maxChar-1;
        if(newPosY > numLines-1)
            newPosY = numLines-1;

        posY = newPosY;

        if(lines[posY].numChar < newPosX) //adaptació als límits de la línia
            posX = lines[posY].numChar;
        else
            posX = newPosX;
        lines[posY].posX = posX;

        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.CHANGE_LINE, (posY+1), (posX+1))));
        
    }

    public void carriageReturn(){
        
        char[] displacedChars = new char[maxChar];
        int displacedCharsLength = 0;
        int oldPosY = posY;

        try{
            newLine();
        } catch(IndexOutOfBoundsException e){
            return;
        }

        if (posX<lines[posY].numChar) {
            displacedCharsLength = lines[posY].numChar - posX;
            for(int i = 0; i<displacedCharsLength; i++){
                displacedChars[i] = lines[posY].line[posX+i];
            }
            while(lines[posY].numChar > posX)
                this.deleteChar(false);    
        }

        for(int i = numLines-1; i>oldPosY+1; i--){
            changePosition(0,i);
            for(int j = 0; j<lines[i-1].numChar; j++){
                this.addChar(lines[i-1].line[j]);
            }
            changePosition(0, i-1);
            while(lines[i-1].numChar > 0)
                this.deleteChar(false);
        }

        changePosition(0, oldPosY+1);
        for(int i = 0; i<displacedCharsLength+1; i++){
            this.addChar(displacedChars[i]);
        }
        changePosition(0, oldPosY+1);
    }

    public void switchOverwrite(){
        replace = !replace;
    }

    public void newLine() throws IndexOutOfBoundsException{

        if(this.numLines == maxRows)
            throw new IndexOutOfBoundsException();

        lines[numLines] = new Line(maxChar);
        this.numLines++;
    }

    @Override
    public String toString(){
        String str = "";
        for(Line l : lines){
            if(l == null)
                return str;
            str += l.toString();
            str += "\n";
        }
        return str;
    }
}
