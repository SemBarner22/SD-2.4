import company.Company
import share.Share
import company.CompanyModel
import share.ShareModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.stream.Collectors

@RestController
class Controller(private val shareDao: ShareModel, private val companyDao: CompanyModel) {

    private fun execute(callable: Callable<String>): ResponseEntity<*>? {
        return try {
            ResponseEntity(callable.call() + System.lineSeparator(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity("An error occurred: " + e.message + System.lineSeparator(), HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @RequestMapping("/new-company")
    fun newCompany(@RequestParam("name") name: String): ResponseEntity<*>? {
        return execute {
            companyDao.addCompany(Company(name))
            "Company '$name' has been successfully added."
        }
    }

    @RequestMapping("/new-share")
    fun newShare(@RequestParam("name") name: String,
                 @RequestParam("company") companyName: String,
                 @RequestParam("price") price: Double,
                 @RequestParam("amount") amount: Long): ResponseEntity<*>? {
        return execute {
            shareDao.addShare(Share(name, companyName, amount, price))
            "New share '$name' by '$companyName' has been successfully added."
        }
    }

    @RequestMapping("/share-info")
    fun shareInfo(): ResponseEntity<*>? {
        return execute {
            shareDao.allShares.stream()
                    .map { share ->
                        "'" + share.name + ":" + share.companyName + "', amount: " + share.amount.toString() + ", price: " + share.price
                    }
                    .collect(Collectors.joining(System.lineSeparator()))
        }
    }

    @RequestMapping("/change-share")
    fun changeShare(@RequestParam("name") name: String,
                    @RequestParam("company") companyName: String,
                    @RequestParam(name = "qdelta", required = false, defaultValue = "0") amountDelta: Long,
                    @RequestParam(name = "pdelta", required = false, defaultValue = "0") priceDelta: Double): ResponseEntity<*>? {
        return execute {
            val price: Double = shareDao.changeShare(name, companyName, amountDelta, priceDelta)
            "Successfully modified share '$name' by '$companyName', price: $price"
        }
    }
}