package TCP_Chat;

import java.io.Serializable;

public class Message implements Serializable {
    String from;
    String to;
    String content;

    public Message(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public String toString() {
        return "From: " + from + "\nTo: " + to + "\n" + content;
    }
}
