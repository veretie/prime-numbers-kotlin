package uk.co.mits4u.primes.service

interface PrimeStrategyFactory {
    fun getStrategy(algorithmName: String): PrimeStrategy
}