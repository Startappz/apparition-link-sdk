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
         url: "[ZIP FILE PATH]",
         checksum: "[Generated CHECKSUM]"
        ),
   ]
)
