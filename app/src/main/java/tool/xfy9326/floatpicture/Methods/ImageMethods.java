package tool.xfy9326.floatpicture.Methods;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.HashMap;

import tool.xfy9326.floatpicture.MainApplication;
import tool.xfy9326.floatpicture.Utils.Config;
import tool.xfy9326.floatpicture.View.FloatImageView;

public class ImageMethods {

    public static String setNewImage(Activity activity, Uri uri) {
        String md5 = CodeMethods.getFileMD5String(activity, uri);
        if (md5 == null) return null;

        Bitmap bitmap = IOMethods.readImageByUri(activity, uri);
        if (bitmap != null) {
            String path = Config.DEFAULT_PICTURE_DIR + md5;
            IOMethods.saveBitmap(bitmap, 100, path);
            return md5;
        }
        return null;
    }

    public static Bitmap getShowBitmap(Context context, String pictureId) {
        String path = Config.DEFAULT_PICTURE_DIR + pictureId;
        File file = new File(path);
        if (file.exists()) {
            return BitmapFactory.decodeFile(path);
        }
        return null;
    }

    public static Bitmap getPreviewBitmap(Context context, String pictureId) {
        return getShowBitmap(context, pictureId);
    }

    public static boolean isPictureFileExist(String pictureId) {
        String path = Config.DEFAULT_PICTURE_DIR + pictureId;
        File file = new File(path);
        return file.exists();
    }

    public static Bitmap getEditBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) return null;
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    public static FloatImageView getFloatImageViewById(Context context, String pictureId) {
        MainApplication app = (MainApplication) context.getApplicationContext();
        HashMap<String, View> register = app.getRegister();
        if (register.containsKey(pictureId)) {
            return (FloatImageView) register.get(pictureId);
        }
        return null;
    }

    public static void saveFloatImageViewById(Context context, String pictureId, FloatImageView view) {
        MainApplication app = (MainApplication) context.getApplicationContext();
        HashMap<String, View> register = app.getRegister();
        register.put(pictureId, view);
        view.setPictureId(pictureId);
    }

    public static float getDefaultZoom(Context context, Bitmap bitmap, boolean b) {
        if (bitmap == null) return 1.0f;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        float widthRatio = (float) dm.widthPixels / bitmap.getWidth();
        float heightRatio = (float) dm.heightPixels / bitmap.getHeight();
        float zoom = Math.min(widthRatio, heightRatio);
        return Math.min(zoom, 1.0f);
    }

    public static FloatImageView createPictureView(Context context, Bitmap bitmap, boolean touch_and_move, boolean allow_picture_over_layout, float zoom_x, float zoom_y, float degree) {
        FloatImageView view = new FloatImageView(context);
        view.setImageBitmap(resizeBitmap(bitmap, zoom_x, zoom_y, degree));
        view.setMoveable(touch_and_move);
        view.setOverLayout(allow_picture_over_layout);
        return view;
    }

    // Overload for backward compatibility
    public static FloatImageView createPictureView(Context context, Bitmap bitmap, boolean touch_and_move, boolean allow_picture_over_layout, float zoom, float degree) {
        return createPictureView(context, bitmap, touch_and_move, allow_picture_over_layout, zoom, zoom, degree);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float zoom_x, float zoom_y, float degree) {
        if (bitmap == null) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if (zoom_x != 0 && zoom_y != 0) {
            matrix.postScale(zoom_x, zoom_y);
        }
        if (degree != -1) {
            matrix.postRotate(degree);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    // Backward compatibility
    public static Bitmap resizeBitmap(Bitmap bitmap, float zoom, float degree) {
        return resizeBitmap(bitmap, zoom, zoom, degree);
    }

    public static void clearAllTemp(Context context, String id) {
        String path = Config.DEFAULT_PICTURE_DIR + id;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
