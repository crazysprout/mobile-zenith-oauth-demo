//
//  ProfileViewController.swift
//  zenith demo
//
//

import UIKit
import Zenith

class ProfileViewController: UIViewController {

    var user: ZenithUserInfo?

    @IBOutlet weak var detailsLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        displayUserInfo()
    }

    @IBAction func onDeleteAccountTapped(_ sender: Any) {
        ZenithApp.shared.deleteAccount(viewController: self) { [weak self] error in
            guard let self = self else { return }
            let alert = UIAlertController(title: "Error", message: "Failed to delete account: \(error.localizedDescription)", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default))
            self.present(alert, animated: true)
        }
    }

    private func displayUserInfo() {
        guard let user = user else { return }
        
        var text = ""
        text += "ID: \(user.id ?? "-")\n"
        text += "Username: \(user.username ?? "-")\n"
        text += "Email: \(user.email ?? "-")\n"
        text += "Verified: \(user.isVerifiedProfile)\n"
        text += "Guest: \(user.isGuest)\n"
        // Add more fields as needed based on ZenithUserInfo
        
        detailsLabel.text = text
    }

    @IBAction func onCloseTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
}
