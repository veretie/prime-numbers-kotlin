package uk.co.mits4u.primes.service.strategy

import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.co.mits4u.primes.service.PrimeStrategy
import org.slf4j.LoggerFactory

@Component("SUNDARAM")
class SundaramPrimeStrategy(
    @Value("\${primes.sundaram.thread.count}") val threadCount: Int
) : PrimeStrategy {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override suspend fun isPrime(numberToCheck: Int): Boolean {
        val primes = generatePrimes(numberToCheck)
        return primes.contains(numberToCheck)
    }

    override suspend fun generatePrimes(maxPrime: Int): List<Int> {

        if (maxPrime == 0 || maxPrime == 1) return listOf()
        val maxPrimeInclusive = maxPrime + 1
        val primeFlags = initAllAsPrime(maxPrimeInclusive)
        markNonPrimes(maxPrimeInclusive, primeFlags)
        return collectPrimes(primeFlags, maxPrimeInclusive)
    }

    private fun initAllAsPrime(limit: Int): BooleanArray {
        return BooleanArray(limit) { true }
    }

    private suspend fun markNonPrimes(limit: Int, primeFlags: BooleanArray) {

        val n = getSundaramLimit(limit)
        val ranges = splitToSlots(n, SLOT_COUNT)

        val job = GlobalScope.launch(Dispatchers.Default.limitedParallelism(threadCount)) {
            ranges.forEach { range ->
                launch {//new thread each time
                    markInRange(range, primeFlags, n)
                }
            }
        }

        try {
            job.join()
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(e)
        }

    }

    private fun markInRange(range: Pair<Int, Int>, primeFlags: BooleanArray, sundaramLimit: Int) {

        LOGGER.info("marking range [${range.first + 1}, ${range.second}]")

        for (i in range.first + 1..range.second) {
            val maxRightInclusive = (sundaramLimit - i) / (1 + 2 * i)
            for (j in i..maxRightInclusive) {
                primeFlags[i + j + 2 * i * j] = false
            }
        }

    }

    private fun collectPrimes(primeFlags: BooleanArray, limit: Int): List<Int> {

        val primes = mutableListOf(FIRST_PRIME)

        for (flagIndex in 1 until getSundaramLimit(limit)) {
            if (primeFlags[flagIndex]) {
                primes.add(2 * flagIndex + 1)
            }
        }

        return primes
    }

    private fun getSundaramLimit(limit: Int): Int {
        return limit / 2
    }

    private fun splitToSlots(n: Int, slots: Int): Collection<Pair<Int, Int>> {

        val slotSize = if (n % slots == 0) {
            n / slots
        } else {
            n / slots + 1
        }

        return generateSequence(0) { it + slotSize }
            .take(slots - 1)
            .map { Pair(it, if (it + slotSize < n) it + slotSize else n) }
            .toList()

    }

    companion object {
        private const val FIRST_PRIME = 2
        private const val SLOT_COUNT = 10
    }
}