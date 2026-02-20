//
//  ViewController.swift
//  zenith demo
//
//

import UIKit
import Zenith

class ViewController: UIViewController {

    @IBOutlet weak var signInContainer: UIStackView!
    @IBOutlet weak var profileContainer: UIStackView!
    @IBOutlet weak var continueContainer: UIStackView!
    
    @IBOutlet weak var idLabel: UILabel!
    @IBOutlet weak var nameLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        checkPreviousSignIn()
    }

    private func checkPreviousSignIn() {
        if let type = ZenithApp.shared.lastSignInType {
            print("\(type.toString()) is last sign in")
            showContinueState()
        } else {
            showSignInState()
        }
    }
    
    // MARK: - State Management
    private func showSignInState() {
        DispatchQueue.main.async {
            self.signInContainer.isHidden = false
            self.profileContainer.isHidden = true
            self.continueContainer.isHidden = true
        }
    }
    
    private func showProfileState(user: ZenithUserInfo? = nil) {
        DispatchQueue.main.async {
            self.signInContainer.isHidden = true
            self.profileContainer.isHidden = false
            self.continueContainer.isHidden = true
            
            if let user = user {
                self.updateProfileInfo(user: user)
            }
            
            // IAP Button is now in Storyboard
        }
    }
    
    @IBAction func onIapTapped(_ sender: Any) {
        self.performSegue(withIdentifier: "showIAP", sender: nil)
    }
        
    private func updateProfileInfo(user: ZenithUserInfo) {
        idLabel.text = "ID: \(user.id ?? "")"
        nameLabel.text = "Username: \(user.username ?? "")"
    }
    
    private func showContinueState() {
        DispatchQueue.main.async {
            self.signInContainer.isHidden = true
            self.profileContainer.isHidden = true
            self.continueContainer.isHidden = false
        }
    }
    
    // MARK: - Actions
    @IBAction func onOAuthSignInTapped(_ sender: Any) {
        handleSignIn(type: .oauth, label: "OAuth")
    }
    
    @IBAction func onGuestSignInTapped(_ sender: Any) {
        handleSignIn(type: .guest, label: "Guest")
    }
    
    @IBAction func onSignOutTapped(_ sender: Any) {
        handleSignOut()
    }
    
    @IBAction func onContinueTapped(_ sender: Any) {
        showLoading(message: "Continuing Sign In...")
        
        ZenithApp.shared.continueSignIn { user in
            DispatchQueue.main.async {
                self.hideLoading {
                    self.showProfileState(user: user)
                }
            }
        } failure: { error in
            DispatchQueue.main.async {
                self.hideLoading {
                    print("Continue Sign In Failed: \(error)")
                    self.handleSignOut()
                }
            }
        }
    }
    
    @IBAction func onGetProfileTapped(_ sender: Any) {
        showLoading(message: "Loading Profile...")
        
        ZenithApp.shared.getProfile { user in
            DispatchQueue.main.async {
                self.hideLoading {
                    self.performSegue(withIdentifier: "showProfileDetail", sender: user)
                }
            }
        } failure: { error in
            DispatchQueue.main.async {
                self.hideLoading {
                    print("Get Profile Failed: \(error)")
                }
            }
        }
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showProfileDetail",
           let vc = segue.destination as? ProfileViewController,
           let user = sender as? ZenithUserInfo {
            vc.user = user
        }
    }
    
    // MARK: - Helpers
    private func handleSignIn(type: ZenithSignInType, label: String) {
        showLoading(message: "Signing in...")
        
        ZenithApp.shared.signIn(type: type, viewController: self) { [weak self] user in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.showProfileState(user: user)
                }
            }
        } cancel: { [weak self] in
            DispatchQueue.main.async {
                self?.hideLoading {
                    print("\(label) Sign In Cancelled")
                }
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.hideLoading {
                    print("\(label) Sign In Failed: \(error)")
                }
            }
        }
    }
    
    private func handleSignOut() {
        showLoading(message: "Signing out...")
        
        ZenithApp.shared.signout { [weak self] in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.showSignInState()
                }
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.hideLoading {
                    print("Sign Out Failed: \(error)")
                    // Force sign in state on failure as fallback
                    self?.showSignInState()
                }
            }
        }
    }
}

