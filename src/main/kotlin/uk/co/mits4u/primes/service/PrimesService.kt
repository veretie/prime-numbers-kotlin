package uk.co.mits4u.primes.service

import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.co.mits4u.primes.api.PrimesApi
import uk.co.mits4u.primes.api.pojos.AlgorithmName
import uk.co.mits4u.primes.api.pojos.PrimesDto
import javax.annotation.Resource

@Service
class PrimesService : PrimesApi {

    private val LOGGER = LoggerFactory.getLogger(PrimesService::class.java)

    @Resource
    private lateinit var numberValidator: NumberValidator

    @Resource
    private lateinit var primeStrategyFactory: PrimeStrategyFactory

    override fun isPrime(numberToCheck: Int, algorithmName: AlgorithmName): Boolean {

        LOGGER.info("starting isPrime with number=[$numberToCheck], algorithm=[$algorithmName]")
        val start = System.currentTimeMillis()
        numberValidator.validateNumber(numberToCheck)

        val primeStrategy = resolveStrategy(algorithmName)

        val isPrime = runBlocking { primeStrategy.isPrime(numberToCheck) }

        val timeTaken = System.currentTimeMillis() - start
        LOGGER.info("finished isPrime with number=[$numberToCheck], algorithm=[$algorithmName in $timeTaken ms -> result: [$isPrime]")

        return isPrime
    }

    override fun getPrimesInRange(floor: Int, ceiling: Int, algorithmName: AlgorithmName): PrimesDto {

        LOGGER.info("starting calculation with range=[$floor, $ceiling], algorithm=[$algorithmName]")
        val start = System.currentTimeMillis()
        numberValidator.validateRange(floor, ceiling)

        val primeStrategy = resolveStrategy(algorithmName)

        val allPrimes = runBlocking { primeStrategy.generatePrimes(ceiling) }

        val primesInRange = allPrimes.filter { it in floor..ceiling }

        val timeTaken = System.currentTimeMillis() - start
        LOGGER.info("ended calculation with range=[$floor, $ceiling], algorithm=[$algorithmName] in $timeTaken ms -> found ${primesInRange.size} primes")

        return PrimesDto(primesInRange)

    }

    private fun resolveStrategy(algorithm: AlgorithmName): PrimeStrategy {
        val primeStrategy = primeStrategyFactory.getStrategy(algorithm.name)
        Validate.notNull(primeStrategy, "Could note resolve prime strategy for '$algorithm' algorithm")
        return primeStrategy
    }


}