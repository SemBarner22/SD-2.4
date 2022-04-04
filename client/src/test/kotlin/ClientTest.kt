import dto.Client
import dto.ClientShare
import model.ClientModel
import model.InMemoryClientModel
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.util.*


class ClientTest : BaseTest() {
    private fun newClient(name: String, funds: Double): Client {
        clientModel.addClient(Client(name, funds))
        val client: Client? = clientModel.getClient(name)
        Assert.assertTrue(client != null)
        client?.let {
            Assert.assertEquals(client.name, name)
            Assert.assertEquals(client.funds, funds, 0.0)
            Assert.assertEquals(client.clientShares, emptyList<ClientShare>())
        }
        return client!!
    }

    @Test
    fun testNewClient() {
        newClient(newRandomName(), 10.0)
    }

    @Test
    fun testNewClientAlreadyExists() {
        val name = newRandomName()
        newClient(name, 10.0)
        Assert.assertThrows(IllegalArgumentException::class.java) { newClient(name, 200.0) }
    }

    @Test
    fun testAddFunds() {
        val name = newRandomName()
        newClient(name, 50.0)
        clientModel.addFunds(name, 200.0)
        clientModel.getClient(name)?.let {
            Assert.assertEquals(250.0, it.funds, 0.0)
        } ?: assert(false)
    }

    @Test
    fun testBuySharesNotEnoughSharesInMarket() {
        val name = newRandomName()
        val client: Client = newClient(name, 100.0)
        Assert.assertThrows(RuntimeException::class.java) { clientModel.buyOrSell(name, "s1", "google", 1000) }
        Assert.assertEquals(100.0, client.funds, 0.0)
    }

    @Test
    fun testBuySharesNotEnoughSharesInClient() {
        val name = newRandomName()
        val client: Client = newClient(name, 100.0)
        Assert.assertThrows(RuntimeException::class.java) { clientModel.buyOrSell(name, "s1", "google", -1000) }
        Assert.assertEquals(100.0, client.funds, 0.0)
    }

    @Test
    fun testBuySharesNotEnoughFunds() {
        val name = newRandomName()
        val client: Client = newClient(name, 0.0)
        Assert.assertThrows(RuntimeException::class.java) { clientModel.buyOrSell(name, "s1", "google", 1) }
        Assert.assertEquals(0.0, client.funds, 0.0)
    }

    companion object {
        lateinit var clientModel: ClientModel
        private val random = Random(228)

        @BeforeClass
        @JvmStatic
        fun prepareClientModel() {
            clientModel = InMemoryClientModel(shareClient = shareClient)
        }

        private fun newRandomName(): String {
            val array = ByteArray(16)
            random.nextBytes(array)
            return String(array, StandardCharsets.UTF_8)
        }
    }
}