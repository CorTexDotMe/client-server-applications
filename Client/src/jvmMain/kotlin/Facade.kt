import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Message
import com.ukma.nechyporchuk.core.entities.Packet
import com.ukma.nechyporchuk.core.utils.CommandAnalyser
import com.ukma.nechyporchuk.core.utils.Constants
import com.ukma.nechyporchuk.network.Client
import com.ukma.nechyporchuk.network.tcp.StoreClientTCP
import com.ukma.nechyporchuk.network.udp.StoreClientUDP

fun ByteArray.toPacket(): Packet {
    return Packet(this)
}

//TODO посередник. Як використовується мапер
class Facade(useTCP: Boolean = false) {
    private val ip = "127.0.0.1"
    private val objectMapper = ObjectMapper()
    private val bPktId = 1L

    private val client: Client
    private val bSrc: Byte
    private val bUserId: Int

    init {
        client =
            if (useTCP) StoreClientTCP()
            else StoreClientUDP()
        startConnection()


        val response = client.sendAndReceiveMessage(
            Packet(
                0b1,
                0,
                Message(CommandAnalyser.INITIAL_PACKET, 0, null)
            ).bytes
        ).toPacket()

        val map: Map<String, Any> = objectMapper.readValue(
            response.bMsg.message,
            object : TypeReference<Map<String, Any>>() {}
        )

        bSrc = map["bSrc"] as Byte
        bUserId = map["bUserId"] as Int

        endConnection()
    }

    fun getAllGroups(): List<Group> {
        startConnection()
        val packet = Packet(
            bSrc,
            bPktId,
            Message(CommandAnalyser.GROUP_GET_ALL, bUserId, null)
        )

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, emptyList<Group>().javaClass)
    }


    private fun startConnection() {
        client.startConnection(ip, Constants.TCP_PORT)
    }

    private fun endConnection() {
        client.endConnection()
    }
}