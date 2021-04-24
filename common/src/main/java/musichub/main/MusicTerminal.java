package musichub.main;

import musichub.business.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Interactive TTY application for MusicHub<br>
 * This class is agnostic from the backend and can be used for client and server
 */
public class MusicTerminal
{
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	protected final IMusicHub hub;
	protected final MusicPlayer player;

	protected Scanner scan;
	protected boolean should_quit;

	protected final LinkedHashMap<String, Command> commands;

	/**
	 * Create a new Music Terminal from a hub
	 * @param hubInput The hub (can be a client or a server)
	 */
	public MusicTerminal(IMusicHub hubInput) {
		this.commands = new LinkedHashMap<>();
		this.hub = hubInput;
		this.player = new MusicPlayer(hubInput);

		this.registerCommand(new Command("h", "print this help message") {
			@Override
			public void run() {
				printAvailableCommands();
			}
		});

		this.registerCommand(new Command("albums", "display the albums, ordered by date") {
			@Override
			public void run() {
				displayAlbums(hub.getAlbumsSortedByDate());
			}
		});

		this.registerCommand(new Command("albums-songs-by-genre", "display songs of an album, ordered by genre") {
			@Override
			public void run() {
				//songs of an album, sorted by genre
				System.out.println("Songs of an album sorted by genre will be displayed; enter the album name, available albums are:");
				displayAlbums(hub.getAlbumsSortedByDate());

				String albumTitle = prompt("Album name: ");
				try {
					displaySongs(hub.getAlbumSongsSortedByGenre(albumTitle));
				} catch (NoAlbumFoundException ex) {
					System.out.println("No album found with the requested title " + ex.getMessage());
				} catch (NoElementFoundException ex) {
					System.out.println("An element of the album couldn't be found.");
				}
			}
		});

		this.registerCommand(new Command("albums-songs", "display songs of an album") {
			@Override
			public void run() {
				//songs of an album
				System.out.println("Available albums:");
				displayAlbums(hub.getAlbumsSortedByDate());

				String albumTitle = prompt("Album name: ");
				try {
				    displaySongs(hub.getAlbumSongs(albumTitle));
				} catch (NoAlbumFoundException ex) {
					System.out.println("No album found with the requested title " + ex.getMessage());
				} catch (NoElementFoundException ex) {
					System.out.println("An element of the album couldn't be found.");
				}
			}
		});

		this.registerCommand(new Command("audiobook", "display audiobooks ordered by author") {
			@Override
			public void run() {
                displayAudiobooks(hub.getAudiobooksSortedByAuthor());
			}
		});

		this.registerCommand(new Command("songs", "display songs") {
			@Override
			public void run() {
				displaySongs(hub.songs());
			}
		});

		this.registerCommand(new Command("elements", "display all songs/audiobooks") {
			@Override
			public void run() {
			    displayElements(hub.elements());
			}
		});

		this.registerCommand(new Command("song-create", "add a new song") {
			@Override
			public void run() {
				// add a new song
				System.out.println("---- New song ----");

				hub.addElement(new Song(
						prompt("Title: "),
						prompt("Artist: "),
						prompt_uint("Length in seconds: "),
						prompt("Content: "),
						prompt_enum("Genre", Genre.class)
				));

				System.out.println("Song created!");
				System.out.println("New element list: ");

				displayElements(hub.elements());
			}
		});

		this.registerCommand(new Command("album-create", "add a new album") {
			@Override
			public void run() {
				// add a new album
				System.out.println("Enter a new album: ");
				hub.addAlbum(new Album(
						prompt("Title: "),
						prompt("Artist: "),
						prompt_uint("Length in seconds: "),
						prompt_date("Date as YYYY-DD-MM: ")
				));
				System.out.println("Album created!");
				System.out.println("New list of albums: ");

				displayAlbums(hub.albums());
			}
		});

		this.registerCommand(new Command("album-add-song", "add a song to an album") {
			@Override
			public void run() {
				//add a song to an album:
				System.out.println("Add an existing song to an existing album");
				System.out.println("Type the name of the song you wish to add. Available songs: ");

				displaySongs(hub.songs());

				String songTitle = prompt("Song name: ");

				System.out.println("Type the name of the album you wish to enrich. Available albums: ");
				displayAlbums(hub.albums());

				String titleAlbum = prompt("Album name: ");
				try {
					hub.addElementToAlbum(songTitle, titleAlbum);
					System.out.println("Song added to the album!");
				} catch (NoAlbumFoundException | NoElementFoundException ex){
					System.out.println (ex.getMessage());
				}
			}
		});

		this.registerCommand(new Command("audiobook-create", "add a new audiobook") {
			@Override
			public void run() {
				// add a new audiobook
				System.out.println("Enter a new audiobook: ");

				AudioBook b = new AudioBook (
					prompt("Title: "),
					prompt("Artist: "),
					prompt_uint("Length in seconds: "),
					prompt("Content: "),
					prompt_enum("AudioBook language", Language.class),
					prompt_enum("Category", Category.class)
				);

				hub.addElement(b);
				System.out.println("Audiobook created! New element list: ");

				displayElements(hub.elements());
			}
		});

		this.registerCommand(new Command("playlist-create", "create a new playlist from existing songs and audio books") {
			@Override
			public void run() {
				//create a new playlist from existing elements
				System.out.println("Add an existing song or audiobook to a new playlist");
				System.out.println("Existing playlists:");

				displayPlaylists(hub.playlists());

				PlayList pl = new PlayList(prompt("New playlist name: "));
				hub.addPlaylist(pl);
				System.out.println("Available elements: ");

				displayElements(hub.elements());

				String choice;
				do {
					String elementTitle = prompt("Type the name of the audio element you wish to add or 'n' to exit: ");	
					try {
						hub.addElementToPlayList(elementTitle, pl.getTitle());
					} catch (NoPlayListFoundException | NoElementFoundException ex) {
						System.out.println(ex.getMessage());
					}

					choice = prompt("Continue (Y/n)? ");
				} while (choice.length() == 0 || choice.charAt(0) != 'n');

				System.out.println("Playlist created!");
			}
		});

		this.registerCommand(new Command("album-delete", "delete an existing album") {
			@Override
			public void run() {
				System.out.println("Delete an existing album. Available albums:");

				displayAlbums(hub.albums());

				try {
					hub.deleteAlbum(prompt("Album name: "));
				} catch (NoAlbumFoundException ex) {
					System.out.println (ex.getMessage());
				}
				System.out.println("Album deleted!");
			}
		});

		this.registerCommand(new Command("element-delete", "delete an existing element") {
			@Override
			public void run() {
				System.out.println("Delete an existing element. Available elements:");
				displayElements(hub.elements());

				try {
					hub.deleteElement(prompt("Element name: "));
				}	catch (NoElementFoundException ex) {
					System.out.println (ex.getMessage());
				}
				System.out.println("Element deleted!");
			}
		});

		this.registerCommand(new Command("playlist-delete", "delete an existing playlist") {
			@Override
			public void run() {
				System.out.println("Delete an existing playlist. Available playlists:");
				displayPlaylists(hub.playlists());

				try {
					hub.deletePlayList("Playlist name: ");
				}	catch (NoPlayListFoundException ex) {
					System.out.println (ex.getMessage());
				}
				System.out.println("Playlist deleted!");
			}
		});

		this.registerCommand(new Command("play", "play an element") {
			@Override
			public void run() {
				//delete a playlist
				System.out.println("Available elements:");

				displayElements(hub.elements());
				player.queueMusic(prompt("Element name: "));
			}
		});

		this.registerCommand(new Command("album-play", "play an entire album") {
			@Override
			public void run() {
				//delete a playlist
				System.out.println("Available albums:");

				displayAlbums(hub.albums());
				String albumName = prompt("Album name: ");

				try {
					for (Song music : hub.getAlbumSongs(albumName)) {
						player.queueMusic(music.getTitle());
					}
				} catch (NoAlbumFoundException e) {
					System.out.println("No album found with the requested title " + e.getMessage());
				} catch (NoElementFoundException e) {
					System.out.println("An element of the album couldn't be found.");
				}
			}
		});

		this.registerCommand(new Command("playlist-play", "play an entire playlist") {
			@Override
			public void run() {
				//delete a playlist
				System.out.println("Available playlists:");
				displayPlaylists(hub.playlists());

				String playlistName = prompt("Playlist name: ");

				try {
					for (AudioElement e : hub.getPlaylistElements(playlistName)) {
						player.queueMusic(e.getTitle());
					}
				} catch (NoPlayListFoundException e) {
					System.out.println("No playlist found with the requested title " + e.getMessage());
				} catch (NoElementFoundException e) {
					System.out.println("An element of the playlist couldn't be found.");
				}
			}
		});

		this.registerCommand(new Command("volume", "change playback volume") {
			@Override
			public void run() {
				//delete a playlist
				player.setVolume(prompt_uint("New volume (0-100): "));
			}
		});

		this.registerCommand(new Command("quit", "quit program") {
			@Override
			public void run() {
				should_quit = true;
			}
		});
	}

