import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

class EditableBufferedReader extends BufferedReader{

  public EditableBufferedReader(Reader in){
        super(in);
  }


  public void setRaw(){

      String[] rawCom = new String[] {"sh", "-c", "stty -echo raw < /dev/tty"};

      try{
          Process proc = new ProcessBuilder(rawCom).start();
      } catch (IOException ex){
          System.out.println("Error in setRaw()");
      }
  }

  public void unsetRaw(){

    String[] cookedCom = new String[] {"sh", "-c", "stty -echo cooked < /dev/tty"};

    try{
     Process proc = new ProcessBuilder(cookedCom).start();
    }catch(IOException ex){
      System.out.println("Error in unsetRaw()");
    }
  }

  public int read(){

      return 0;
  }

  public String readLine(){

      return "0";
  }
}
