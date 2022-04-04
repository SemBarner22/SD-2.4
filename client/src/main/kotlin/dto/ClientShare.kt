package dto

class ClientShare(val name: String, val companyName: String, amount: Long) {

    var amount: Long
    private set

    init {
        this.amount = amount
    }

    val fullName: String = getFullName(name, companyName)

    fun changeAmount(amountDelta: Long) {
        amount += amountDelta
    }

    companion object {
        fun getFullName(shareName: String, companyName: String): String {
            return "$shareName:$companyName"
        }
    }
}
