package uk.co.mits4u.primes.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.ArgumentMatchers.anyInt
import uk.co.mits4u.primes.api.pojos.AlgorithmName.ERATOSTHENES
import uk.co.mits4u.primes.api.pojos.AlgorithmName.SUNDARAM

class PrimesServiceTest {

    @InjectMocks
    private lateinit var primesService: PrimesService

    @Mock
    private lateinit var numberValidator: NumberValidator

    @Mock
    private lateinit var primeStrategyFactory: PrimeStrategyFactory

    @Mock
    private lateinit var eratosthenesPrimeStrategy: PrimeStrategy


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        runBlocking {
            Mockito.doNothing().`when`(numberValidator).validateNumber(anyInt())
            Mockito.`when`(primeStrategyFactory.getStrategy("ERATOSTHENES")).thenReturn(eratosthenesPrimeStrategy)
            Mockito.`when`(eratosthenesPrimeStrategy.isPrime(anyInt())).thenReturn(true)
            Mockito.`when`(eratosthenesPrimeStrategy.generatePrimes(anyInt()))
                .thenReturn(listOf(2, 3, 5, 7))
        }
    }

    @Test
    fun testIsPrime() {
        val isPrime = primesService.isPrime(1, ERATOSTHENES)
        Mockito.verify(numberValidator).validateNumber(1)
        Assertions.assertThat(isPrime).isTrue
    }

    @Test
    fun testIsPrimeWhenValidatorThrowsException() {
        Mockito.doThrow(IllegalArgumentException()).`when`(numberValidator).validateNumber(-1)
        Assertions.assertThatThrownBy { primesService.isPrime(-1, ERATOSTHENES) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testGetPrimesInRange() {
        val results = primesService.getPrimesInRange(1, 10, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(1, 10)
        Assertions.assertThat(results.primes).containsExactly(2, 3, 5, 7)
    }

    @Test
    fun testGetPrimesWithValidationException() {
        Mockito.doThrow(IllegalArgumentException()).`when`(numberValidator).validateRange(100, 1)
        Assertions.assertThatThrownBy { primesService.getPrimesInRange(100, 1, ERATOSTHENES) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testGetPrimesInRangeWithExactInclusiveRange() {
        val results = primesService.getPrimesInRange(2, 7, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(2, 7)
        Assertions.assertThat(results.primes).containsExactly(2, 3, 5, 7)
    }

    @Test
    fun testGetPrimesInRangeWithTrimmingResults() {
        val results = primesService.getPrimesInRange(3, 3, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(3, 3)
        Assertions.assertThat(results.primes).containsExactly(3)
    }

    @Test
    fun testUnknownStrategy() {
        Assertions.assertThatThrownBy { primesService.getPrimesInRange(1, 10, SUNDARAM) }
            .isInstanceOf(NullPointerException::class.java)
            .hasMessageContaining("Could note resolve prime strategy for 'SUNDARAM' algorithm")
    }

}