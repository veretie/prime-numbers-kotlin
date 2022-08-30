package uk.co.mits4u.primes.config

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.mits4u.primes.service.PrimeStrategyFactory

@Configuration
class AppConfig {

    @Bean
    fun primeStrategyFactory(): ServiceLocatorFactoryBean {
        val locatorFactoryBean = ServiceLocatorFactoryBean()
        locatorFactoryBean.setServiceLocatorInterface(PrimeStrategyFactory::class.java)
        return locatorFactoryBean
    }

}