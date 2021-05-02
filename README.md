# Build and test

In order to build MusicHub, you need JDK 1.8 and Maven. Then, you can run:
```bash
$ mvn build package
```

# Run server

Server will use port 7779. In the current version, this port cannot be changed. Make sure this port is free then run:
```bash
$ java -jar server/target/MusicHub-Server.jar
```

The server has the `save` command. You should use it before exiting the server.

# Run client

To run the client you can use:
```bash
$ java -jar client/target/MusicHub-Client.jar [ip]
```

`[ip]` is an optional parameter corresponding to the IP of the server's host. If the server is running on the same
host as client, you don't need to provide a value.

# Use client & server

To know how to use client or server you can run the command `h` in the CLI. This will print all available commands.

The server data will be stored in the current directory. If you run it from the root of this git project, you'll have
some defaults songs, albums and playlists.
