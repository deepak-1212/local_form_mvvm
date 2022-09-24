package test.interview.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.checkNumber(): Boolean {
    return this.toBigIntegerOrNull() != null
}

fun String.checkName(): Boolean {
    val pattern: Pattern = Pattern.compile("^[a-zA-Z\\s]*$")
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}