package company

interface CompanyModel {
    fun addCompany(company: Company)
    fun getCompany(name: String): Company
}