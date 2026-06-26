import SwiftUI
import AVFoundation
import AudioToolbox

struct QRScannerView: UIViewControllerRepresentable {
    
    @Binding var scannedCode: String
    
    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }
    
    func makeUIViewController(context: Context) -> ScannerViewController {
        
        let controller = ScannerViewController()
        controller.delegate = context.coordinator
        
        return controller
    }
    
    func updateUIViewController(
        _ uiViewController: ScannerViewController,
        context: Context
    ) {
    }
    
    class Coordinator: NSObject, ScannerViewControllerDelegate {
        
        var parent: QRScannerView
        
        init(parent: QRScannerView) {
            self.parent = parent
        }
        
        func didFind(code: String) {
            
            parent.scannedCode = code
            
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
        }
    }
}

protocol ScannerViewControllerDelegate: AnyObject {
    func didFind(code: String)
}

final class ScannerViewController: UIViewController {
    
    weak var delegate: ScannerViewControllerDelegate?
    
    private let session = AVCaptureSession()
    private var previewLayer: AVCaptureVideoPreviewLayer!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .black
        
        checkPermission()
    }
    
    private func checkPermission() {
        
        switch AVCaptureDevice.authorizationStatus(for: .video) {
            
        case .authorized:
            setupCamera()
            
        case .notDetermined:
            
            AVCaptureDevice.requestAccess(for: .video) { granted in
                
                DispatchQueue.main.async {
                    
                    if granted {
                        self.setupCamera()
                    }
                }
            }
            
        default:
            print("Нет доступа к камере")
        }
    }
    
    private func setupCamera() {
        
        guard let device = bestCamera() else {
            print("Камера не найдена")
            return
        }
        
        do {
            
            let input = try AVCaptureDeviceInput(device: device)
            
            if session.canAddInput(input) {
                session.addInput(input)
            }
            
            let output = AVCaptureMetadataOutput()
            
            if session.canAddOutput(output) {
                
                session.addOutput(output)
                
                output.setMetadataObjectsDelegate(
                    self,
                    queue: .main
                )
                
                output.metadataObjectTypes = [.qr]
            }
            
            previewLayer = AVCaptureVideoPreviewLayer(session: session)
            previewLayer.frame = view.layer.bounds
            previewLayer.videoGravity = .resizeAspectFill
            
            view.layer.addSublayer(previewLayer)
            
            DispatchQueue.global(qos: .userInitiated).async {
                self.session.startRunning()
            }
            
        } catch {
            print(error)
        }
    }
    
    private func bestCamera() -> AVCaptureDevice? {
        
        if let back = AVCaptureDevice.default(
            .builtInWideAngleCamera,
            for: .video,
            position: .back
        ) {
            return back
        }
        
        return AVCaptureDevice.default(for: .video)
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        previewLayer?.frame = view.bounds
    }
}

extension ScannerViewController: AVCaptureMetadataOutputObjectsDelegate {
    
    func metadataOutput(
        _ output: AVCaptureMetadataOutput,
        didOutput metadataObjects: [AVMetadataObject],
        from connection: AVCaptureConnection
    ) {
        
        guard let object = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let code = object.stringValue else {
            return
        }
        
        delegate?.didFind(code: code)
    }
}
