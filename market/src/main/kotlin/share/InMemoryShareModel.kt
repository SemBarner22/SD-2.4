package share

import exceptions.ShareException
import java.util.*
import kotlin.collections.ArrayList

class InMemoryShareModel : ShareModel {
    private val sharesByCompanyName: MutableMap<String, MutableList<Share>> = HashMap<String, MutableList<Share>>()

    override fun addShare(share: Share) {
        val companyName: String = share.companyName
        if (getShare(companyName, share.name) == null) {
            throw ShareException("Share ${share.name} is already present")
        }
        sharesByCompanyName.putIfAbsent(companyName, ArrayList())
        sharesByCompanyName[companyName]!!.add(share)
    }

    override fun getShare(companyName: String, name: String) =
        sharesByCompanyName[companyName]?.asSequence()?.filter { share: Share ->
            share.name == name
        }?.firstOrNull()

    override val allShares: List<Share>
        get() {
            val result = mutableListOf<Share>()
            sharesByCompanyName.values.asSequence().flatMap {
                obj: List<Share> -> obj.asSequence()
            }.toCollection(result)
            return result
        }

    override fun changeShare(name: String, companyName: String,
                             amountDelta: Long, priceDelta: Double): Double {
        val share: Share = getShare(companyName, name) ?: throw ShareException("No such share in $companyName")
        val newAmount: Long = share.amount + amountDelta
        if (newAmount < 0) {
            throw ShareException("New amount should be larger than zero")
        }
        val newPrice: Double = share.price + priceDelta
        if (newPrice <= 0.0) {
            throw ShareException("New price should be larger than zero")
        }
        share.amount = newAmount
        share.price = newPrice
        return newPrice
    }
}