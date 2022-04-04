import model.ClientModel
import model.InMemoryClientModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Configuration {
    @Bean
    open fun clientDao(shareClient: NetworkUtils): ClientModel {
        return InMemoryClientModel(shareClient)
    }

    @Bean
    open fun shareClient(): NetworkUtils {
        return NetworkUtils("http://127.0.0.1:8080")
    }
}