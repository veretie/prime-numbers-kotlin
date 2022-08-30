package uk.co.mits4u.primes.service

interface PrimeStrategy {

     suspend fun generatePrimes(limitingNumber: Int): List<Int>

     suspend fun isPrime(numberToCheck: Int): Boolean

}