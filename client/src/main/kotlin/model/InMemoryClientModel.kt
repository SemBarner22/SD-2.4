package model

import NetworkUtils
import dto.Client
import dto.ClientShare

class InMemoryClientModel(shareClient: NetworkUtils) : ClientModel {
    private val clientByName: MutableMap<String, Client> = HashMap()
    private val shareClient: NetworkUtils

    init {
        this.shareClient = shareClient
    }

    private fun checkContains(name: String) {
        require(clientByName.containsKey(name)) { "Client '$name' does not exist." }
    }

    @Synchronized
    override fun addClient(client: Client) {
        require(!clientByName.containsKey(client.name)) { "Client '" + client.name + "' already exists." }
        clientByName[client.name] = client
    }

    @Synchronized
    override fun getClient(name: String): Client? {
        checkContains(name)
        return clientByName[name]
    }

    @Synchronized
    override fun addFunds(name: String, delta: Double) {
        checkContains(name)
        clientByName[name]?.changeFunds(delta)
    }

    @Synchronized
    override fun hasShare(name: String, shareName: String, companyName: String, amount: Long): Boolean {
        return getClient(name)?.let { (it.clientShares.stream()
                .filter { share -> share.companyName == companyName && share.name == shareName }
                .mapToLong(ClientShare::amount).sum()
                >= amount) } ?: false
    }

    @Synchronized
    override fun buyOrSell(name: String, shareName: String, companyName: String, amount: Long) {
        require(!(amount < 0 && !this.hasShare(name, shareName, companyName, -amount))) { "Insufficient shares for selling." }
        val cost: Double = shareClient.modifyShare(shareName, companyName, -amount, 0.0)
        val client: Client = getClient(name)!!
        if (client.funds < cost * amount) {
            shareClient.modifyShare(shareName, companyName, amount, 0.0)
            throw IllegalArgumentException("Insufficient funds: " + client.funds.toString() + " < " + cost * amount)
        }
        client.changeFunds(-cost * amount)
        if (amount > 0) {
            client.add(ClientShare(shareName, companyName, amount))
        } else if (amount < 0) {
            client.remove(shareName, companyName, -amount)
        }
    }

    override fun totalValue(name: String): Double {
        val client: Client? = getClient(name)
        return client?.let { client.funds + client.clientShares.stream()
                .mapToDouble { share -> share.amount * shareClient.queryPrice(share.fullName) }
                .sum() } ?: 0.0
    }

    override fun queryPrice(shareName: String): Double {
        return shareClient.queryPrice(shareName)
    }
}