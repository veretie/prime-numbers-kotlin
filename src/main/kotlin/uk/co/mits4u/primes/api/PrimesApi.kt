package uk.co.mits4u.primes.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.co.mits4u.primes.api.pojos.AlgorithmName
import uk.co.mits4u.primes.api.pojos.PrimesDto

@Tag(name = "prime-numbers", description = "Prime numbers service")
@RestController
interface PrimesApi {

    @Operation(summary = "Calculate primes in specified [floor, ceiling] range")
    @GetMapping(path = ["/numbers/primes"], produces = ["application/json"])
    fun getPrimesInRange(

        @Parameter(description = "more or equal [default 0]", required = false)
        @RequestParam(name = "floor", defaultValue = "0") floor: Int,

        @Parameter(description = "less or equal then 2^24 = 16777216", required = true)
        @RequestParam("ceiling") ceiling: Int,

        @Parameter(description = "algorithm", required = false)
        @RequestParam(name = "algorithm", defaultValue = "ERATOSTHENES") algorithmName: AlgorithmName

    ): PrimesDto

    @Operation(summary = "Check if provided number is prime")
    @GetMapping(path = ["/numbers/{number}/isPrime"], produces = ["application/json"])
    fun isPrime(

        @Parameter(description = "less or equal then 2^24 = 16777216", required = true)
        @PathVariable("number") numberToCheck: Int,

        @Parameter(description = "algorithm", required = false)
        @RequestParam(name = "algorithm", defaultValue = "ERATOSTHENES") algorithmName: AlgorithmName

    ): Boolean

}