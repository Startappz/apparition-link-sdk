// swift-tools-version:5.8
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://api.github.com/repos/Startappz/apparition-link-sdk/releases/assets/215773237.zip"
let remoteKotlinChecksum = "dd8cd72fde1395819ffa92ecb8f94ac1837f0ff5b1106fe9e237f1ee38abe8cf"
let packageName = "ApparitionSDK"
// END KMMBRIDGE BLOCK

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v14)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)