	/**
	 * Create a formatter for various displayer
	 * @param columnNames Column names
	 * @return The displayer
	 */
	public TableFormatter createFormatter(String... columnNames) {
		return new TableFormatter(columnNames);
	}

	/**
	 * Display a list of elements using a formatter
	 * @param elements elements to display
	 */
	public void displayElements(AudioElement[] elements) {
		TableFormatter formatter = createFormatter("Title", "Artist", "Duration");

		for (AudioElement e : elements)
			formatter.addLine(e.getTitle(), e.getArtist(), e.getLengthInSeconds());

		formatter.display(System.out);
	}

	/**
	 * Display a list of playlists using a formatter
	 * @param elements elements to display
	 */
	public void displayPlaylists(PlayList[] elements) {
		TableFormatter formatter = createFormatter("Title", "Element count");

		for (PlayList e : elements)
			formatter.addLine(e.getTitle(), e.getElements().size());

		formatter.display(System.out);

	}

	/**
	 * Display a list of albums using a formatter
	 * @param elements elements to display
	 */
	public void displayAlbums(Album[] elements) {
		TableFormatter formatter = createFormatter("Title", "Artist", "Date", "Duration", "Element count");

		for (Album e : elements)
			formatter.addLine(e.getTitle(), e.getArtist(), e.getDateStr(), e.getLengthInSeconds(), e.getSongs().size());

		formatter.display(System.out);
	}

