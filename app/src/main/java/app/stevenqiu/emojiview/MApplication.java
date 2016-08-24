package app.stevenqiu.emojiview;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Stevenqiu on 2016/8/23.
 */
public class MApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
        context = this;
    }

    public void init(Context context) {
        //创建默认的ImageLoader配置参数
//      ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.diskCacheFileCount(5000);
        builder.diskCacheSize(50*1024*1024);
        ImageLoaderConfiguration configuration = builder.build();

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }


    public static void displayImage(String uri, ImageView view, int loadingResId, int failResId) {
        if (TextUtils.isEmpty(uri) || (null == view)) {
            return;
        }
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        if (0 < loadingResId) {
            builder.showImageOnLoading(loadingResId);
        }
        if (0 < failResId) {
            builder.showImageOnFail(failResId);
        }
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        ImageLoader.getInstance().displayImage(uri, view, builder.build());
    }

    public static Context getAppContext() {
        return context;
    }
}
