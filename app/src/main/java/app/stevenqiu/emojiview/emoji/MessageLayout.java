package app.stevenqiu.emojiview.emoji;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import app.stevenqiu.emojiview.R;

/**
 * Created by Stevenqiu on 2016/8/23.
 */
public class MessageLayout extends LinearLayout{
    private final String TAG = MessageLayout.class.getSimpleName();
    private int needEmojiViewState = View.GONE;
    private boolean isKeyBoradShowing = false;
    private boolean needOpreateViewState = true;
    private ImageView mEmojiImageView;
    private EmojiView emojiLayout;
    private EditText editText;

    public MessageLayout(Context context) {
        super(context);
        initView(context);
    }

    public MessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        addOnSoftKeyBoardVisibleListener();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.message_layout, this);
        emojiLayout = (EmojiView) view.findViewById(R.id.chatting_emoji_ev);
        mEmojiImageView = (ImageView) view.findViewById(R.id.chatting_emoji_check_iv);
        mEmojiImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                opreateEmojiViewVisible();
            }
        });
        editText = (EditText) view.findViewById(R.id.chatting_emoji_check_ed);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    focusEditText();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusEditText();
            }
        });
    }


    public void addOnSoftKeyBoardVisibleListener() {
        final View decorView = ((Activity)getContext()).getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int displayHight = Util.getAppHeight((Activity) getContext());
                int hight = decorView.getHeight();
                boolean visible = (double) displayHight / hight < 0.8;
                if(visible) Util.saveKeyboardHeight(hight - displayHight,(Activity) getContext());
                Log.i(TAG, "softkeyboard visible = " + visible+" keyboard height:"+(hight - displayHight));
                isKeyBoradShowing = visible;
                if(visible) {
                    needOpreateViewState = true;
                    if(emojiLayout.getVisibility() != View.INVISIBLE){
                        emojiLayout.getLayoutParams().height = Util.getKeyboardHeight((Activity) getContext());
                        setEmojiVisible(View.INVISIBLE);
                    }
                }else {
                    if(!needOpreateViewState){
                        needOpreateViewState = true;
                        return;
                    }
                    if(needEmojiViewState == View.VISIBLE) {
                        needEmojiViewState = View.GONE;
                        needOpreateViewState = false;
                        setEmojiVisible(View.VISIBLE);
                    } else if(needEmojiViewState == View.INVISIBLE){
                        needEmojiViewState = View.GONE;
                        needOpreateViewState = false;
                    }else if(emojiLayout.getVisibility() == View.INVISIBLE){
                        setEmojiVisible(View.GONE);
                    }
                }
            }
        });
    }


    private void setEmojiVisible(int visible){
        emojiLayout.setVisibility(visible);
        if(emojiLayout.getVisibility() == View.VISIBLE){
            mEmojiImageView.setImageResource(R.mipmap.bm_chatting_emoji_icon_open);
        }else{
            mEmojiImageView.setImageResource(R.mipmap.bm_chatting_emoji_icon_close);
        }
    }

    /**
     * onFocuseChangeListen when has focus need this, and onClickListen need this, editText need setOnFocusChangeListener and setOnClickListener
     */
    private void focusEditText(){
        hideEmotionView(true);
    }

    private void opreateEmojiViewVisible(){
        if(emojiLayout.isShown()){
            hideEmotionView(false);
        }else{
            showEmotionView();
        }
    }

    private void hideEmotionView(boolean showKeyBoard) {
        needOpreateViewState = true;
        if (emojiLayout.isShown()) {
            needEmojiViewState = View.INVISIBLE;
            if (showKeyBoard) {
                emojiLayout.getLayoutParams().height = Util.getKeyboardHeight(((Activity)getContext()));
                setEmojiVisible(View.INVISIBLE);
                ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                Util.showKeyBoard(editText);
            } else {
                needEmojiViewState = View.GONE;
                setEmojiVisible(View.GONE);
            }
        }else if(!isKeyBoradShowing){
            needEmojiViewState = View.INVISIBLE;
            emojiLayout.getLayoutParams().height = Util.getKeyboardHeight(((Activity)getContext()));
            setEmojiVisible(View.INVISIBLE);
            ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            Util.showKeyBoard(editText);
        }
    }

    private void showEmotionView() {
        needEmojiViewState = View.VISIBLE;
        needOpreateViewState = true;
        if(isKeyBoradShowing){
            Util.hideSoftInput(editText);
            ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else {
            emojiLayout.getLayoutParams().height = Util.getKeyboardHeight(((Activity)getContext()));
            setEmojiVisible(View.VISIBLE);
        }
    }

    public void hideKeyBoradAndEmoji(){
        if(isKeyBoradShowing){
            Util.hideSoftInput(editText);
            ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            needEmojiViewState = View.GONE;
            setEmojiVisible(View.GONE);
        }
    }

    public void setListener(OnEmojiClickListener listener) {
        emojiLayout.setListener(listener);
    }

}
