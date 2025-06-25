package com.startappz.apparition.models

/**
 * Represents the possible states of the SDK's initialization process.
 *
 * This sealed class defines the different states that the SDK can be in,
 * allowing for a clear and type-safe way to manage the SDK's lifecycle.
 *
 * The possible states are:
 * - [NotInitialized]: The SDK has not yet been initialized. (install
 * - [Initialising]: The SDK is currently in the process of being initialized.
 * - [Initialised]: The SDK has been successfully initialized and is ready for use.
 */
internal sealed class SdkState {
    data object NotInitialized : SdkState()
    data object Initialised : SdkState()
    data object Initialising : SdkState()
}