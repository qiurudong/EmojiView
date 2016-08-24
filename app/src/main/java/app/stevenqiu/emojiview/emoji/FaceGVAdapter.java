package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.stevenqiu.emojiview.MApplication;
import app.stevenqiu.emojiview.R;

/**
 * Created by Stevenqiu on 2016/8/23.
 */
public class FaceGVAdapter extends BaseAdapter {
    private List<Emotion> list;
    private long emojiCaseId;
    private Context mContext;
    private EmojiDetailPopWindow popWindow;
    private boolean isTouchCancle = false;
    private boolean isTouchDown = false;
    private OnEmojiClickListener listener;
    private android.os.Handler handler = new android.os.Handler(Looper.myLooper());

    public FaceGVAdapter(long emojiCaseId, List<Emotion> list, Context mContext, EmojiDetailPopWindow popWindow, OnEmojiClickListener listener) {
        this.list = list;
        this.emojiCaseId = emojiCaseId;
        this.mContext = mContext;
        this.popWindow = popWindow;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chatting_emoji_item_ui, null);
            holder.iconIv = (ImageView) convertView.findViewById(R.id.chatting_emoji_item_icon);
            holder.titleTv = (TextView) convertView.findViewById(R.id.chatting_emoji_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MApplication.displayImage("file://"+EmojiFileManager.INSTANCE.getEmojiIconPath(emojiCaseId, list.get(position).getEmotionId()), holder.iconIv, -1, -1);
        holder.titleTv.setText(list.get(position).getEmotionTitleCN());
        holder.iconIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                //当按下时处理
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouchDown = true;
                    isTouchCancle = false;
                    //设置表情view按下的背景
                    v.setBackgroundResource(R.drawable.emoji_item_click);
                    //表情view按下时间超过300毫秒则显示表情详情悬浮框
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isTouchDown && !isTouchCancle)
                                popWindow.showDetailWindow(emojiCaseId, list.get(position).getEmotionId(), position, v);
                        }
                    }, 300);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {// 松开处理
                    //去除按下背景
                    v.setBackgroundColor(Color.TRANSPARENT);
                    isTouchDown = false;
                    popWindow.dismiss();
                    //表情按下时间少于200毫秒则默认为点击事件
                    if(event.getEventTime() - event.getDownTime() < 200 && !isTouchCancle && !Util.isNull(listener)){
                        listener.onEmojiClick(emojiCaseId, list.get(position));
                    }
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    popWindow.dismiss();
                    v.setBackgroundColor(Color.TRANSPARENT);
                    isTouchDown = false;
                    isTouchCancle = true;
                }
                return true;
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView iconIv;
        TextView titleTv;
    }
}