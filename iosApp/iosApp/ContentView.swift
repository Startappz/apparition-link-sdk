import UIKit
import SwiftUI

import SwiftUI

struct ContentView: View {

    @ObservedObject var viewModel = APIViewModel()
    @State private var urlInput: String = ""

    var body: some View {
        
        VStack(spacing: 20) {

            Button(action: {
                Task {
                    await viewModel.register()
                }
            }) {
                Text("Register")
                    .font(.title3)
                    .frame(maxWidth: .infinity, minHeight: 40)
                    .background(Color.purple)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }

            TextField("Enter URL", text: $urlInput)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .frame(maxWidth: .infinity, minHeight: 60)

            Button(action: {
                Task {
                    await viewModel.fetchData(url: urlInput)
                }
            }) {
                Text("Expand")
                    .font(.title3)
                    .frame(maxWidth: .infinity, minHeight: 40)
                    .background(Color.purple)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }

            switch viewModel.result {
            case .loading:
                EmptyView()
            case .success(let response):
                Text("Response: \(response)")
                    .padding()

            case .failure(let errorMsg):
                Text("Error: \(errorMsg)")
                    .padding()
                    .foregroundColor(.red)
            }
        }
        .padding()
    }
}
