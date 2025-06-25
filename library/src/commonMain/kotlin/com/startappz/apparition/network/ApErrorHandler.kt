package com.startappz.apparition.network

/**
 * Returns a general error if the server back-end is down.
 */
class ApErrorHandler(failMsg: String, statusCode: Int) {
    /**
     *
     * Returns the message explaining the error.
     *
     * @return A [String] value that can be used in error logging or for dialog display
     * to the user.
     */
    var message: String = ""

    /**
     *
     * Returns an error code for this Branch Error.
     *
     * @return An [Integer] specifying  the error code for this error. Value will be one of the error code defined in branch errors.
     */
    var errorCode: Int = ERR_OTHER

    /**
     *
     * Overridden toString method for this object; returns the error message rather than the
     * object's address.
     *
     * @return A [String] value representing the object's current state.
     */
    override fun toString(): String {
        return message
    }

    init {
        message = failMsg + initErrorCodeAndGetLocalisedMessage(statusCode)
    }

    /*
     * <p> Provides localised error messages for the gives status code </p>
     *
     * @param status Http error code or Branch error codes
     *
     * @return A {@link String} with localised error message for the given status
     */
    private fun initErrorCodeAndGetLocalisedMessage(statusCode: Int): String {
        val errMsg: String
        if (statusCode == ERR_BRANCH_NO_CONNECTIVITY) {
            errorCode = ERR_BRANCH_NO_CONNECTIVITY
            errMsg = " Check network connectivity or DNS settings."
        } else if (statusCode == ERR_BRANCH_KEY_INVALID) {
            errorCode = ERR_BRANCH_KEY_INVALID
            errMsg =
                " Branch API Error: Please enter your branch_key in your project's manifest file first."
        } else if (statusCode == ERR_BRANCH_INIT_FAILED) {
            errorCode = ERR_BRANCH_INIT_FAILED
            errMsg =
                " Did you forget to call init? Make sure you init the session before making Branch calls."
        } else if (statusCode == ERR_NO_SESSION) {
            errorCode = ERR_NO_SESSION
            errMsg =
                " Unable to initialize Branch. Check network connectivity or that your branch key is valid."
        } else if (statusCode == ERR_NO_INTERNET_PERMISSION) {
            errorCode = ERR_NO_INTERNET_PERMISSION
            errMsg = " Please add 'android.permission.INTERNET' in your applications manifest file."
        } else if (statusCode == ERR_BRANCH_DUPLICATE_URL) {
            errorCode = ERR_BRANCH_DUPLICATE_URL
            errMsg =
                " Unable to create a URL with that alias. If you want to reuse the alias, make sure to submit the same properties for all arguments and that the user is the same owner."
        } else if (statusCode == ERR_API_LVL_14_NEEDED) {
            errorCode = ERR_API_LVL_14_NEEDED
            errMsg = "BranchApp class can be used only" +
                    " with API level 14 or above. Please make sure your minimum API level supported is 14." +
                    " If you wish to use API level below 14 consider calling getInstance(Context) instead."
        } else if (statusCode == ERR_BRANCH_NOT_INSTANTIATED) {
            errorCode = ERR_BRANCH_NOT_INSTANTIATED
            errMsg = "Branch instance is not created." +
                    " Make  sure your Application class is an instance of BranchLikedApp."
        } else if (statusCode == ERR_BRANCH_NO_SHARE_OPTION) {
            errorCode = ERR_BRANCH_NO_SHARE_OPTION
            errMsg =
                " Unable create share options. Couldn't find applications on device to share the link."
        } else if (statusCode == ERR_BRANCH_REQ_TIMED_OUT) {
            errorCode = ERR_BRANCH_REQ_TIMED_OUT
            errMsg = " Request to Branch server timed out. Please check your internet connectivity"
        } else if (statusCode == ERR_BRANCH_TRACKING_DISABLED) {
            errorCode = ERR_BRANCH_TRACKING_DISABLED
            errMsg =
                " Tracking is disabled. Requested operation cannot be completed when tracking is disabled"
        } else if (statusCode == ERR_BRANCH_ALREADY_INITIALIZED) {
            errorCode = ERR_BRANCH_ALREADY_INITIALIZED
            errMsg = " Session initialization already happened. To force a new session, " +
                    "set intent extra, \"branch_force_new_session\", to true."
        } else if (statusCode >= 500 || statusCode == ERR_BRANCH_UNABLE_TO_REACH_SERVERS) {
            errorCode = statusCode
            errMsg = " Unable to reach the Branch servers, please try again shortly."
        } else if (statusCode == 409 || statusCode == ERR_BRANCH_RESOURCE_CONFLICT) {
            errorCode = statusCode
            errMsg = " A resource with this identifier already exists."
        } else if (statusCode >= 400 || statusCode == ERR_BRANCH_INVALID_REQUEST) {
            errorCode = statusCode
            errMsg = " The request was invalid"
        } else if (statusCode == ERR_IMPROPER_REINITIALIZATION) {
            errorCode = ERR_IMPROPER_REINITIALIZATION
            errMsg =
                "Intra-app linking (i.e. session reinitialization) requires an intent flag, \"branch_force_new_session\"."
        } else if (statusCode == ERR_BRANCH_TASK_TIMEOUT) {
            errorCode = ERR_BRANCH_TASK_TIMEOUT
            errMsg = " Task exceeded timeout."
        } else {
            errorCode = ERR_OTHER
            errMsg = " See exception message or logs for more details. "
        }
        return errMsg
    }

