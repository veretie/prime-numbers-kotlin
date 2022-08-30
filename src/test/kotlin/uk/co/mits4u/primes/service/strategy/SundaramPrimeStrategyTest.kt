package uk.co.mits4u.primes.service.strategy

class SundaramPrimeStrategyTest : AbstractPrimeStrategyTester(SundaramPrimeStrategy(threadCount = 3))