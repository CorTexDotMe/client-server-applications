# Stock manager

Client-server app to manage shop's storage.
Store can have items that form groups.
Items have: name, description, amount, cost and producer.
Groups have: name and description. Project uses relational database.

## Server

Server side is done with Java. Everything works without any frameworks like Spring.
Only native Java for client-server communication. This is an educational project so a lot of systems were created manually.

Server introduces its own protocol for communication that is not HTTP.
Communication can be established by either TCP or UDP. Server uses thread pools
to process more connections. All data is stored with SQLite and accessed through JDBC.
Server can send and receive packets. All packages are encrypted with AES-GCM algorithm.

There are 74 unit tests to cover most of the server functionality.

### Packet structure:

| Offset  | Length | Mnemonic | Notes                                                               |
|:--------|:------:|:---------|:--------------------------------------------------------------------|
| 00      |   1    | bMagic   | Bytes that shows the start of the packet. Value - 13h(h - mean hex) |
| 01      |   1    | bSrc     | Unique client id                                                    |
| 02      |   8    | bPktId   | Packet id. Newer packets have bigger id. Big-endian format          |
| 10      |   4    | wLen     | Message length. Big-endian                                          |
| 14      |   2    | wCrc16   | CRC16. Bytes from 00 to 13. Big-endian                              |
| 16      |  wLen  | bMsq     | Message                                                             |
| 16+wLen |   2    | wCrc16   | CRC16. Bytes from 16 to 16+wLen-1. Big-endian                       |

### Message structure:

| Offset | Length | Mnemonic | Notes                                                                  |
|:-------|:------:|:---------|:-----------------------------------------------------------------------|
| 00     |   4	   | cType    | Command code. big-endian                                               |
| 04     |   4    | bUserId  | User id that is different from client id. Users are stored in database |
| 08     | wLen-8 | message  | Main message. Could be json or byte array big-endian                   |

## Client

Client side is a desktop application written in Kotlin. 
Main framework is Jetpack Compose. 

To communicate with server client project has copy of server's core package. 
Also, it has copy of StoreClientTCP and StoreClientUDP classes from server.
