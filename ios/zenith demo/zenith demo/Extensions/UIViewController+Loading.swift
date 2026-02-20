import UIKit

fileprivate var loadingIndicatorAlertKey: UInt8 = 0

fileprivate class LoadingState {
    var alert: UIAlertController?
    var isPresenting = false
    var needsDismiss = false
    var hideCompletion: (() -> Void)?
}

extension UIViewController {
    
    private var loadingState: LoadingState {
        if let state = objc_getAssociatedObject(self, &loadingIndicatorAlertKey) as? LoadingState {
            return state
        }
        let state = LoadingState()
        objc_setAssociatedObject(self, &loadingIndicatorAlertKey, state, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        return state
    }
    
    func showLoading(message: String) {
        DispatchQueue.main.async {
            let state = self.loadingState
            guard state.alert == nil else {
                state.alert?.message = message
                return
            }
            
            let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
            let loadingIndicator = UIActivityIndicatorView(frame: CGRect(x: 10, y: 5, width: 50, height: 50))
            loadingIndicator.hidesWhenStopped = true
            loadingIndicator.style = .medium
            loadingIndicator.startAnimating()
            
            alert.view.addSubview(loadingIndicator)
            state.alert = alert
            state.isPresenting = true
            state.needsDismiss = false
            state.hideCompletion = nil
            
            self.present(alert, animated: true, completion: {
                state.isPresenting = false
                
                if state.needsDismiss {
                    alert.dismiss(animated: true) {
                        let completion = state.hideCompletion
                        state.alert = nil
                        state.needsDismiss = false
                        state.hideCompletion = nil
                        completion?()
                    }
                }
            })
        }
    }
    
    func hideLoading(completion: (() -> Void)? = nil) {
        DispatchQueue.main.async {
            let state = self.loadingState
            guard let alert = state.alert else {
                completion?()
                return
            }
            
            if state.isPresenting {
                state.needsDismiss = true
                if let newCompletion = completion {
                    let oldCompletion = state.hideCompletion
                    state.hideCompletion = {
                        oldCompletion?()
                        newCompletion()
                    }
                }
            } else {
                alert.dismiss(animated: true) {
                    state.alert = nil
                    state.needsDismiss = false
                    state.hideCompletion = nil
                    completion?()
                }
            }
        }
    }
}
