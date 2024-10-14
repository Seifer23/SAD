package EditableBufferedReaderMVCMultiline;

import java.util.Arrays;
import java.util.Observable;
import EditableBufferedReader.EscapeSeq;

@SuppressWarnings("deprecation")
public class MultiLine extends Observable{

    int maxRows, maxChar, posX, currentRow, numLines;
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
        this.currentRow = 0; //Array de linies: 0-maxRow  | posicions del terminal: 1-maxRow+1 :)
        this.numLines = 1;
        this.posX = 0;
        lines[currentRow] = new Line(maxChar);
    }

    public void addChar(char newChar){
        try{
            posX = lines[currentRow].addChar(newChar);
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
                    posX = lines[currentRow].move(direction);
                } catch(IndexOutOfBoundsException e){
                    return;
                }
                break;
        }
        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.MOVE_TO, (posX+1))));
    }

    public void changeLine(int direction){

        int newNumChar;

        switch (direction) {
            case UP:
                if(currentRow == 0)
                    return;
                currentRow--;
                this.setChanged();
                newNumChar = lines[currentRow].numChar;
                if(newNumChar < posX){
                    posX = newNumChar;
                    lines[currentRow].setNumChar(posX);
                }else{
                    lines[currentRow].setNumChar(posX);
                };
                break;
            case DOWN:
                if(currentRow == maxRows-1 || currentRow == numLines-1){
                    return;
                }else{
                    currentRow++;
                    this.setChanged();
                    newNumChar = lines[currentRow].numChar;
                    if(newNumChar < posX){
                        posX = newNumChar;
                        lines[currentRow].setNumChar(posX);
                    }
                    else
                        lines[currentRow].setNumChar(posX);
                }   
                break;
        }

        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.CHANGE_LINE, (currentRow+1), (posX+1))));
    }

    public void carriageReturn(){

        if(currentRow == maxRows - 1)
            return;

        if(currentRow == numLines - 1){

            try{
                this.newLine();
            } catch(IndexOutOfBoundsException e){
                return;
            }
        }

        char[] line = lines[currentRow].toString().toCharArray();
        line = Arrays.copyOfRange(line,posX,line.length);


        for(int i = numLines-2; i>currentRow+1; i--){
            char[] tempChar = lines[currentRow].line;
            lines[currentRow+1].line = tempChar;
        }

        lines[currentRow+1].line = line;
        lines[currentRow+1].numChar = line.length;

        for(int i = 0; i<line.length; i++){

            this.deleteChar(false);
            this.setChanged();
            this.notifyObservers(new EscapeSeq(EscapeSeq.DEL_SEQ));
        }

        posX = 0;
        currentRow++;
        this.setChanged();
        this.notifyObservers(new EscapeSeq(String.format(EscapeSeq.CHANGE_LINE, (currentRow+1), (posX+1))));

        for(char ch : line){
            try{
                posX = lines[currentRow].addChar(ch);
            } catch (IndexOutOfBoundsException e){
                return;
            }
            this.setChanged();
            this.notifyObservers(new EscapeSeq(""+ch));
        }

    }

    public void deleteChar(boolean delete){

        try{
            posX = lines[currentRow].deleteChar(delete);
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


    public void switchOverwrite(){
        replace = !replace;
    }


    public void newLine() throws IndexOutOfBoundsException{

        if(numLines == maxRows)
            throw new IndexOutOfBoundsException();

        lines[numLines] = new Line(maxChar);
        numLines++;
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
