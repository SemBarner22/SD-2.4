package company

import exceptions.CompanyException

class InMemoryCompanyModel: CompanyModel {
    private val companies: MutableList<Company> = ArrayList()

    private fun getCompanyByName(name: String): Sequence<Company> {
        return companies.asSequence().filter { c: Company -> c.name == name }
    }

    override fun addCompany(company: Company) {
        if (!getCompanyByName(company.name).all {
                    it.name != company.name
        }) {
            throw CompanyException("Company already exists!")
        }
        companies.add(company)
    }

    override fun getCompany(name: String): Company {
        return getCompanyByName(name).firstOrNull() ?: throw CompanyException("No such Company")
    }

}