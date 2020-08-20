package id.ac.esaunggul.breastcancerdetection.util.state

/**
 * An enum class representing the current status of resource data being manipulated and/or fetched.
 */
sealed class ResourceState<T>(
    val data: T? = null,
    val code: String? = null
) {

    class Success<T>(data: T) : ResourceState<T>(data)
    class Loading<T>(data: T? = null) : ResourceState<T>(data)
    class Error<T>(code: String?, data: T? = null) : ResourceState<T>(data, code)
}