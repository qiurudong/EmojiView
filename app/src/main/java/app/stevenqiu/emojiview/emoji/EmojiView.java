package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import app.stevenqiu.emojiview.R;

/**
 * 表情view
 */
public class EmojiView extends FrameLayout implements AdapterView.OnItemClickListener, OnEmojiClickListener{
    //表情滑动的viewPager
    private ViewPager faceViewPager;
    //每套表情对应的下标圆点
    private ViewFlipperIndicator indicatorView;
    //表情view集合
    private ArrayList<View> ViewPagerItems = new ArrayList<>();
    //表情列数
    public static final int COLUMNS = 4;
    //表情行数
    public static int ROWS = 2;
    //表情数据
    private List<EmotionModel> emojiCaseList;
    private OnEmojiClickListener listener;
    //当前表情套的下标
    private int currEmojiModelPageIndex  = 0;
    //当前表情页的下标
    private int currEmojiPageIndex = 0;
    //记录每套表情页数
    private int[] emojiModelPageRecord ;
    //表情套的grid view，各个item对应各套表情
    private GridView emojiModelGv;
    //表情套的grid view对应的adapter
    private EmotionModelAdapter emotionModelAdapter;
    //表情的下标圆点layout
    private LinearLayout indicatorViewLL;
    //长按表情弹出的悬浮popupwindow
    private EmojiDetailPopWindow detailPopWindow;

    public EmojiView(Context context) {
        super(context);
        initView(context);
    }

