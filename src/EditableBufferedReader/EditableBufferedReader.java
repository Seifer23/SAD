package EditableBufferedReader;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class EditableBufferedReader extends BufferedReader{

  Line linia;

  public EditableBufferedReader(Reader in){
    super(in);
    linia = new Line(getMaxCol());
  }

  public int getMaxCol(){

    String[] getCol = new String[] {"sh", "-c", "tput cols < /dev/tty"};
    try {
      Process p = new ProcessBuilder(getCol).start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String maxColStr = reader.readLine();
      reader.close();
      return Integer.parseInt(maxColStr.trim());

    } catch (Exception e) {
        return 0;
    }
  }

  public void setRaw(){

      String[] rawCom = new String[] {"sh", "-c", "stty -echo raw < /dev/tty"};

      try{
          new ProcessBuilder(rawCom).start();
      } catch (IOException ex){
          System.out.println("Error in setRaw()");
      }
  }

  public void unsetRaw(){

    String[] cookedCom = new String[] {"sh", "-c", "stty echo cooked < /dev/tty"};

    try{
      new ProcessBuilder(cookedCom).start();
    }catch(IOException ex){
      System.out.println("Error in unsetRaw()");
    }
  }

  public int read() throws IOException{

    try{
      int charac = super.read();
      if(charac != EscapeSeq.ESC)
        return charac;
      charac = super.read();
      if(charac == EscapeSeq.BRACKET)
        return -super.read();
    }catch(IOException e){
      throw e;
    }
    return EscapeSeq.EMPTY;
  }

  public String readLine() throws IOException{


    this.setRaw(); //amaguem el input del teclat
    int charac = 0;
    try{

      while(charac != EscapeSeq.ENTER){
        charac = this.read();
        
        switch (charac) {
          case (-EscapeSeq.LEFT):
            linia.move(Line.LEFT);
            break;
          
          case (-EscapeSeq.RIGHT):
            linia.move(Line.RIGHT);
            break;
          
          case(-EscapeSeq.END):
          case(-EscapeSeq.DOWN):
            linia.move(Line.END);
            break;

          case(-EscapeSeq.START):
          case(-EscapeSeq.UP):
            linia.move(Line.START);
            break;

          case(-EscapeSeq.INSERT):
            linia.switchOverwrite();
            this.read();
            break;        
          
          case(-EscapeSeq.SUPR):
            this.read();
            linia.deleteChar(false);
            break;
          
          case(EscapeSeq.DELETE):
            linia.deleteChar(true);
            break;

          default:
            if(charac > 0 && charac != EscapeSeq.ENTER)
              linia.addChar((char) charac);
            break;
        }
      }
    } catch(IndexOutOfBoundsException ex){
    } catch(IOException e){
      throw e;
    }

    this.unsetRaw();
    return linia.toString();
  }
}
