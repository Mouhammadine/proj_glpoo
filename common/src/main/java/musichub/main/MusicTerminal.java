package musichub.main;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import musichub.business.Album;
import musichub.business.AudioBook;
import musichub.business.AudioElement;
import musichub.business.IMusicHub;
import musichub.business.NoAlbumFoundException;
import musichub.business.NoElementFoundException;
import musichub.business.NoPlayListFoundException;
import musichub.business.PlayList;
import musichub.business.Song;

public class MusicTerminal
{
	private IMusicHub hub;
	private Scanner scan;
	private boolean should_quit;

	private LinkedHashMap<Character, Command> commands;

	/**
	 * Create a new Music Terminal from a hub
	 * @param hub The hub (can be a client or a server)
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

				String albumTitle = prompt("Title: ");
				try {
					System.out.println(hub.getAlbumSongsSortedByGenre(albumTitle));
				} catch (NoAlbumFoundException ex) {
					System.out.println("No album found with the requested title " + ex.getMessage());
				}
			}
		});

		this.registerCommand(new Command('d', "display songs of an album") {
			@Override
			public void run() {
				//songs of an album
				System.out.println("Songs of an album will be displayed; enter the album name, available albums are:");
				System.out.println(hub.getAlbumsTitlesSortedByDate());

				String albumTitle = prompt("Title: ");
				try {
					System.out.println(hub.getAlbumSongs(albumTitle));
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

		this.registerCommand(new Command('c', "add a new song") {
			@Override
			public void run() {
				// add a new song
				System.out.println("---- New song ----");
				String title = prompt("Song title: ");
				String genre = prompt("Song genre (jazz, classic, hiphop, rock, pop, rap):");
				String artist = prompt("Song artist: ");
				int length = Integer.parseInt(prompt("Song length in seconds: "));
				String content = prompt("Song content: ");

				Song s = new Song (title, artist, length, content, genre);
				hub.addElement(s);

				System.out.println("New element list: ");
				Iterator<AudioElement> it = hub.elements();
				while (it.hasNext()) System.out.println(it.next().getTitle());

				System.out.println("Song created!");
			}
		});

		this.registerCommand(new Command('a', "add a new album") {
			@Override
			public void run() {
				// add a new album
				System.out.println("Enter a new album: ");
				String aTitle = prompt("Album title: ");
				String aArtist = prompt("Album artist: ");
				int aLength = Integer.parseInt(prompt("Album length in seconds: "));
				String aDate = prompt("Album date as YYYY-DD-MM: ");
				Album a = new Album(aTitle, aArtist, aLength, aDate);
				hub.addAlbum(a);
				System.out.println("New list of albums: ");
				Iterator<Album> ita = hub.albums();
				while (ita.hasNext()) System.out.println(ita.next().getTitle());
				System.out.println("Album created!");
			}
		});

		this.registerCommand(new Command('+', "add a song to an album") {
			@Override
			public void run() {
				//add a song to an album:
				System.out.println("Add an existing song to an existing album");
				System.out.println("Type the name of the song you wish to add. Available songs: ");
				Iterator<AudioElement> itae = hub.elements();
				while (itae.hasNext()) {
					AudioElement ae = itae.next();
					if ( ae instanceof Song) System.out.println(ae.getTitle());
				}
				String songTitle = prompt("Song name: ");	

				System.out.println("Type the name of the album you wish to enrich. Available albums: ");
				Iterator<Album> ait = hub.albums();
				while (ait.hasNext()) {
					Album al = ait.next();
					System.out.println(al.getTitle());
				}
				String titleAlbum = prompt("Album name: ");
				try {
					hub.addElementToAlbum(songTitle, titleAlbum);
				} catch (NoAlbumFoundException ex){
					System.out.println (ex.getMessage());
				} catch (NoElementFoundException ex){
					System.out.println (ex.getMessage());
				}
				System.out.println("Song added to the album!");
			}
		});

		this.registerCommand(new Command('l', "add a new audiobook") {
			@Override
			public void run() {
				// add a new audiobook
				System.out.println("Enter a new audiobook: ");
				String bTitle = prompt("AudioBook title: ");
				String bCategory = prompt("AudioBook category (youth, novel, theater, documentary, speech)");
				String bArtist = prompt("AudioBook artist: ");
				int bLength = Integer.parseInt(prompt("AudioBook length in seconds: "));
				String bContent = prompt("AudioBook content: ");
				String bLanguage = prompt("AudioBook language (french, english, italian, spanish, german)");
				AudioBook b = new AudioBook (bTitle, bArtist, bLength, bContent, bLanguage, bCategory);
				hub.addElement(b);
				System.out.println("Audiobook created! New element list: ");
				Iterator<AudioElement> itl = hub.elements();
				while (itl.hasNext()) System.out.println(itl.next().getTitle());
			}
		});

		this.registerCommand(new Command('p', "create a new playlist from existing songs and audio books") {
			@Override
			public void run() {
				//create a new playlist from existing elements
				System.out.println("Add an existing song or audiobook to a new playlist");
				System.out.println("Existing playlists:");
				Iterator<PlayList> itpl = hub.playlists();
				while (itpl.hasNext()) {
					PlayList pl = itpl.next();
					System.out.println(pl.getTitle());
				}
				String playListTitle = prompt("Type the name of the playlist you wish to create:");	
				PlayList pl = new PlayList(playListTitle);
				hub.addPlaylist(pl);
				System.out.println("Available elements: ");

				Iterator<AudioElement> itael = hub.elements();
				while (itael.hasNext()) {
					AudioElement ae = itael.next();
					System.out.println(ae.getTitle());
				}
				String choice;
				do {
					String elementTitle = prompt("Type the name of the audio element you wish to add or 'n' to exit: ");	
					try {
						hub.addElementToPlayList(elementTitle, playListTitle);
					} catch (NoPlayListFoundException ex) {
						System.out.println (ex.getMessage());
					} catch (NoElementFoundException ex) {
						System.out.println (ex.getMessage());
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
				Iterator<PlayList> itp = hub.playlists();
				while (itp.hasNext()) {
					PlayList p = itp.next();
					System.out.println(p.getTitle());
				}
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
	 * @param ps1 Prompt to display before each commands (example: "> ")
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
				System.out.println("Unknow command. Type h for help");
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