package musichub.server;

import musichub.business.IMusicHub;
import musichub.main.MusicTerminal;

import javax.xml.ws.Endpoint;
import java.io.IOException;

/**
 * Servers' main class
 */
public class Main {
    public static void main(String[] args) throws IOException {
        LogFormatter.prepareLogger("server_log.txt");

        IMusicHub server = ServerMusicHub.load();
    	MusicTerminal terminal = new ServerMusicTerminal(server);

        Endpoint.publish("http://localhost:7779/ws/musichub", server);
    	terminal.parseCommands("MusicHub-Server$ ");
    }
}
