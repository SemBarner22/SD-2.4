package share

data class Share(
    val name: String,
    val companyName: String,
    var amount: Long,
    var price: Double
)
