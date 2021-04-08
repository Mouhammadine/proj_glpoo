package musichub.server;

import musichub.main.MusicTerminal;

public class Main {
    public static void main(String[] args) {
    	MusicTerminal terminal = new MusicTerminal(new ServerMusicHub(), true);
    	terminal.parseCommands("MusicHub-Server$ ");
    }
}
