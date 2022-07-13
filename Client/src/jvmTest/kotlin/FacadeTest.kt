import com.ukma.nechyporchuk.core.entities.Group
import com.ukma.nechyporchuk.core.entities.Item
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import utils.Facade

internal class FacadeTest {

    private val initGroup1 = Group(0, "TestUniqueGroup1", "des")
    private val initGroup2 = Group(0, "TestUniqueGroup2", "des")

    private val initItem1 = Item(0, "TestUniqueItem1", "des", 0, 10.0, "", 0)
    private val initItem2 = Item(0, "TestUniqueItem2", "des", 0, 10.0, "", 0)

    companion object {
        private lateinit var facade: Facade

        //TODO connected
        @BeforeAll
        @JvmStatic
        internal fun beforeClass() {
            facade = Facade(useTCP = false, reconnectInfinitely = false)
//            facade = Facade.getInstance()
            assumeTrue(facade.connected, "Cannot connect to server")
        }
    }

    @BeforeEach
    internal fun setUp() {
        facade.createGroup(initGroup1.name, initGroup1.description)
        initGroup1.id = facade.getAllGroups().find { it.name == initGroup1.name }!!.id

        facade.createGroup(initGroup2.name, initGroup2.description)
        initGroup2.id = facade.getAllGroups().find { it.name == initGroup2.name }!!.id

        facade.createItem(
            initItem1.name,
            initItem1.description,
            initItem1.amount,
            initItem1.cost,
            initItem1.producer,
            initGroup1.id
        )
        initItem1.groupId = initGroup1.id
        initItem1.id = facade.getAllItems().find { it.name == initItem1.name }!!.id

        facade.createItem(
            initItem2.name,
            initItem2.description,
            initItem2.amount,
            initItem2.cost,
            initItem2.producer,
            initGroup1.id
        )
        initItem2.groupId = initGroup1.id
        initItem2.id = facade.getAllItems().find { it.name == initItem2.name }!!.id
    }

    @AfterEach
    internal fun tearDown() {
        facade.deleteGroup(initGroup1.id)
        facade.deleteGroup(initGroup2.id)
    }

    @Test
    fun testCreateDeleteAndGetAllGroups() {
        assertTrue(facade.getAllGroups().containsAll(listOf(initGroup1, initGroup2)))
    }

    @Test
    fun testCreateDeleteAndGetAllItems() {
        assertTrue(facade.getAllItems().containsAll(listOf(initItem1, initItem2)))
    }

    @Test
    fun testListItemByGroup() {
        assertTrue(facade.getAllItemsByGroup(initGroup1.id).containsAll(listOf(initItem1, initItem2)))
    }

    @Test
    fun testUpdateGroupName() {
        val newName = "NewTestUniqueGroup1Name"
        facade.updateGroupName(initGroup1.id, newName)

        assertEquals(newName, facade.getGroup(initGroup1.id)!!.name)
    }

    @Test
    fun testUpdateGroupDescription() {
        val newDescription = "NewTestUniqueGroup1Description"
        facade.updateGroupDescription(initGroup1.id, newDescription)

        assertEquals(newDescription, facade.getGroup(initGroup1.id)!!.description)
    }

    @Test
    fun testUpdateItemName() {
        val newName = "NewTestUniqueItem1Name"
        facade.updateItemName(initItem1.id, newName)

        assertEquals(newName, facade.getItem(initItem1.id)!!.name)
    }

    @Test
    fun testUpdateItemDescription() {
        val newDescription = "NewTestUniqueItem1Description"
        facade.updateItemDescription(initItem1.id, newDescription)

        assertEquals(newDescription, facade.getItem(initItem1.id)!!.description)
    }

    @Test
    fun testUpdateItemAmount() {
        val newAmount = 264221
        facade.updateAmount(initItem1.id, newAmount)

        assertEquals(newAmount, facade.getItem(initItem1.id)!!.amount)
    }

    @Test
    fun testUpdateItemCost() {
        val newCost = 264.221
        facade.updateCost(initItem1.id, newCost)

        assertEquals(newCost, facade.getItem(initItem1.id)!!.cost)
    }

    @Test
    fun testUpdateItemProducer() {
        val newProducer = "NewTestUniqueItem1Producer"
        facade.updateProducer(initItem1.id, newProducer)

        assertEquals(newProducer, facade.getItem(initItem1.id)!!.producer)
    }

    @Test
    fun testUpdateItemGroupId() {
        val newGroupId = initGroup2.id
        facade.updateGroupId(initItem1.id, newGroupId)

        assertEquals(newGroupId, facade.getItem(initItem1.id)!!.groupId)
    }

    @Test
    fun testDeleteItem() {
        facade.deleteItem(initItem1.id)
        assertNull(facade.getItem(initItem1.id))

    }
}