	/**
	 * Display a list of songs using a formatter
	 * @param elements elements to display
	 */
	public void displaySongs(Song[] elements) {
		TableFormatter formatter = createFormatter("Title", "Artist", "Duration", "Genre");

		for (Song e : elements)
			formatter.addLine(e.getTitle(), e.getArtist(), e.getLengthInSeconds(), e.getGenre());

		formatter.display(System.out);
	}

	/**
	 * Display a list of audiobooks using a formatter
	 * @param elements elements to display
	 */
	public void displayAudiobooks(AudioBook[] elements) {
		TableFormatter formatter = createFormatter("Title", "Artist", "Duration", "Category", "Language");

		for (AudioBook e : elements)
			formatter.addLine(e.getTitle(), e.getArtist(), e.getLengthInSeconds(), e.getCategory(), e.getLanguage());

		formatter.display(System.out);
	}

	/**
	 * Print help using a formatter
	 */
	public void printAvailableCommands() {
		TableFormatter formatter = createFormatter("Command", "Description");

		for (Command command : this.commands.values()) {
			formatter.addLine(command.name, command.description);
		}

		formatter.display(System.out);
	}

	/**
	 * Parse commands from standard input
	 * @param ps1 Prompt to display before each commands (example: "$ ")
	 */
	public void parseCommands(String ps1) {
		System.out.println("Type h for available commands");
		scan = new Scanner(System.in);

		this.should_quit = false;

		String choice;
		do {
			choice = prompt(ps1);

			if (choice.isEmpty())
				continue;

			if (commands.containsKey(choice)) {
				commands.get(choice).run();
			} else {
				System.out.println("Unknown command. Type h for help");
			}

		} while (!this.should_quit);

		scan.close();
		scan = null;
	}

	protected String prompt(String value) {
		System.out.print(value);
		try {
			return this.scan.nextLine();
		} catch (NoSuchElementException e) { // Ctrl+D
			System.out.println();
			System.exit(0);
			return "";
		}
	}

	protected <T extends Enum<T>> T prompt_enum(String prompt, Class<T> clazz) {
	    StringBuilder bld = new StringBuilder();
	    bld.append(prompt);
	    bld.append(" [");

	    boolean first = true;
	    for (T c : clazz.getEnumConstants()) {
	        if (first)
	        	first = false;
	        else
	        	bld.append(", ");
			bld.append(c);
		}
	    bld.append("]: ");
	    prompt = bld.toString();


		while (true) {
		    String value = this.prompt(prompt);
			for (T candidate : clazz.getEnumConstants()) {
				if (candidate.toString().equalsIgnoreCase(value))
					return candidate;
			}

			System.out.format("`%s` isn't valid", value);
		}
	}

	protected int prompt_uint(String ps1) {
		while (true) {
			String value = prompt(ps1);

			try {
				int v = Integer.parseInt(value);
				if (v >= 0)
					return v;
				System.out.println("Input should be a positive integer");
			} catch (NumberFormatException e) {
				System.out.println("Input isn't a valid integer!");
			}
		}
	}

	protected Date prompt_date(String ps1) {
		while (true) {
			String value = prompt(ps1);

			try {
				Date date = sdf.parse(value);
				return date;
			} catch (ParseException e) {
				System.out.println("Input isn't a valid date! Example: 2021-01-31");
			}
		}
	}

	protected void registerCommand(Command cmd) {
		this.commands.put(cmd.name, cmd);
	}

	protected abstract class Command {
		public String name;
		public String description;

		public Command(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public abstract void run();
	}
}