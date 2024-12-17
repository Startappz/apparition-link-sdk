// swift-tools-version:5.3
import PackageDescription

let package = Package(
   name: "ApparitionSDK",
   platforms: [
     .iOS(.v14),
   ],
   products: [
      .library(name: "ApparitionSDK", targets: ["ApparitionSDK"])
   ],
   targets: [
      .binaryTarget(
         name: "ApparitionSDK",
         url: "https://github.com/Startappz/apparition-link-sdk/releases/download/release%2F0.0.1/ApparitionSDK.xcframework.zip",
         checksum: "d884621cf82c0bf6bd1e3ca101ad88cd9d35950f98ebbb0369d8b037488bc861"
        ),
   ]
)
