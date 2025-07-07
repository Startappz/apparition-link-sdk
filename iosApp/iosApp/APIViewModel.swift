//
//  APIViewModel.swift
//  iosApp
//
//  Created by Rawan on 25/06/2025.
//

import Foundation

import Foundation

enum APIResult<T> {
    case loading
    case success(T)
    case failure(Error)
}

class APIViewModel: ObservableObject {
    @Published var result: APIResult<String> = .loading

    init() {
        SdkWrapper.initSdk()
    }

    @MainActor
    func fetchData(url: String) async {
        self.result = .loading
        do {
            let response = try await SdkWrapper.expand(url: url)
            self.result = .success(response)
        } catch {
            self.result = .failure(error)
        }
    }

    @MainActor
    func register() async {
        self.result = .loading
        do {
            let response = try await SdkWrapper.register()
            self.result = .success(response)
        } catch {
            self.result = .failure(error)
        }
    }
}
