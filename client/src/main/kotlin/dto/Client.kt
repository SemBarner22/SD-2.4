package dto

import exceptions.ClientShareException

class Client(val name: String, funds: Double) {

    var funds: Double
    private set

    init {
        this.funds = funds
    }

    private val mapSharesByName: MutableMap<String, ClientShare> = HashMap()
    val clientShares: List<ClientShare>
        get() = ArrayList(mapSharesByName.values)

    fun changeFunds(delta: Double) {
        funds += delta
    }

    fun add(share: ClientShare) {
        if (mapSharesByName.containsKey(share.fullName)) {
            val clientShare: ClientShare? = mapSharesByName[share.fullName]
            clientShare?.changeAmount(share.amount) ?: throw ClientShareException("No such share")
        } else {
            mapSharesByName[share.fullName] = share
        }
    }

    fun remove(shareName: String, companyName: String, amount: Long) {
        val share: ClientShare? = mapSharesByName[ClientShare.getFullName(shareName, companyName)]
        share?.changeAmount(-amount) ?: throw ClientShareException("No such share")
        if (share.amount == 0L) {
            mapSharesByName.remove(ClientShare.getFullName(shareName, companyName))
        }
    }
}