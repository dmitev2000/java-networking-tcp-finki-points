package TCP_Chat;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {
    public String username;
    InetAddress local_address;

    public User(String username, InetAddress local_address) {
        this.username = username;
        this.local_address = local_address;
    }

    @Override
    public String toString() {
        return this.username + " (" + this.local_address + ")";
    }
}
