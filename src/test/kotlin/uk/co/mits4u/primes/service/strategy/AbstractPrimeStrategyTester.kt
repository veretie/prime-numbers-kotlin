package uk.co.mits4u.primes.service.strategy

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.co.mits4u.primes.service.PrimeStrategy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AbstractPrimeStrategyTester(private val primeStrategy: PrimeStrategy) {

    companion object {

        @JvmStatic
        fun primes() =
            intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)

        @JvmStatic
        fun nonPrimes() = intArrayOf(0, 1, 4, 6, 8, 9, 10, 12, 14, 16, 18, 20)

    }

    @MethodSource("primes")
    @ParameterizedTest
    fun testIsPrime(prime: Int) = runTest {
        val isPrime = primeStrategy.isPrime(prime)
        Assertions.assertThat(isPrime).`as`("checking prime $prime").isTrue
    }

    @MethodSource("nonPrimes")
    @ParameterizedTest
    fun testIsNotPrime(nonPrime: Int) = runTest {
        val isPrime = primeStrategy.isPrime(nonPrime)
        Assertions.assertThat(isPrime).`as`("checking non prime $nonPrime").isFalse()
    }

    @Test
    fun testGeneratePrimes() = runTest {
        val primeResults = primeStrategy.generatePrimes(100)
        assertIterableEquals(primes().toList(), primeResults)
    }

    @Test
    fun testGeneratePrimesZero() = runTest {
        val expectedResults = primeStrategy.generatePrimes(0)
        Assertions.assertThat(expectedResults).isEmpty()
    }

    @Test
    fun testGeneratePrimesOne() = runTest {
        val expectedResults = primeStrategy.generatePrimes(1)
        Assertions.assertThat(expectedResults).isEmpty()
    }

    @Test
    fun testGeneratePrimesTwo() = runTest {
        val expectedResults = primeStrategy.generatePrimes(2)
        Assertions.assertThat(expectedResults).containsExactly(2)
    }

    @Test
    fun testIsPrimeBigNumbers() = runTest {
        for (nonPrime in listOf(16777213, 15485867)) {
            val isPrime = primeStrategy.isPrime(nonPrime)
            Assertions.assertThat(isPrime).`as`("checking big prime $nonPrime").isTrue()
        }
    }

    @Test
    fun testIsNotPrimeBigNumbers() = runTest {
        for (nonPrime in listOf(16777216, 15485866)) {
            val isPrime = primeStrategy.isPrime(nonPrime)
            Assertions.assertThat(isPrime).`as`("checking big non prime $nonPrime").isFalse()
        }
    }

    @Test
    fun testIsPrimeBigNumbersSimultaneously() = runTest {

        val asyncResults = mutableMapOf<Int, Deferred<Boolean>>()
        for (prime in listOf(16777213, 15485867)) {
            asyncResults.put(prime, async { primeStrategy.isPrime(prime) })
        }

        for (entry in asyncResults) {
            val number = entry.key
            val isPrime = entry.value.await()
            Assertions.assertThat(isPrime).`as`("checking big non prime $number").isTrue
        }

    }

    @Test
    fun testIsNotPrimeBigNumbersSimultaneously() = runTest {

        val asyncResults = mutableMapOf<Int, Deferred<Boolean>>()
        for (nonPrime in listOf(16777216, 15485866)) {
            asyncResults.put(nonPrime, async { primeStrategy.isPrime(nonPrime) })
        }

        for (entry in asyncResults) {
            val number = entry.key
            val isPrime = entry.value.await()
            Assertions.assertThat(isPrime).`as`("checking big non prime $number").isFalse()
        }

    }
}