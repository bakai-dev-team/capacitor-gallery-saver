import Foundation
import Capacitor
import Photos
import UIKit
import PDFKit // <— важно

@objc(GallerySaverPlugin)
public class GallerySaverPlugin: CAPPlugin {

    @objc func saveAuto(_ call: CAPPluginCall) {
        guard let uriStr = call.getString("uri"), let url = URL(string: uriStr) else {
            call.reject("uri is required")
            return
        }
        let album = call.getString("album") ?? "Bakai"

        let handle: (PHAuthorizationStatus) -> Void = { status in
            guard status == .authorized || status == .limited else {
                call.reject("Photo Library permission not granted")
                return
            }

            let ext = url.pathExtension.lowercased()
            if ext == "pdf" {
                self.savePdfFirstPageToPhotos(pdfUrl: url, album: album, call: call)
            } else {
                self.saveImageFileToPhotos(fileUrl: url, album: album, call: call)
            }
        }

        if #available(iOS 14, *) {
            PHPhotoLibrary.requestAuthorization(for: .addOnly) { status in
                handle(status)
            }
        } else {
            PHPhotoLibrary.requestAuthorization { status in
                handle(status)
            }
        }
    }

    private func saveImageFileToPhotos(fileUrl: URL, album: String, call: CAPPluginCall) {
        do {
            let data = try Data(contentsOf: fileUrl)
            guard let image = UIImage(data: data) else {
                call.reject("Not an image or invalid data")
                return
            }
            self.saveUIImage(image, album: album, call: call)
        } catch {
            call.reject("Read failed: \(error.localizedDescription)")
        }
    }

    private func savePdfFirstPageToPhotos(pdfUrl: URL, album: String, call: CAPPluginCall) {
        guard let doc = PDFDocument(url: pdfUrl), let page = doc.page(at: 0) else {
            call.reject("Cannot open PDF")
            return
        }
        let pageRect = page.bounds(for: .mediaBox)
        // масштаб: ширина ~1600 px
        let targetWidth: CGFloat = 1600
        let scale = targetWidth / pageRect.width
        let targetSize = CGSize(width: pageRect.width * scale, height: pageRect.height * scale)

        UIGraphicsBeginImageContextWithOptions(targetSize, true, 1.0)
        guard let ctx = UIGraphicsGetCurrentContext() else {
            UIGraphicsEndImageContext()
            call.reject("Graphics context failed")
            return
        }
        // белый фон
        UIColor.white.setFill()
        ctx.fill(CGRect(origin: .zero, size: targetSize))

        // масштаб и рендер PDF-страницы
        ctx.saveGState()
        ctx.scaleBy(x: scale, y: scale)
        ctx.translateBy(x: 0, y: pageRect.height)
        ctx.scaleBy(x: 1.0, y: -1.0)
        page.draw(with: .mediaBox, to: ctx)
        ctx.restoreGState()

        let img = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        guard let rendered = img else {
            call.reject("Render failed")
            return
        }
        self.saveUIImage(rendered, album: album, call: call)
    }

    private func saveUIImage(_ image: UIImage, album: String, call: CAPPluginCall) {
        PHPhotoLibrary.shared().performChanges({
            let req = PHAssetCreationRequest.forAsset()
            if let data = image.jpegData(compressionQuality: 0.92) {
                let options = PHAssetResourceCreationOptions()
                options.uniformTypeIdentifier = "public.jpeg"
                req.addResource(with: .photo, data: data, options: options)
            }
        }) { success, error in
            if success {
                call.resolve(["saved": true])
            } else {
                call.reject(error?.localizedDescription ?? "Save failed")
            }
        }
    }
}
