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
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private final IMusicHub hub;
	private Scanner scan;
	private boolean should_quit;

	private final LinkedHashMap<Character, Command> commands;

	/**
	 * Create a new Music Terminal from a hub
	 * @param hubInput The hub (can be a client or a server)
	 * @param isServer If isServer is true, add the commands specific to server, otherwise commands specific to client
	 */
	public MusicTerminal(IMusicHub hubInput, boolean isServer) {
		this.commands = new LinkedHashMap<>();
		this.hub = hubInput;

		this.registerCommand(new Command('h', "print this help message") {
			@Override
			public void run() {
				printAvailableCommands();
			}
		});

		this.registerCommand(new Command('t', "display the album titles, ordered by date") {
			@Override
			public void run() {
				//album titles, ordered by date
				System.out.println(hub.getAlbumsTitlesSortedByDate());
			}
		});

		this.registerCommand(new Command('g', "display songs of an album, ordered by genre") {
			@Override
			public void run() {
				//songs of an album, sorted by genre
				System.out.println("Songs of an album sorted by genre will be displayed; enter the album name, available albums are:");
				System.out.println(hub.getAlbumsTitlesSortedByDate());

				String albumTitle = prompt("Album name: ");
				try {
					for (Song s : hub.getAlbumSongsSortedByGenre(albumTitle))
						System.out.println(s);
				} catch (NoAlbumFoundException ex) {
					System.out.println("No album found with the requested title " + ex.getMessage());
				}
			}
		});

		this.registerCommand(new Command('d', "display songs of an album") {
			@Override
			public void run() {
				//songs of an album
				System.out.println("Available albums:");
				System.out.println(hub.getAlbumsTitlesSortedByDate());

				String albumTitle = prompt("Album name: ");
				try {
					for (Song s : hub.getAlbumSongs(albumTitle))
						System.out.println(s);
				} catch (NoAlbumFoundException ex) {
					System.out.println("No album found with the requested title " + ex.getMessage());
				}
			}
		});

		this.registerCommand(new Command('u', "display audiobooks ordered by author") {
			@Override
			public void run() {
				//audiobooks ordered by author
				System.out.println(hub.getAudiobooksTitlesSortedByAuthor());
			}
		});

		this.registerCommand(new Command('m', "display all songs/audiobooks titles") {
			@Override
			public void run() {
				for (AudioElement el : hub.elements())
					System.out.println(el.getTitle());
			}
		});

		this.registerCommand(new Command('c', "add a new song") {
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

				System.out.println("New element list: ");
				for (AudioElement el : hub.elements())
					System.out.println(el.getTitle());

				System.out.println("Song created!");
			}
		});

		this.registerCommand(new Command('a', "add a new album") {
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
				System.out.println("New list of albums: ");

				for (Album el : hub.albums())
					System.out.println(el.getTitle());

				System.out.println("Album created!");
			}
		});

		this.registerCommand(new Command('+', "add a song to an album") {
			@Override
			public void run() {
				//add a song to an album:
				System.out.println("Add an existing song to an existing album");
				System.out.println("Type the name of the song you wish to add. Available songs: ");

				for (AudioElement ae : hub.elements())
					if (ae instanceof Song)
						System.out.println(ae.getTitle());

				String songTitle = prompt("Song name: ");

				System.out.println("Type the name of the album you wish to enrich. Available albums: ");
				for (Album el : hub.albums())
					System.out.println(el.getTitle());

				String titleAlbum = prompt("Album name: ");
				try {
					hub.addElementToAlbum(songTitle, titleAlbum);
					System.out.println("Song added to the album!");
				} catch (NoAlbumFoundException | NoElementFoundException ex){
					System.out.println (ex.getMessage());
				}
			}
		});

		this.registerCommand(new Command('l', "add a new audiobook") {
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

				for (AudioElement el : hub.elements())
					System.out.println(el.getTitle());
			}
		});

		this.registerCommand(new Command('p', "create a new playlist from existing songs and audio books") {
			@Override
			public void run() {
				//create a new playlist from existing elements
				System.out.println("Add an existing song or audiobook to a new playlist");
				System.out.println("Existing playlists:");

				for (PlayList pl : hub.playlists())
					System.out.println(pl.getTitle());

				PlayList pl = new PlayList(prompt("New playlist name: "));
				hub.addPlaylist(pl);
				System.out.println("Available elements: ");

				for (AudioElement ae : hub.elements())
					System.out.println(ae.getTitle());

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

		this.registerCommand(new Command('-', "delete an existing playlist") {
			@Override
			public void run() {
				//delete a playlist
				System.out.println("Delete an existing playlist. Available playlists:");

				for (PlayList p : hub.playlists())
					System.out.println(p.getTitle());

				String plTitle = prompt("Playlist name: ");
				try {
					hub.deletePlayList(plTitle);
				}	catch (NoPlayListFoundException ex) {
					System.out.println (ex.getMessage());
				}
				System.out.println("Playlist deleted!");
			}
		});

		if (isServer) {
			this.registerCommand(new Command('s', "save elements, albums, playlists") {
				@Override
				public void run() {
					//save elements, albums, playlists
					hub.save();
					System.out.println("Elements, albums and playlists saved!");
				}
			});
		}

		this.registerCommand(new Command('q', "quit program") {
			@Override
			public void run() {
				should_quit = true;
			}
		});
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

			if (commands.containsKey(choice.charAt(0))) {
				commands.get(choice.charAt(0)).run();
			} else {
				System.out.println("Unknown command. Type h for help");
			}

		} while (!this.should_quit);

		scan.close();
		scan = null;
	}

	/**
	 * Print terminal help
	 */
	public void printAvailableCommands() {
		for (Command command : this.commands.values()) {
			System.out.format("%c: %s\n", command.name, command.description);
		}
	}

	private String prompt(String value) {
		System.out.print(value);
		try {
			return this.scan.nextLine();
		} catch (NoSuchElementException e) { // Ctrl+D
			System.out.println();
			System.exit(0);
			return "";
		}
	}

	private <T extends Enum<T>> T prompt_enum(String prompt, Class<T> clazz) {
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

	private int prompt_uint(String ps1) {
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

	private Date prompt_date(String ps1) {
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

	private void registerCommand(Command cmd) {
		this.commands.put(cmd.name, cmd);
	}

	private abstract class Command {
		public char name;
		public String description;

		public Command(char name, String description) {
			this.name = name;
			this.description = description;
		}

		public abstract void run();
	}
}