package id.ac.esaunggul.breastcancerdetection.util.state

/**
 * An enum class representing the current authentication state.
 */
enum class AuthState {
    AUTHENTICATED,
    UNAUTHENTICATED,
    LOADING,
    INVALID,
    COLLIDE,
    WEAK,
    ERROR,
}