    public EmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context){
        emojiCaseList = EmojiFileManager.INSTANCE.parseEmojiByLocalEmojiJson();
        if(Util.isNull(emojiCaseList)) return;
        emojiModelPageRecord = new int[emojiCaseList.size()];
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chatting_emoji_fragment_ui, this);
        faceViewPager = (ViewPager) view.findViewById(R.id.emoji_viewPager);
        indicatorView = (ViewFlipperIndicator) view.findViewById(R.id.emoji_indicator);
        emojiModelGv = (GridView) view.findViewById(R.id.emoji_model_gv);
        indicatorViewLL = (LinearLayout) view.findViewById(R.id.emoji_indicator_ll);
        emojiModelGv.setOnItemClickListener(this);
        detailPopWindow = new EmojiDetailPopWindow(context);
        initViewPager();
    }

    @Override
    public void setVisibility(int visibility) {
        //如果没有数据则不显示
        if(Util.isNull(emojiCaseList) || emojiCaseList.isEmpty()){
            super.setVisibility(GONE);
        } else{
            super.setVisibility(visibility);
            if(visibility != VISIBLE){
                //重设圆点layout高度，整个表情view会根据输入法高度变化
                indicatorViewLL.getLayoutParams().height = getHeight() - Util.dp2px(getContext(), 210) - Util.dp2px(getContext(), 43);
            }else {
                detailPopWindow.dismiss();
            }
        }
    }

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }


    /**
     * 设置表情套的grid view
     * @param emotionModels
     */
    public void setEmotionModelIndicator(List<EmotionModel> emotionModels){
        if(null == emotionModels || emotionModels.isEmpty()) {
            this.setVisibility(View.GONE);
            return;
        }
        this.setVisibility(View.VISIBLE);
        int itemWidth = getContext().getResources().getDimensionPixelSize(R.dimen.EmotionViewModelWidth);
        int gridviewWidth = itemWidth*emotionModels.size();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        emojiModelGv.setLayoutParams(params);
        emojiModelGv.setColumnWidth(itemWidth);
        emojiModelGv.setHorizontalSpacing(0);
        emojiModelGv.setStretchMode(GridView.NO_STRETCH);
        emojiModelGv.setNumColumns(emotionModels.size());
        emotionModelAdapter = new EmotionModelAdapter(getContext(), emotionModels);
        emotionModelAdapter.updateModelCheck(currEmojiModelPageIndex);
        emojiModelGv.setAdapter(emotionModelAdapter);
    }

    private void initViewPager() {
        updateIndicator(currEmojiPageIndex);
        ViewPagerItems.clear();
        for (int i = 0; i < emojiCaseList.size(); i++){
            EmotionModel emojiCase = emojiCaseList.get(i);
            //获取每套表情有多少页
            int emojiCasePageCount = getPagerCount(emojiCase.getEmotion());
            //记录每套表情的总页数
            emojiModelPageRecord[i] = emojiCasePageCount;
            if(emojiCasePageCount == 0) continue;
            for (int j = 0; j < emojiCasePageCount; j++) {
                ViewPagerItems.add(getViewPagerItem(emojiCase.getModelId(), j, emojiCase.getEmotion()));
            }
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(ViewPagerItems);
        faceViewPager.setAdapter(mVpAdapter);
        //表情的viewpager滑动的时候更新圆点下标
        faceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                updateIndicatorSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setEmotionModelIndicator(emojiCaseList);
    }


    /**
     * 更新每套表情的圆点下标
     * @param selectPosition
     */
    private void updateIndicator(int selectPosition){
        indicatorView.removeAllIndicators();
        List<Emotion> emojiCase = emojiCaseList.get(currEmojiModelPageIndex).getEmotion();
        indicatorView.addIndicators(getPagerCount(emojiCase), R.drawable.emoji_model_uncheck, R.drawable.emoji_model_check);
        indicatorView.setSelected(selectPosition);
    }

    private void updateIndicatorSelect(int currPosition){
        int count = 0;
        int emojiCasePageIndex = 0;
        //根据viewPager当前的下标计算出在当前套表情的下标，viewPager item的总数是所有套表情的页码总数
        for(int i = 0; i<emojiModelPageRecord.length; i++){
            count += emojiModelPageRecord[i];
            if(currPosition < count){
                emojiCasePageIndex = i;
                currEmojiPageIndex = currPosition - (count - emojiModelPageRecord[i]);
                break;
            }
        }
        if(currEmojiModelPageIndex != emojiCasePageIndex){
            //如果切换不同套的表情需更改表情套gridview对应item的背景颜色，然后更新圆点下标，圆点下标总数为每套表情的总页数
            emotionModelAdapter.updateModelCheck(emojiCasePageIndex);
            emojiModelGv.getChildAt(currEmojiModelPageIndex).setBackgroundColor(Color.WHITE);
            emojiModelGv.getChildAt(emojiCasePageIndex).setBackgroundColor(getResources().getColor(R.color.bm_emoji_modle_check_color));
            currEmojiModelPageIndex = emojiCasePageIndex;
            updateIndicator(currEmojiPageIndex);
        }else{
            indicatorView.setSelected(currEmojiPageIndex);
        }
    }

    /**
     * 获取一套表情的页码总数
     * @param list
     * @return
     */
    private int getPagerCount(List<Emotion> list) {
        if(Util.isNull(list) || list.isEmpty()) return 0;
        int count = list.size();
        return count % (COLUMNS * ROWS) == 0 ? count / (COLUMNS * ROWS) : count / (COLUMNS * ROWS) + 1;
    }

    /**
     * 将一套表情切割成多页表情grid view
     * @param emojiCaseId 表情套的id
     * @param position 表情数据的下标，从position开始计算出一页所需数据放入到grid view中形成一页表情
     * @param list 表情数据
     * @return
     */
    private View getViewPagerItem(final long emojiCaseId, int position, List<Emotion> list) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.chatting_emoji_grid_ui, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        gridview.setVerticalScrollBarEnabled(false);
        gridview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gridview.setPadding(Util.dp2px(getContext(), 30), 0,Util.dp2px(getContext(), 30), 0);
        final List<Emotion> subList = new ArrayList<>();
        subList.addAll(list.subList(position * (COLUMNS * ROWS), (COLUMNS * ROWS) * (position + 1) > list.size() ? list.size() : (COLUMNS * ROWS) * (position + 1)));
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(emojiCaseId, subList, getContext(), detailPopWindow, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(COLUMNS);
        return gridview;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position != currEmojiModelPageIndex){
            //计算当前点击的position对应的套表情其开始的页在整个viewPager中的下标
            int currEmojiPage = 0;
            for(int i = 0; i<position; i++){
                currEmojiPage += emojiModelPageRecord[i];
            }
            faceViewPager.setCurrentItem(currEmojiPage);
        }
    }

    @Override
    public void onEmojiClick(long emojiCaseId, Emotion emotion) {
        //表情的点击事件回调
        if(listener != null) listener.onEmojiClick(emojiCaseId, emotion);
    }
}



