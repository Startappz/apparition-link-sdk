//
//  SdkWrapper.swift
//  iosApp
//
//  Created by Rawan on 25/06/2025.
//

import Foundation
import Apparition

class SdkWrapper {

    static func initSdk() {
        ApparitionLinkSDK.shared.doInit(apiKey:"key_test_d0knI3CTvMZmXD6SUT17SQ")
        ApparitionLinkSDK.shared.setLogLevel(level: .debug)
    }

    static func expand(url: String) async throws -> String {
        do {
            let result = try await ApparitionLinkSDK.shared.expand(url: url)
            return result
        } catch {
            print(error)
            throw error
        }
    }

    static func register() async throws -> String {
        do {
            let collectedData = await collectFirstUserDataFromKotlin()

            if let userData = collectedData {
                try await ApparitionLinkSDK.shared.register(userData: userData)
                return "success registration"
            } else {
                throw NSError(domain: "AppError",
                              code: 1001,
                              userInfo: [NSLocalizedDescriptionKey: "No user data available."])
            }
        } catch {
            print("Error during registration: \(error)")
            throw error
        }
    }


    private static func collectFirstUserDataFromKotlin() async -> UserData? {
        do {
            let firstUserData = await Apparition.UserDataFactory().userData.first(where: { _ in true })
            return firstUserData
        }
    }
}
