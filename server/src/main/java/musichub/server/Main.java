package musichub.server;

import musichub.business.IMusicHub;
import musichub.main.MusicTerminal;

import javax.xml.ws.Endpoint;

public class Main {
    public static void main(String[] args) {
        IMusicHub server = ServerMusicHub.load();
    	MusicTerminal terminal = new MusicTerminal(server, true);

        Endpoint.publish("http://localhost:7779/ws/musichub", server);
    	terminal.parseCommands("MusicHub-Server$ ");
    }
}
