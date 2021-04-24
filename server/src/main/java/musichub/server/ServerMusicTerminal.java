package musichub.server;

import musichub.business.IMusicHub;
import musichub.main.MusicTerminal;

/**
 * Server music terminal
 *
 * Same than a classic music terminal, but add command 'save'
 */
public class ServerMusicTerminal extends MusicTerminal {
    /**
     * Create a new Music Terminal from a hub
     *
     * @param hubInput The hub (can be a client or a server)
     */
    public ServerMusicTerminal(IMusicHub hubInput) {
        super(hubInput);

        this.registerCommand(new Command("save", "save elements, albums, playlists") {
            @Override
            public void run() {
                hub.save();
            }
        });
    }
}
