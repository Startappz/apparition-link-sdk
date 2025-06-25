package com.startappz.apparition.models.response

import com.startappz.apparition.utils.ApLogger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject


/**
 *
 * Class providing the structure of a HTTP response as received from the Branch API.
 *
 * Supports the following methods:
 *
 *  * [ServerResponse.getTag]
 *  * [ServerResponse.getStatusCode]
 *  * [ServerResponse.setPost]
 *  * [ServerResponse.getObject]
 *  * [ServerResponse.getArray]
 */
class ServerResponse(
    val tag: String,
    val statusCode: Int,
    private val requestId_: String,
    val message: String
) {
    private var post_: Any? = null

    fun setPost(post: Any?) {
        post_ = post
    }

    val `object`: JsonObject
        get() {
            if (post_ is JsonObject) {
                return post_ as JsonObject
            }
            return JsonObject(emptyMap())
        }

    val array: JsonArray?
        get() {
            if (post_ is JsonArray) {
                return post_ as JsonArray
            }
            return null
        }

    val failReason: String
        get() {
            var causeMsg = ""
            try {
                val postObj: JsonObject = `object`

                if (postObj.isNotEmpty()) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(postObj.toString())
                    val errorMessage = errorResponse.error?.message?.trim()
                    if (!errorMessage.isNullOrEmpty()) {
                        causeMsg = "$errorMessage."
                    }
                }
            } catch (e: Exception) {
                ApLogger.w("Caught Exception ServerResponse getFailReason: ${e.message}")
            }
            return causeMsg
        }
}