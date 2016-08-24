package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import java.io.IOException;

import app.stevenqiu.emojiview.MApplication;
import app.stevenqiu.emojiview.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 表情详情悬浮的popupwindow
 * Created by Stevenqiu on 2016/8/16.
 */
public class EmojiDetailPopWindow extends PopupWindow{
    private final String TAG = EmojiDetailPopWindow.class.getSimpleName();
    //详情中的gifImageview
    private GifImageView gifImageView;
    //声音播发player
    private Context context;
    private LinearLayout triangleLL;

    public EmojiDetailPopWindow(Context context){
        super(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.context = context;
        init();
    }

    private void init(){
        LayoutInflater layoutInflater = (LayoutInflater) MApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.emoji_detail_layout, null);
        setContentView(view);
        gifImageView = (GifImageView) view.findViewById(R.id.emoji_detail_gif);
        triangleLL = (LinearLayout) view.findViewById(R.id.triangle_ll);
    }

    /**
     * 显示表情详情的window
     * @param modelId
     * @param emojiId
     * @param itemPosition
     * @param parentView
     */
    public void showDetailWindow(long modelId, long emojiId, int itemPosition, View parentView){
        dismiss();
        //如果gif图片不存在则使用icon图片
        if(EmojiFileManager.INSTANCE.isEmojiGifFileExists(modelId, emojiId)){
            GifDrawable gifFromPath;
            try {
                gifFromPath = new GifDrawable(EmojiFileManager.INSTANCE.getEmojiGifPath(modelId, emojiId));
            } catch (IOException e) {
                Log.e(TAG, "emoji gif not found, model id = "+modelId+", emoji id ="+emojiId);
                e.printStackTrace();
                return;
            }
            gifFromPath.setLoopCount(0);
            gifImageView.setImageDrawable(gifFromPath);
        }else if(EmojiFileManager.INSTANCE.isEmojiIconFileExists(modelId, emojiId)){
            MApplication.displayImage("file://"+EmojiFileManager.INSTANCE.getEmojiIconPath(modelId, emojiId), gifImageView, -1, -1);
        }else{
            return;
        }
        int[] location = new int[2];
        parentView.getLocationOnScreen(location);
        //popupwindow悬浮的位置，左右两个悬浮框的箭头有所不同，中间的悬浮框箭头居中
        if(itemPosition % EmojiView.COLUMNS ==  0){
            triangleLL.setGravity(Gravity.LEFT);
            showAtLocation(parentView, Gravity.LEFT| Gravity.TOP,0, location[1]- context.getResources().getDimensionPixelSize(R.dimen.EmotionDetailViewModelHeight));
        }else if(itemPosition % EmojiView.COLUMNS  ==  EmojiView.COLUMNS - 1){
            triangleLL.setGravity(Gravity.RIGHT);
            showAtLocation(parentView, Gravity.RIGHT| Gravity.TOP, 0, location[1]- context.getResources().getDimensionPixelSize(R.dimen.EmotionDetailViewModelHeight));
        }else{
            triangleLL.setGravity(Gravity.CENTER_HORIZONTAL);
            showAtLocation(parentView, Gravity.NO_GRAVITY, location[0] -(context.getResources().getDimensionPixelSize(R.dimen.EmotionDetailViewModelWidth) - parentView.getWidth())/2, location[1]- context.getResources().getDimensionPixelSize(R.dimen.EmotionDetailViewModelHeight));
        }
    }

}
