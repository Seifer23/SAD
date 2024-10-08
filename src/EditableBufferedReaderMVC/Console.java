package EditableBufferedReaderMVC;

import java.util.Observer;
import java.util.Observable;

import EditableBufferedReader.EscapeSeq;

@SuppressWarnings("deprecation")
public class Console implements Observer{

    private Line linia;

    public Console(Line l) {
        this.linia = l;
        System.out.print("\033[?1003h"); //activar lectura ratolí
        System.out.print("\033[?1006h"); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
    }

    @Override
    public void update(Observable o, Object arg) {

        if(o == linia){
            EscapeSeq seq = (EscapeSeq) arg;
            System.out.print(seq.toString());
        }
    }

}
