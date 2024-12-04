# Publish Swift Package

1. Build the framework
    ```shell
    ./gradlew :library:assembleApparitionSDKXCFramework
    ```
    The resulting framework will be created as the `library/build/XCFrameworks/release/ApparitionSDK.xcframework`
    folder in your project directory.
2. Compress the [ApparitionSDK.xcframework](library/build/XCFrameworks/release/ApparitionSDK.xcframework)
3. Generate checksum
    ```shell
    swift package compute-checksum ApparitionSDK.xcframework.zip
    ```
4. Upload to github after that copy from assets the zip file URL
5. Update `Package.swift` with the checksum and the copied URL
6. Push `Package.swift`
7. Update dependency from xcode

**References**
[Swift package export setup](https://kotlinlang.org/docs/native-spm.html#check-your-setup)