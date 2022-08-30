package uk.co.mits4u.primes.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
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

    @Captor
    private lateinit var numberCaptor: ArgumentCaptor<Int>

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
        assertThat(isPrime).isTrue
    }

    @Test
    fun testIsPrimeWhenValidatorThrowsException() {
        Mockito.doThrow(IllegalArgumentException()).`when`(numberValidator).validateNumber(-1)
        val thrownException = assertThrows(IllegalArgumentException::class.java) {
            primesService.isPrime(-1, ERATOSTHENES)
        }
        assertTrue(thrownException is IllegalArgumentException)
    }

    @Test
    fun testGetPrimesInRange() {
        val results = primesService.getPrimesInRange(1, 10, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(1, 10)
        assertThat(results.primes).containsExactly(2, 3, 5, 7)
    }

    @Test
    fun testGetPrimesWithValidationException() {
        Mockito.doThrow(IllegalArgumentException()).`when`(numberValidator).validateRange(100, 1)
        assertThatThrownBy { primesService.getPrimesInRange(100, 1, ERATOSTHENES) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testGetPrimesInRangeWithExactInclusiveRange() {
        val results = primesService.getPrimesInRange(2, 7, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(2, 7)
        assertAll(
            { assertThat(results.primes).hasSize(4) },
            { assertThat(results.primes).containsExactly(2, 3, 5, 7) }
        )

    }

    @Test
    fun testGetPrimesInRangeWithTrimmingResults() {
        val results = primesService.getPrimesInRange(3, 4, ERATOSTHENES)
        Mockito.verify(numberValidator).validateRange(numberCaptor.capture(), numberCaptor.capture())
        assertAll(
            { assertThat(numberCaptor.allValues).containsExactly(3, 4) },
            { assertThat(results.primes).containsExactly(3) }
        )

    }

    @Test
    fun testUnknownStrategy() {
        assertThatThrownBy { primesService.getPrimesInRange(1, 10, SUNDARAM) }
            .isInstanceOf(NullPointerException::class.java)
            .hasMessageContaining("Could note resolve prime strategy for 'SUNDARAM' algorithm")
    }

}