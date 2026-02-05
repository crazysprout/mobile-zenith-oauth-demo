//
//  AppDelegate.swift
//  zenith demo
//
//

import UIKit
import Zenith

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        // Staging
        ZenithApp.shared.setup(apiKey: "4bfa58a20f559c88a80ba6db4727e98b8c52f99aa501a0dbd8268c520d1bb373")
        
        // Production
        // ZenithApp.shared.setup(apiKey: "e67d72bdc76ebfe2b30ff30c16cb3ab1bef81243ca4fc344ee6ccedaa077d5c9")
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }


}

