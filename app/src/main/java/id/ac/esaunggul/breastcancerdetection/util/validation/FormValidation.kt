package id.ac.esaunggul.breastcancerdetection.util.validation

/**
 * A singleton class that implement a set of common form validation.
 */
object FormValidation {

    /**
     * Check if the email being inputted is valid or not.
     * @param email String of the email being validated.
     */
    fun isEmailNotValid(email: String?): Boolean {
        if (email == null) {
            return true
        }
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Check if the password being inputted is too weak or not.
     * @param password String of the password being validated.
     */
    fun isPasswordWeak(password: String?): Boolean {
        if (password == null) {
            return true
        }
        return password.isEmpty() || password.length < 5
    }

    /**
     * Check if the name being inputted is valid or not.
     * @param name String of the name being validated.
     */
    fun isNameNotValid(name: String?): Boolean {
        if (name == null) {
            return true
        }
        return !name.matches(nameRegex)
    }

    private val nameRegex: Regex =
        "^(?:[\\p{L}\\p{Mn}\\p{Pd}'\\x{2019}]+\\s[\\p{L}\\p{Mn}\\p{Pd}'\\x{2019}]+\\s?)+\$".toRegex()
}