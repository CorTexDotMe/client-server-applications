# Stock manager

Client-server app to manage shop's storage.
Storage can have items that form groups.
Item has a name, description, amount, cost and producer.
The group has a name and description. The project uses a relational database.

## Server

The server side is written in Java. Everything works without any frameworks like Spring.
Only native Java for client-server communication. This is an educational project so a lot of systems were created
manually.

The server introduces its own protocol for communication, which is not HTTP.
Communication can be established by either TCP or UDP. The server uses a thread pool
to process more connections. All data is stored with SQLite and accessed through JDBC.
The server can send and receive packets. All packages are encrypted with the AES-GCM algorithm.

There are 74 unit tests to cover most of the server functionality.

### Packet structure:

| Offset  | Length | Mnemonic | Notes                                                              |
|:--------|:------:|:---------|:-------------------------------------------------------------------|
| 00      |   1    | bMagic   | Bytes that show the start of the packet. Value - 13h(h - mean hex) |
| 01      |   1    | bSrc     | Unique client id                                                   |
| 02      |   8    | bPktId   | Packet id. Newer packets have bigger id. Big-endian format         |
| 10      |   4    | wLen     | Message length. Big-endian                                         |
| 14      |   2    | wCrc16   | CRC16. Bytes from 00 to 13. Big-endian                             |
| 16      |  wLen  | bMsq     | Message                                                            |
| 16+wLen |   2    | wCrc16   | CRC16. Bytes from 16 to 16+wLen-1. Big-endian                      |

### Message structure:

| Offset | Length | Mnemonic | Notes                                                            |
|:-------|:------:|:---------|:-----------------------------------------------------------------|
| 00     |   4    | cType    | Command code. big-endian                                         |
| 04     |   4    | bUserId  | User id differs from client id. Users are stored in the database |
| 08     | wLen-8 | message  | Main message. Can be JSON or byte array big-endian               |

## Client

The client side is a desktop application written in Kotlin.
The main framework is Jetpack Compose.

To communicate with the server, the client part has a copy of the server's core package.
Also, it has a copy of the StoreClientTCP and StoreClientUDP classes from the server.