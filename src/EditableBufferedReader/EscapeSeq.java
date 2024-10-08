package EditableBufferedReader;

public class EscapeSeq {

    //TECLES ESPECIALS
    public final static int RIGHT = 67; //C
    public final static int LEFT = 68; //D
    public final static int START = 72; //H
    public final static int END = 70; //F
    public final static int INSERT = 50; //2
    public final static int SUPR = 51; //3
    public final static int DELETE = 127;
    public final static int ENTER = 13; //enter
    public final static int UP = 65;//A
    public final static int DOWN = 66;//B
    public final static int CTLRC = 3;

    //CARÀCTERS IMPORTANTS
    public final static int ESC = 27; //^[
    public final static int BRACKET = 91; //[
    public final static int TILDE = 126; //~
    public final static int EMPTY = -1;
    public final static int MOUSE = 60; //<

    //SEQ DE CONTROL

    public final static String MOVE_TO = "\033[%dG";
    public final static String DEL_SEQ = "\033[P";
    public final static String ADD_SPACE = "\033[@";
    
    private String seq;

    public EscapeSeq(String seq){
        this.seq = seq;
    }

    @Override
    public String toString() {
        return seq;
    }

}