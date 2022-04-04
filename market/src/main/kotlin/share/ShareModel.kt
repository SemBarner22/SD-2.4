package share

interface ShareModel {
    fun addShare(share: Share)
    fun getShare(companyName: String, name: String): Share?
    val allShares: List<Share>

    fun changeShare(name: String, companyName: String, amountDelta: Long, priceDelta: Double): Double
}