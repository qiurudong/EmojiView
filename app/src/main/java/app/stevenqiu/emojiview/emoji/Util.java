package app.stevenqiu.emojiview.emoji;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import app.stevenqiu.emojiview.MApplication;

/**
 * Created by Stevenqiu on 2016/8/23.
 */
public class Util {

    public static boolean isNull(Object aObject) {
        return null == aObject;
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }


    /**
     * 解压assets的zip压缩文件到指定目录
     * @param assetName 压缩文件名
     * @param outputDirectory 输出目录
     * @param isReWrite 是否覆盖
     * @throws IOException
     */
    public static void unZipAssetFile(String assetName, String outputDirectory, boolean isReWrite) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = MApplication.getAppContext().getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdirs();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    public static final boolean fileExists(String filePath) {
        if (filePath == null) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists())
            return true;
        return false;
    }

    public static void saveKeyboardHeight(int height, Activity paramActivity){
        SharedPreferences sp = MApplication.getAppContext().getSharedPreferences(Contsants.SPPATH, Context.MODE_PRIVATE);
        sp.edit().putInt(Contsants.SPKEY.KEYBORD_HEIGHT, height - Util.getStatusBarHeight(paramActivity)).apply();
    }
    /**屏幕分辨率高**/
    public static int getScreenHeight(Activity paramActivity) {
        Display display = paramActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }
    /**statusBar高度**/
    public static int getStatusBarHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.top;

    }
    /**可见屏幕高度**/
    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }
    /**关闭键盘**/
    public static void hideSoftInput(View paramEditText) {
        ((InputMethodManager) MApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
    }
    // below actionbar, above softkeyboard
    public static int getAppContentHeight(Activity paramActivity) {
        return Util.getScreenHeight(paramActivity) - Util.getStatusBarHeight(paramActivity)
                - Util.getActionBarHeight(paramActivity) - Util.getKeyboardHeight(paramActivity);
    }
    /**获取actiobar高度**/
    public static int getActionBarHeight(Activity paramActivity) {
        if (true) {
            return Util.dp2px(paramActivity, 56);
        }
        int[] attrs = new int[] { android.R.attr.actionBarSize };
        TypedArray ta = paramActivity.obtainStyledAttributes(attrs);
        return ta.getDimensionPixelSize(0, Util.dp2px(paramActivity, 56));
    }

    /**键盘是否在显示**/
    public static boolean isKeyBoardShow(Activity paramActivity) {
        int height = Util.getScreenHeight(paramActivity) - Util.getStatusBarHeight(paramActivity)
                - Util.getAppHeight(paramActivity);
        return height != 0;
    }
    /**显示键盘**/
    public static void showKeyBoard(final View paramEditText) {
        paramEditText.requestFocus();
        paramEditText.post(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) MApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramEditText, 0);
            }
        });
    }

    public static int getKeyboardHeight(Activity paramActivity) {
        int height = Util.getScreenHeight(paramActivity) - Util.getStatusBarHeight(paramActivity) - Util.getAppHeight(paramActivity);
        SharedPreferences sp = MApplication.getAppContext().getSharedPreferences(Contsants.SPPATH, Context.MODE_PRIVATE);
        if (height == 0) {
            height = sp.getInt(Contsants.SPKEY.KEYBORD_HEIGHT, 787);//787为默认软键盘高度 基本差不离
        }else{
            sp.edit().putInt(Contsants.SPKEY.KEYBORD_HEIGHT, height).apply();
        }
        Log.i("EmojiUtils", height+" keyboard");
        return height;
    }
}
