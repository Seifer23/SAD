import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditableBufferedReader extends BufferedReader {

	Line linia;
	
	public EditableBufferedReader(Reader in) {
		super(in);
		linia = new Line(getMaxCol());
		System.out.println(getMaxCol());
	}

	public void setRaw() {

		String[] rawCom = new String[] { "sh", "-c", "stty echo raw < /dev/tty" };

		try {
			Process proc = new ProcessBuilder(rawCom).start();
		} catch (IOException ex) {
			System.out.println("Error in setRaw()");
		}
	}

	public int getMaxCol() {

		String[] getCol = new String[] { "sh", "-c", "tput cols < /dev/tty" };
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

	public void unsetRaw() {

		String[] cookedCom = new String[] { "sh", "-c", "stty echo cooked < /dev/tty" }; //Only changed made: redirects input to terminal to give exact number of columns

		try {
			Process proc = new ProcessBuilder(cookedCom).start();
		} catch (IOException ex) {
			System.out.println("Error in unsetRaw()");
		}
	}

	@Override
	public int read() throws IOException {

		try {
			int charac = super.read(); // 1rst call to super.read(): Read the next caracter
			if (charac == EscapeSeq.DELETE)
				return -charac;
			if (charac == EscapeSeq.CR)
				return EscapeSeq.ENTER;
			if (charac != EscapeSeq.ESC) // If not an escape sequence, return the character
				return charac;
			charac = super.read(); // 2nd call to super.read(): Read the next caracter of escape sequence
			if (charac == EscapeSeq.BRACKET)
				return -super.read();
			else if (charac == EscapeSeq.CTLRC)
				return EscapeSeq.ENTER;
		} catch (IOException e) {
			throw e;
		}
		return EscapeSeq.EMPTY;
//		return super.read();
	}

	public String readLine() throws IOException {

		this.setRaw(); // amaguem el input del teclat
		int charac = 0;
		try {

			while (charac != EscapeSeq.ENTER) {
				charac = this.read();

				switch (charac) {
				case (-EscapeSeq.LEFT):
					linia.move(Line.LEFT);
					break;

				case (-EscapeSeq.RIGHT):
					linia.move(Line.RIGHT);
					break;
				case (-EscapeSeq.ENTER):
					linia.move(Line.END);
					break;
				case (-EscapeSeq.END):
				case (-EscapeSeq.DOWN):
					linia.move(Line.END);
					break;

				case (-EscapeSeq.START):
				case (-EscapeSeq.UP):
					linia.move(Line.START);
					break;

				case (-EscapeSeq.INSERT):
					linia.switchOverwrite();
					this.read();
					break;

				case (-EscapeSeq.SUPR):
					this.read();
					linia.deleteChar(false);
					break;

				case (-EscapeSeq.DELETE):
					linia.deleteChar(true);
					break;

				default:
					if (charac > 0 && charac != 13) // CR posat manualment
						linia.addChar((char) charac);
					break;
				}
				linia.printLine();
			}
		} catch (IndexOutOfBoundsException ex) {
		} catch (IOException e) {
			throw e;
		}
		linia.move(Line.END);
		linia.addChar((char) 13);
		linia.printLine();
		this.unsetRaw();
		return linia.toString();
	}
}
