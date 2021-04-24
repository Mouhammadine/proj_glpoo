package musichub.client;

import musichub.business.IMusicHub;
import musichub.main.MusicTerminal;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Client's main class
 */
public class Main {
	public static void main(String[] args) throws MalformedURLException {
		String ip = "localhost";
	    if (args.length > 1) {
	    	ip = args[1];
		}

		URL url = new URL("http://" + ip + ":7779/ws/musichub?wsdl");
		QName qname = new QName("http://server.musichub/", "ServerMusicHubService");

		Service service = Service.create(url, qname);
		IMusicHub musicHub = service.getPort(IMusicHub.class);

		new MusicTerminal(musicHub).parseCommands("MusicHub-Client$ ");
	}
}
