
import org.junit.BeforeClass
import org.junit.ClassRule
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.GenericContainer
import java.util.Map
import java.util.function.Consumer

open class BaseTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        var shareWebServer: GenericContainer<Nothing> = FixedHostPortGenericContainer<Nothing>("market:1.0-SNAPSHOT")
                .withFixedExposedPort(8080, 8080)

        @BeforeClass
        @JvmStatic
        fun fillMarket() {
            shares.forEach { ( _, sharesList: List<Share>) ->
                sharesList.forEach(Consumer { share: Share ->
                    shareClient.doPostRequest("new-share", Map.of(
                            "name", share.name,
                            "company", share.companyName,
                            "amount", 10.toString(),
                            "price", share.price.toString()))
                }
                )
            }
        }


        internal val shareClient: NetworkUtils = Configuration().shareClient()
        private val companyNames = listOf("google", "yandex", "intel")
        internal val shares: MutableMap<String, List<Share>> = HashMap()
    }

    init {
        shares["google"] = listOf(
                Share("s1", "google", 200.0),
                Share("s2", "google", 1000.0)
        )
        shares["yandex"] = listOf(
                Share("s2", "yandex", 20.0),
                Share("s3", "yandex", 100.0)
        )
        shares["intel"] = listOf(
                Share("s3", "intel", 1500.0),
                Share("s4", "intel", 50.0)
        )
    }
    internal class Share constructor(val name: String, val companyName: String, val price: Double)
}