package EditableBufferedReaderMVCMultiline;

import java.util.Observer;
import java.util.Observable;

import EditableBufferedReader.EscapeSeq;

@SuppressWarnings("deprecation")
public class Console implements Observer{

    private MultiLine MLinia;

    public Console(MultiLine l) {

        this.MLinia = l;
    }

    @Override
    public void update(Observable o, Object arg) {

        if(o == MLinia){
            EscapeSeq seq = (EscapeSeq) arg;
            System.out.print(seq.toString());
        }
    }

    public void clear() {
        System.out.print("\033[2J");
    }

}
