package musichub.client;

import musichub.business.IMusicHub;
import musichub.main.MusicTerminal;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:7779/ws/musichub?wsdl");
		QName qname = new QName("http://server.musichub/", "ServerMusicHubService");

		Service service = Service.create(url, qname);
		IMusicHub musicHub = service.getPort(IMusicHub.class);

		new MusicTerminal(musicHub, false).parseCommands("MusicHub-Client$ ");
	}
}
