package EditableBufferedReader;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SuppressWarnings("deprecation")
public class EditableBufferedReader extends BufferedReader{

  public EditableBufferedReader(Reader in){
    super(in);
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

  public int read() throws IOException{  //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )

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

    Line linia;
    Console console;
    linia = new Line(getMaxCol());
    console = new Console(linia);
    linia.addObserver(console);
    this.setRaw(); //amaguem el input del teclat
    int charac = 0;
    try{

      while(charac != EscapeSeq.ENTER){
        charac = this.read();
        
        switch (charac) {
          case (-EscapeSeq.LEFT):
            linia.moveLeft();
            break;
          
          case (-EscapeSeq.RIGHT):
            linia.moveRight();
            break;
          
          case(-EscapeSeq.END):
          case(-EscapeSeq.DOWN):
            linia.moveEnd();
            break;

          case(-EscapeSeq.START):
          case(-EscapeSeq.UP):
            linia.moveStart();
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

          case(-EscapeSeq.MOUSE)://(click: ^[[<XXX;YYY;ZZZm)

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

            linia.move(-Integer.parseInt(strX));
            
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
    System.out.print(EscapeSeq.DISABLE_MOUSE); //activar lectura ratolí
    System.out.print(EscapeSeq.DISABLE_MOUSE_EX); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
    
    this.unsetRaw();
    return linia.toString();
  }
}
