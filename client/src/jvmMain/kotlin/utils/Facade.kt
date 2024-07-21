package utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ukma.stockmanager.core.entities.Group
import com.ukma.stockmanager.core.entities.Item
import com.ukma.stockmanager.core.entities.Message
import com.ukma.stockmanager.core.entities.Packet
import com.ukma.stockmanager.core.utils.CommandAnalyser
import com.ukma.stockmanager.core.utils.Constants
import com.ukma.stockmanager.network.Client
import com.ukma.stockmanager.network.tcp.StoreClientTCP
import com.ukma.stockmanager.network.udp.StoreClientUDP
import kotlinx.coroutines.*

fun ByteArray.toPacket(): Packet {
    return Packet(this)
}

//TODO посередник. Як використовується мапер
class Facade(useTCP: Boolean = false, val reconnectInfinitely: Boolean = true) {
    var connected: Boolean = false
    private val ip = "127.0.0.1"
    private val objectMapper = ObjectMapper()
    private val bPktId = 1L

    private val client: Client
    private val bSrc: Byte
    private val bUserId: Int

    companion object {
        @Volatile
        private var instance: Facade? = null

        fun getInstance(useTCP: Boolean = true, reconnectInfinitely: Boolean = true): Facade {
            synchronized(this) {
                var localInstance = instance
                if (localInstance == null) {
                    localInstance = Facade(useTCP, reconnectInfinitely)
                    instance = localInstance
                }
                return localInstance
            }
        }
    }

    private suspend fun receivePacket(): Packet? {
        return withContext(Dispatchers.IO) {

            val packet = Packet(
                0b1, 0,
                Message(CommandAnalyser.INITIAL_PACKET, 0, emptyJsonAsBytes())
            ).bytes

            if (reconnectInfinitely)
                return@withContext client.sendAndReceiveMessage(packet).toPacket()
            else {
                while (isActive) {
                    val result = client.sendAndReceiveMessageWithoutReconnect(packet)
                    if (result != null) return@withContext result.toPacket()
                }

                return@withContext null
            }
        }

    }

    init {
        client =
            if (useTCP) StoreClientTCP()
            else StoreClientUDP()
        startConnection()


        val response = runBlocking {
            withTimeoutOrNull(Constants.WAITING_TIME_MILLISECONDS.toLong() * 2) {
                receivePacket()
            }
        }

        if (response != null) {
            connected = true

            val map: Map<String, Any> = objectMapper.readValue(
                response.bMsg.message,
                object : TypeReference<Map<String, Any>>() {}
            )

            bSrc = (map["bSrc"] as Int).toByte()
            bUserId = map["bUserId"] as Int

            endConnection()
        } else {
            bSrc = 0
            bUserId = 0
        }
    }

    fun getGroup(id: Int): Group? {
        startConnection()

        val message = Message(
            CommandAnalyser.GROUP_GET, bUserId,
            objectMapper.writeValueAsBytes(mapOf("id" to id))
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, object : TypeReference<Group>() {})
    }

