package EditableBufferedReaderMVCMultiline;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import EditableBufferedReader.EscapeSeq;

@SuppressWarnings("deprecation")
class EditableBufferedReader extends BufferedReader{

  MultiLine mlinia;
  Console console;

  public EditableBufferedReader(Reader in){
    super(in);
    this.setRaw(); //amaguem el input del teclat
    mlinia = new MultiLine(getMaxRowCol());
    console = new Console(mlinia);
    mlinia.addObserver(console);
  }

  public int[] getMaxRowCol(){

    int[] maxRowCol = {0,0};
    String strPosX = "";
    String strPosY = "";
    System.out.print("\033[2J");
    System.out.println("\033[9999;9999H");
    System.out.println("\033[6n");
    //output format: ^[[16;224R
    try{
      super.read(); //ESC
      super.read(); //[
      char read = (char) super.read();
      while(read != ';'){
        strPosY += read;
        read = (char) super.read(); 
      }
      read = (char) super.read();
      while(read != 'R'){
        strPosX += read;
        read = (char) super.read();
      }
      maxRowCol[0] = Integer.parseInt(strPosY);
      maxRowCol[1] = Integer.parseInt(strPosX);
      System.out.print("\033[1;1H");
      System.out.print("\033[?1003h"); //activar lectura ratolí
      System.out.print("\033[?1006h"); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )

    } catch(IOException e){
        return null;
    }
    return maxRowCol;
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

    int charac = 0;
    try{

      while(charac != EscapeSeq.CTLRC){
        charac = this.read();
        
        switch (charac) {
          case (-EscapeSeq.LEFT):
            mlinia.move(Line.LEFT);
            break;
          
          case (-EscapeSeq.RIGHT):
            mlinia.move(Line.RIGHT);
            break;
          
          case(-EscapeSeq.END):
            mlinia.move(Line.END);
            break;

          case(-EscapeSeq.START):
            mlinia.move(Line.START);
            break;

          case(-EscapeSeq.UP):
            mlinia.changeLine(MultiLine.UP);
            break;

          case(EscapeSeq.ENTER):
            mlinia.carriageReturn();
            break;

          case(-EscapeSeq.DOWN):
            mlinia.changeLine(MultiLine.DOWN);
            break;

          case(-EscapeSeq.INSERT):
            mlinia.switchOverwrite();
            this.read();
            break;        
          
          case(-EscapeSeq.SUPR):
            this.read();
            mlinia.deleteChar(false);
            break;
          
          case(EscapeSeq.DELETE):
            mlinia.deleteChar(true);
            break;

          case(-EscapeSeq.MOUSE):

            String strT = "";
            char charM = (char) this.read();

            while(charM != ';'){
              strT += charM;
              charM = (char) this.read();
            }  

            String strX = "";
            charM = (char) this.read();
            while(charM != ';'){
              strX += charM;
              charM = (char) this.read();
            }
            
            String strY = "";
            charM = (char) this.read();
            while(charM != 'm' && charM != 'M'){
              strY += charM;
              charM = (char) this.read();
            }
            if(Integer.parseInt(strT) != 0 || charM == 'm'){
              break;
            }

            mlinia.changePosition(Integer.parseInt(strX)-1, Integer.parseInt(strY)-1);
            
            break;
          
          default:
            if(charac > 0 &&   charac != EscapeSeq.ENTER)
              mlinia.addChar((char) charac);
            break;
        }
      }
    } catch(IndexOutOfBoundsException ex){
    } catch(IOException e){
      throw e;
    }
    System.out.print("\033[?1003l"); //desactivar lectura ratolí
    System.out.print("\033[?1006l"); //desactivar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
    this.unsetRaw();


    return mlinia.toString();
  }
}
