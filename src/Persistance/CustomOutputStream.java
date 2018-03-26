package Persistance;

import javax.swing.*;
import java.io.*;

public class CustomOutputStream extends BufferedOutputStream{

    private JTextArea area;
    private String fileName;

    public CustomOutputStream(JTextArea area, String fileName) throws FileNotFoundException {
        super(new FileOutputStream(fileName));
        this.area = area;
        this.fileName = fileName;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        super.write(b);
        area.append(new String(this.buf, 0, b));
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        area.append(new String(b, off, len));
    }

    @Override
    public synchronized void flush() throws IOException {
        super.flush();
        if (this.count > 0) area.append(new String(this.buf, 0, this.count));
    }
}