    companion object {
        /* Error processing request since session not initialised yet. */
        const val ERR_NO_SESSION: Int = -101

        /* Error processing request since app doesn't have internet permission. */
        const val ERR_NO_INTERNET_PERMISSION: Int = -102

        /* Error processing request since Branch is not initialised. */
        const val ERR_BRANCH_INIT_FAILED: Int = -104

        /* Error processing request since alias is already used. */
        const val ERR_BRANCH_DUPLICATE_URL: Int = -105

        /* Error with API level below 14. */
        const val ERR_API_LVL_14_NEEDED: Int = -108

        /* Error Branch is not instantiated. */
        const val ERR_BRANCH_NOT_INSTANTIATED: Int = -109

        /* Error while creating share options. */
        const val ERR_BRANCH_NO_SHARE_OPTION: Int = -110

        /* Request Branch server timed out. */
        const val ERR_BRANCH_REQ_TIMED_OUT: Int = -111

        /* Request failed to hit branch servers */
        const val ERR_BRANCH_UNABLE_TO_REACH_SERVERS: Int = -112

        /* Request failed due to poor connectivity */
        const val ERR_BRANCH_NO_CONNECTIVITY: Int = -113

        /* Branch key is not specified or invalid */
        const val ERR_BRANCH_KEY_INVALID: Int = -114

        /* Request failed due to resource conflict */
        const val ERR_BRANCH_RESOURCE_CONFLICT: Int = -115

        /* Branch request is invalid */
        const val ERR_BRANCH_INVALID_REQUEST: Int = -116

        /* Tracking is disabled. Requested operations will not work when tracking is disabled */
        const val ERR_BRANCH_TRACKING_DISABLED: Int = -117

        /* Branch session is already initialized */
        const val ERR_BRANCH_ALREADY_INITIALIZED: Int = -118

        /* Reinitializing session without the flag, IntentKey.ForceNewBranchSession */
        const val ERR_IMPROPER_REINITIALIZATION: Int = -119

        /* Request task timed out before completing*/
        const val ERR_BRANCH_TASK_TIMEOUT: Int = -120

        /* Error when network request is made on main thread */
        const val ERR_NETWORK_ON_MAIN: Int = -121

        /* General error reporting */
        const val ERR_OTHER: Int = -122
    }
}
