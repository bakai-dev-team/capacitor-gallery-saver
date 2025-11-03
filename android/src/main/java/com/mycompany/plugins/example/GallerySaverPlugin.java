package com.mycompany.plugins.example;

import android.content.ContentValues;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.InputStream;
import java.io.OutputStream;

@CapacitorPlugin(name = "GallerySaver")
public class GallerySaverPlugin extends Plugin {

    @PluginMethod
    public void saveAuto(PluginCall call) {
        String uriStr = call.getString("uri");
        if (uriStr == null || uriStr.isEmpty()) {
            call.reject("uri is required");
            return;
        }
        String album = call.getString("album");
        if (album == null) album = "Bakai";

        try {
            Uri uri = Uri.parse(uriStr);
            String path = uri.getPath() != null ? uri.getPath() : "";
            Uri savedUri;
            if (path.toLowerCase().endsWith(".pdf")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    call.reject("PDF not supported on this Android version");
                    return;
                }
                Bitmap img = renderPdfFirstPageToBitmap(uri);
                if (img == null) {
                    call.reject("PDF render failed");
                    return;
                }
                savedUri = saveBitmapToGallery(img, album);
            } else {
                savedUri = copyImageToGallery(uri, album);
            }
            JSObject ret = new JSObject();
            ret.put("saved", true);
            ret.put("uri", savedUri.toString());
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Save failed: " + e.getMessage(), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Bitmap renderPdfFirstPageToBitmap(Uri uri) throws Exception {
        ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "r");
        if (pfd == null) return null;
        PdfRenderer renderer = new PdfRenderer(pfd);
        if (renderer.getPageCount() == 0) {
            renderer.close();
            pfd.close();
            return null;
        }
        PdfRenderer.Page page = renderer.openPage(0);
        int targetWidth = 1600;
        float scale = targetWidth / (float) page.getWidth();
        Bitmap bmp = Bitmap.createBitmap(
                Math.max(1, (int) (page.getWidth() * scale)),
                Math.max(1, (int) (page.getHeight() * scale)),
                Bitmap.Config.ARGB_8888
        );
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();
        renderer.close();
        pfd.close();
        return bmp;
    }

    private Uri saveBitmapToGallery(Bitmap bitmap, String album) throws Exception {
        ContentResolver resolver = getContext().getContentResolver();
        String displayName = System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + album);
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri outUri = resolver.insert(collection, values);
        if (outUri == null) throw new Exception("MediaStore insert failed");

        try (OutputStream os = resolver.openOutputStream(outUri)) {
            if (os == null) throw new Exception("OpenOutputStream failed");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, os);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues fin = new ContentValues();
            fin.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(outUri, fin, null, null);
        }
        return outUri;
    }

    private Uri copyImageToGallery(Uri srcUri, String album) throws Exception {
        ContentResolver resolver = getContext().getContentResolver();
        String displayName = System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + album);
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri outUri = resolver.insert(collection, values);
        if (outUri == null) throw new Exception("MediaStore insert failed");

        try (InputStream input = resolver.openInputStream(srcUri);
             OutputStream output = resolver.openOutputStream(outUri)) {
            if (input == null || output == null) throw new Exception("Stream open failed");
            byte[] buf = new byte[8192];
            int len;
            while ((len = input.read(buf)) > 0) output.write(buf, 0, len);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues fin = new ContentValues();
            fin.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(outUri, fin, null, null);
        }
        return outUri;
    }
}
