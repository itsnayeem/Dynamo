
public class Server {
	private String IP;
	private int port;
	
	public Server(String i, int p) {
		IP = i;
		port = p;
	}
	
	public String getIP() {
		return IP;
	}
	
	public int getPort() {
		return port;
	}
	
	public Server getCopy() {
		return new Server (IP, port);
	}
	
	public String toString() {
		return IP + ":" + port;
	}
	
	public boolean equals(Server other) {
		return (IP.equals(other.getIP()) && port == other.getPort());
	}
}
