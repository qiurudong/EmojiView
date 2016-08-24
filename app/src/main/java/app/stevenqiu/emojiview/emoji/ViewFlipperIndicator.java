package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import app.stevenqiu.emojiview.R;

public class ViewFlipperIndicator extends LinearLayout {

	private static final String TAG = ViewFlipperIndicator.class.getName();
	private LinearLayout mIndicateContainerLL;
	private int mNormalResId;
	private int mSelectedResId;
	private int mMaxHeight;
	private int mSpacing = 4;
	public ViewFlipperIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.view_flipper_indicator, this);
		mIndicateContainerLL = (LinearLayout) ll.getChildAt(0);

		ViewGroup.LayoutParams params = this.getLayoutParams();
		if ((null != params) && (0 < params.height)) {
			mMaxHeight = params.height;
			Log.d(TAG, "flipper indicator layout height = " + mMaxHeight);
		}
	}

	private ImageView createImageView(int resId) {
		ImageView iv = new ImageView(this.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		iv.setLayoutParams(params);
		iv.setAdjustViewBounds(true);
		iv.setScaleType(ScaleType.FIT_XY);
		if (0 < mMaxHeight) {
			iv.setMaxHeight(mMaxHeight);
			iv.setMaxWidth(mMaxHeight);
		}
		iv.setImageResource(resId);
		return iv;
	}

	public void setSpacing(int dp) {
		if (0 < dp) {
			mSpacing = dp;
		}
	}

	public void addIndicator(int normalResId, int selectedResId) {
		mNormalResId = normalResId;
		mSelectedResId = selectedResId;
		ImageView iv = createImageView(normalResId);
		if (0 < mIndicateContainerLL.getChildCount()) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();
			if (null != params) {
				params.leftMargin = Util.dp2px(this.getContext(), mSpacing);
				iv.setLayoutParams(params);
			}
		}
		mIndicateContainerLL.addView(iv);
	}
	
	public void removeAllIndicators() {
		mIndicateContainerLL.removeAllViews();
	}

	public void addIndicators(int size, int normalResId, int selectedResId) {
		for (int i=0; i<size; ++i) {
			addIndicator(normalResId, selectedResId);
		}
	}
	
	public void setSelected(int position) {
		if (position >= mIndicateContainerLL.getChildCount()) {
			Log.e(TAG, "position is out of range. position = " + position);
			return;
		}
		
		for (int i=0; i<mIndicateContainerLL.getChildCount(); ++i) {
			ImageView iv = (ImageView) mIndicateContainerLL.getChildAt(i);
			if (i == position) {
				iv.setImageResource(mSelectedResId);
			} else {
				iv.setImageResource(mNormalResId);
			}
		}
	}
}
