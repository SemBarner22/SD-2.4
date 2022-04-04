import company.CompanyModel
import company.InMemoryCompanyModel
import share.InMemoryShareModel
import share.ShareModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Configuration {
    @Bean
    open fun companyDao(): CompanyModel {
        return InMemoryCompanyModel()
    }

    @Bean
    open fun shareDao(): ShareModel {
        return InMemoryShareModel()
    }
}