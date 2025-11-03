// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "GallerySaver",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "GallerySaver",
            targets: ["GallerySaverPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "GallerySaverPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/GallerySaverPlugin"),
        .testTarget(
            name: "GallerySaverPluginTests",
            dependencies: ["GallerySaverPlugin"],
            path: "ios/Tests/GallerySaverPluginTests")
    ]
)