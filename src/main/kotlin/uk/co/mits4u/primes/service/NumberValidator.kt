package uk.co.mits4u.primes.service

import org.springframework.stereotype.Component

@Component
class NumberValidator {

    fun validateRange(floor: Int, ceiling: Int) {
        validateNumber(floor)
        validateNumber(ceiling)
        require(floor <= ceiling) { "floor [$floor] cannot be higher then ceiling [$ceiling]" }
    }

    fun validateNumber(number: Int) {
        require(number >= 0) { "prime cannot be negative. [$number] is invalid" }
        require(number <= MAX_NUMBER) { "[$number] is invalid. Select number <= 16777216 = 2^24. " }
    }

    companion object {
        const val MAX_NUMBER = 16777216
    }
}