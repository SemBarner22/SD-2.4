package model

import dto.Client

interface ClientModel {
    fun addClient(client: Client)
    fun getClient(name: String): Client?
    fun addFunds(name: String, delta: Double)
    fun hasShare(name: String, shareName: String, companyName: String, amount: Long): Boolean
    fun buyOrSell(name: String, shareName: String, companyName: String, amount: Long)
    fun totalValue(name: String): Double
    fun queryPrice(shareName: String): Double
}
