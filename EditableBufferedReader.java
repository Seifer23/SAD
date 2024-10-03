package SAD;

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

    String[] getCol = new String[] {"sh", "-c", "tput cols"};
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

  public int read(){
     
    try{

      int charac = super.read();
      if(charac != EscapeSeq.ESC){ // Si el següent caràcter no comença per ^[      (ESC)
        if(charac == 13){
          return EscapeSeq.ENTER;
        }
        return charac;  //enviar el caràcter a readLine()
      } //si és ^[ tenim dues opcions: EOT i [
      charac = super.read();
      if(charac == EscapeSeq.BRACKET){ // si la lletra següent és [ 
        charac = super.read();
        return charac+1000; //Sumem 1000 al caràcter de després de ^[[
      }


    } catch (IOException ex){
      return -1;
    }
    return -1; //TODO: revisar que el fallback funcioni
  }

  public String readLine(){


    this.setRaw(); //amaguem el input del teclat
    int charac = 0;
    try{

      while(charac != EscapeSeq.ENTER){
        charac = this.read();
        
        switch (charac) {
          case (EscapeSeq.LEFT + 1000):
            linia.move(Line.LEFT);
            break;
          
          case (EscapeSeq.RIGHT + 1000):
            linia.move(Line.RIGHT);
            break;

          case (EscapeSeq.ENTER):
            this.unsetRaw();

          case(EscapeSeq.END+1000):

            linia.move(Line.END);
            break;

          case(EscapeSeq.START+1000):
            linia.move(Line.START);
            break;
          
          default:
            linia.addChar((char) charac);
            break;
        }
        linia.printLine();
      }
    } catch (IndexOutOfBoundsException ex){
    }
    
    this.unsetRaw();

    return linia.toString();
  }
}