    fun getAllGroups(): List<Group> {
        startConnection()
        val packet = Packet(
            bSrc,
            bPktId,
            Message(CommandAnalyser.GROUP_GET_ALL, bUserId, emptyJsonAsBytes())
        )

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, object : TypeReference<List<Group>>() {})
    }

    fun getItem(id: Int): Item? {
        startConnection()

        val message = Message(
            CommandAnalyser.ITEM_GET, bUserId,
            objectMapper.writeValueAsBytes(mapOf("id" to id))
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, object : TypeReference<Item>() {})
    }

    fun getAllItems(): List<Item> {
        startConnection()
        val packet = Packet(
            bSrc,
            bPktId,
            Message(CommandAnalyser.ITEM_GET_ALL, bUserId, emptyJsonAsBytes())
        )

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, object : TypeReference<List<Item>>() {})
    }

    fun getAllItemsByGroup(id: Int): List<Item> {
        startConnection()

        val message = Message(
            CommandAnalyser.ITEM_GET_BY_GROUP, bUserId,
            objectMapper.writeValueAsBytes(mapOf("id" to id))
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()


        return objectMapper.readValue(response.bMsg.message, object : TypeReference<List<Item>>() {})
    }

    fun createGroup(
        name: String,
        description: String,
    ): Boolean {
        startConnection()

        val message = Message(
            CommandAnalyser.GROUP_CREATE, bUserId,
            objectMapper.writeValueAsBytes(
                mapOf(
                    "name" to name,
                    "description" to description,
                )
            )
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()

        return objectMapper.readValue(response.bMsg.message, object : TypeReference<Map<String, Any>>() {})
            .get("response") as Boolean
    }

    fun createItem(
        name: String,
        description: String,
        amount: Int,
        cost: Double,
        producer: String,
        groupId: Int
    ): Boolean {
        startConnection()

        val message = Message(
            CommandAnalyser.ITEM_CREATE, bUserId,
            objectMapper.writeValueAsBytes(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "amount" to amount,
                    "cost" to cost,
                    "producer" to producer,
                    "groupId" to groupId
                )
            )
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()

        return objectMapper.readValue(response.bMsg.message, object : TypeReference<Map<String, Any>>() {})
            .get("response") as Boolean
    }

    fun updateGroupName(id: Int, name: String): Boolean {
        val responseMap = updateField(id, CommandAnalyser.GROUP_SET_NAME, "name" to name)
        return responseMap["response"] as Boolean
    }

    fun updateGroupDescription(id: Int, description: String) {
        updateField(id, CommandAnalyser.GROUP_SET_DESCRIPTION, "description" to description)
    }

    fun updateItemName(id: Int, name: String): Boolean {
        val responseMap = updateField(id, CommandAnalyser.ITEM_SET_NAME, "name" to name)
        return responseMap["response"] as Boolean
    }

    fun updateItemDescription(id: Int, description: String) {
        updateField(id, CommandAnalyser.ITEM_SET_DESCRIPTION, "description" to description)
    }

    fun addAmount(id: Int, amount: Int) {
        updateField(id, CommandAnalyser.ITEM_ADD_AMOUNT, "amount" to amount)
    }

    fun updateCost(id: Int, cost: Double) {
        updateField(id, CommandAnalyser.ITEM_SET_COST, "cost" to cost)
    }

    fun updateProducer(id: Int, producer: String) {
        updateField(id, CommandAnalyser.ITEM_SET_PRODUCER, "producer" to producer)
    }

    fun updateGroupId(id: Int, groupId: Int) {
        updateField(id, CommandAnalyser.ITEM_SET_GROUP, "groupId" to groupId)
    }

    fun deleteGroup(id: Int) {
        performQuery(CommandAnalyser.GROUP_REMOVE, mapOf("id" to id))
    }

    fun deleteItem(id: Int) {
        performQuery(CommandAnalyser.ITEM_REMOVE, mapOf("id" to id))
    }

    private fun updateField(id: Int, command: Int, pair: Pair<String, Any>): Map<String, Any> {
        return performQuery(command, mapOf("id" to id, pair))
    }


    private fun performQuery(command: Int, map: Map<String, Any>): Map<String, Any> {
        startConnection()

        val message = Message(
            command, bUserId,
            objectMapper.writeValueAsBytes(map)
        )
        val packet = Packet(bSrc, bPktId, message)

        val response = client.sendAndReceiveMessage(packet.bytes).toPacket()
        endConnection()
        return objectMapper.readValue(response.bMsg.message, object : TypeReference<Map<String, Any>>() {})
    }

    private fun emptyJsonAsBytes(): ByteArray {
        return objectMapper.writeValueAsBytes(emptyMap<String, Any>())
    }

    private fun startConnection(port: Int = client.port) {
        client.startConnection(ip, port, reconnectInfinitely)
    }

    private fun endConnection() {
        client.endConnection()
    }
}