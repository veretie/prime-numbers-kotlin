package uk.co.mits4u.primes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import uk.co.mits4u.primes.api.pojos.PrimesDto
import java.net.URI

@SpringBootTest(classes = [PrimesApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PrimesApplicationTests {

	@Value("\${local.server.port}")
	private val port = 0

	private lateinit var baseUrl: String
	private lateinit var template: TestRestTemplate

	@BeforeEach
	fun setUp() {
		baseUrl = "http://localhost:$port"
		template = TestRestTemplate()
	}

	@Test
	fun isPrimeForBiggestAllowed() {

		val url = URI("$baseUrl/numbers/16777213/isPrime")

		val response = template.getForEntity(url, Boolean::class.java)
		val isPrime = response.body
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		Assertions.assertThat(isPrime).isTrue
		
	}

	@Test
	fun isNonPrime() {
		
		val url = URI("$baseUrl/numbers/10/isPrime")
		
		val response = template.getForEntity(url, Boolean::class.java)
		val isPrime = response.body
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		Assertions.assertThat(isPrime).isFalse
		
	}

	@Test
	fun isPrimeWithChosenStrategy() {

		val url = URI("$baseUrl/numbers/101/isPrime?algorithm=SUNDARAM")

		val response = template.getForEntity(url, Boolean::class.java)
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		val isPrime = response.body
		Assertions.assertThat(isPrime).isTrue

	}

	@Test
	fun calculatePrimesWithDefaults() {

		val url = URI("$baseUrl/numbers/primes?ceiling=10")

		val response = template.getForEntity(url, PrimesDto::class.java)
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		val primes = response.getBody()
		Assertions.assertThat(primes?.primes).containsExactly(2, 3, 5, 7)

	}

	@Test
	fun calculatePrimesWithSpecifiedRangeAndAlgorithm() {

		val url = URI("$baseUrl/numbers/primes?floor=0&ceiling=10&algorithm=SUNDARAM")

		val response = template.getForEntity(url, PrimesDto::class.java)
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		val primes = response.getBody()
		Assertions.assertThat(primes?.primes).containsExactly(2, 3, 5, 7)

	}

	@Test
	fun calculatePrimesWithLetters() {

		val url = URI("$baseUrl/numbers/one/isPrime")

		val response = template.getForEntity(url, String::class.java)
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

	}

	@Test
	fun calculatePrimesForWrongRange() {

		val url = URI("$baseUrl/numbers/primes?floor=10&ceiling=1")

		val response = template.getForEntity(url, String::class.java)
		Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
		val responseBody = response.body
		Assertions.assertThat(responseBody).contains("floor [10] cannot be higher then ceiling [1]")

	}

}
