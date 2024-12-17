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
         url: "https://github.com/Startappz/apparition-link-sdk/releases/download/release%2F0.0.2/ApparitionSDK.xcframework.zip",
         checksum: "90762a0a7f41bd8b1395dec142e77cdd7e51561498b395e70b3d7dc59ca3d5a5"
        ),
   ]
)
