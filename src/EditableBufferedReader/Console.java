package EditableBufferedReader;

import java.util.Observer;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Console implements Observer{

    private Line linia;

    public Console(Line l) {
        this.linia = l;
        System.out.print(EscapeSeq.ENABLE_MOUSE); //activar lectura ratolí
        System.out.print(EscapeSeq.ENABLE_MOUSE_EX); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
    }

    @Override
    public void update(Observable o, Object arg) {

        if(o == linia){
            EscapeSeq seq = (EscapeSeq) arg;
            System.out.print(seq.toString());
        }
    }

}
