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
}
