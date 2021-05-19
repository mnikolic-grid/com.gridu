package externalsorting;

import java.io.BufferedReader;
import java.io.IOException;

public final class FileBuffer {

    private BufferedReader fbr;
    private String value;

    public FileBuffer(BufferedReader r) throws IOException {
        this.fbr = r;
        reload();
    }

    private void reload() throws IOException{
        this.value = this.fbr.readLine();
    }

    public String peek(){
        return this.value;
    }

    public String pop() throws IOException {
        String answer = peek();
        reload();
        return answer;
    }

    public void close() throws IOException {
        this.fbr.close();
    }

    public boolean empty() {
        return this.value == null;
    }
}
