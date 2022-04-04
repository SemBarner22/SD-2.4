import org.junit.Assert
import org.junit.Test

class NetworkUtilsTest : BaseTest() {
    @Test
    fun testQueryPriceNoSuchShare() {
        Assert.assertThrows(IllegalArgumentException::class.java) { shareClient.queryPrice("no-such-share") }
    }

    @Test
    fun testModifyShareNoSuchShare() {
        Assert.assertThrows(RuntimeException::class.java) { shareClient.modifyShare("no-share", "no-company", 0, 0.0) }
    }

    @Test
    fun testModifyTooLowamount() {
        Assert.assertThrows(RuntimeException::class.java) { shareClient.modifyShare("s3", "yandex", -1000, 0.0) }
    }

    @Test
    fun testModifyTooLowPrice() {
        Assert.assertThrows(RuntimeException::class.java) { shareClient.modifyShare("s3", "yandex", 0, -1000.0) }
    }
}