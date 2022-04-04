import dto.Client
import model.ClientModel
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.stream.Collectors

@RestController
class Controller(private val clientModel: ClientModel) {

    private fun execute(callable: Callable<String>): String? {
        return try {
            callable.call() + System.lineSeparator()
        } catch (t: Throwable) {
            "An error occurred: " + t.message + System.lineSeparator()
        }
    }

    @RequestMapping("/new-user")
    fun newUser(@RequestParam("name") name: String,
                @RequestParam(name = "funds", required = false, defaultValue = "0") funds: Double): String? {
        return execute {
            clientModel.addClient(Client(name, funds))
            "Client '$name' has been successfully added."
        }
    }

    @RequestMapping("/add-funds")
    fun addFunds(@RequestParam("name") name: String, @RequestParam("delta") delta: Double): String? {
        return execute {
            clientModel.addFunds(name, delta)
            "Funds have been successfully added to '$name'"
        }
    }

    @RequestMapping("/get-shares")
    fun getSharesList(@RequestParam("name") name: String?): String? {
        return execute {
            name?.let { it ->
                clientModel.getClient(it)?.clientShares?.stream()?.map {
                    share -> share.name + ": " + share.amount + " x " + clientModel.queryPrice(share.fullName)
                }?.collect(Collectors.joining(System.lineSeparator()))
            }
        }
    }

    @RequestMapping("/get-total")
    fun getTotalValue(@RequestParam("name") name: String): String? {
        return execute { name + "'s value is " + clientModel.totalValue(name) }
    }

    @RequestMapping("/buy-sell")
    fun buyOrSell(@RequestParam("name") name: String,
                  @RequestParam("share-name") shareName: String,
                  @RequestParam("company-name") companyName: String,
                  @RequestParam("delta") delta: Long): String? {
        return execute {
            if (delta < 0 && !clientModel.hasShare(name, shareName, companyName, -delta)) {
                return@execute "Client cannot sell this share." + System.lineSeparator()
            }
            clientModel.buyOrSell(name, shareName, companyName, delta)
            "$name successfully bought or sold $delta units of '$shareName'"
        }
    }
}