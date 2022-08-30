package uk.co.mits4u.primes.service.strategy

import org.springframework.stereotype.Component
import uk.co.mits4u.primes.service.PrimeStrategy

@Component("ERATOSTHENES")
class EratosthenesPrimeStrategy : PrimeStrategy {

    override suspend fun isPrime(numberToCheck: Int): Boolean {
        val primes = generatePrimes(numberToCheck)
        return primes.contains(numberToCheck)
    }

    override suspend fun generatePrimes(maxPrime: Int): List<Int> {

        if (maxPrime == 0) return listOf()

        val primeFlags = initAllAsPrime(maxPrime)

        markNonPrimes(maxPrime, primeFlags)

        val primes = collectPrimes(primeFlags)

        return primes

    }

    private fun initAllAsPrime(limit: Int): BooleanArray {
        return BooleanArray(limit + 1) { true }
    }

    private fun markNonPrimes(limit: Int, primeFlags: BooleanArray) {

        val maxPotentialPrime = Math.sqrt(limit.toDouble()).toInt()

        generateSequence(FIRST_PRIME) { it + 1 }
            .take(maxPotentialPrime - 1)
            .forEach { markMultiplesAsNonPrimes(it, primeFlags) }

    }

    private fun markMultiplesAsNonPrimes(prime: Int, primeFlags: BooleanArray) {
        var i = prime * prime
        while (i < primeFlags.size) {
            primeFlags[i] = false
            i += prime
        }
    }

    private fun collectPrimes(primeFlags: BooleanArray): List<Int> {

        val results = mutableListOf<Int>()
        for (i in FIRST_PRIME until primeFlags.size) {
            if (primeFlags[i]) results.add(i)
        }

        return results
    }

    companion object {
        private const val FIRST_PRIME = 2
    }
}