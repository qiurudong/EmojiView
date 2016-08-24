package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import app.stevenqiu.emojiview.MApplication;
import app.stevenqiu.emojiview.R;

/**
 * Created by Stevenqiu on 2016/8/23.
 */
public class EmotionModelAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<EmotionModel> mDatas;
    private int checkModelIndex = 0;
    private Context context;

    public EmotionModelAdapter(Context context, List<EmotionModel> datats) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = datats;
    }

    @Override
    public int getCount() {
        return null == mDatas ? 0 : mDatas.size();
    }
    @Override
    public Object getItem(int position) {
        return null == mDatas ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.emoji_model_item, null);
            holder.modelIconIv = (ImageView) convertView.findViewById(R.id.emoji_model_gv_item_iv);
            holder.modelIconLL = (LinearLayout) convertView.findViewById(R.id.emoji_model_gv_item_ll);
            convertView.setTag(holder);
        } else {
            holder=(ViewHolder)convertView.getTag();
        }
        MApplication.displayImage("file://"+EmojiFileManager.INSTANCE.getEmojiIconPath(mDatas.get(position).getModelId(), 0), holder.modelIconIv, -1, -1);
        if(checkModelIndex == position)
            holder.modelIconLL.setBackgroundColor(context.getResources().getColor(R.color.bm_emoji_modle_check_color));
        else
            holder.modelIconLL.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    public void updateModelCheck(int checkModelIndex){
        this.checkModelIndex = checkModelIndex;
    }


    private static class ViewHolder{
        ImageView modelIconIv;
        LinearLayout modelIconLL;
    }
}
