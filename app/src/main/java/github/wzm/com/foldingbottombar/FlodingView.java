package github.wzm.com.foldingbottombar;


import android.content.Context;
import android.os.Build;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Okline(Hangzhou)co,Ltd<br/>
 * Author: wangzhongming<br/>
 * Email:  wangzhongming@okline.cn</br>
 * Date :  2017/12/13 17:45 </br>
 * Summary:
 */

public class FlodingView extends ViewGroup {
    public static final String TAG = FlodingView.class.getSimpleName();
    private volatile ViewDragHelper mViewDragHelper;
    private Context mContext;
    private View topView;
    private View bottomView;
    private int elevationHeight = 25;//层叠高度
    private int extendHeight = 30;//延伸高度
    private boolean isExpand = true;
    private openChangeListener mOpenChangeListener;

    public FlodingView(Context context) {
        this(context, null);
    }

    public FlodingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlodingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public interface openChangeListener {

        void onScrolling(float percent);
    }

    public void setOpenChangeListener(openChangeListener openChangeListener) {
        mOpenChangeListener = openChangeListener;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new RuntimeException("一定要是2个子View!");
        }
        if (mViewDragHelper == null) {
            topView = getChildAt(0);
            bottomView = getChildAt(1);
            bringChildToFront(topView);//将view显示在屏幕最前方
            mViewDragHelper = ViewDragHelper.create(this, 1.0f, new MyViewDragHelper());
        }

    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void setExtendHeight(int extendHeight) {
        this.extendHeight = extendHeight;
        requestLayout();
    }

    public void setElevationHeight(int elevationHeight) {
        this.elevationHeight = elevationHeight;
        requestLayout();
    }

    //折叠布局
    public void foldLayout() {
        if (topView != null && mViewDragHelper != null) {
            isExpand = false;
            mViewDragHelper.settleCapturedViewAt(topView.getLeft(), getHeight() - topView.getHeight() - elevationHeight);
        }
    }

    //还原/展开布局
    public void expandLayout() {
        if (topView != null && mViewDragHelper != null) {
            mViewDragHelper.settleCapturedViewAt(topView.getLeft(), 0);
            isExpand = true;
        }
    }

    //布局是否展开状态
    public boolean isExpand() {
        return isExpand;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);//计算所有子View的宽高
        int width = 0;
        int height = 0;
        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            MarginLayoutParams marginParams = (MarginLayoutParams) childAt.getLayoutParams();//需重写generateLayoutParams 才行

            int childWidthWithMarge = childAt.getMeasuredWidth() + marginParams.leftMargin + marginParams.rightMargin;
            int childHeightWithMarge = childAt.getMeasuredHeight() + marginParams.topMargin + marginParams.bottomMargin;
            height = height + childHeightWithMarge;//高度为所有子View的高度和
            width = width > childWidthWithMarge ? width : childWidthWithMarge;//宽度取最大的那个子View的宽

        }

        //当设置精确值或match_parent时使用传入的值
        //当wrap_content时使用测量出来的值
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {//其中boolean changed表示是否与上一次位置不同
        //①getMeasureWidth()方法在measure()过程结束后就可以获得到它的值，而getWidth()方法要在layout()过程结束后才能获取到
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int cWidth = childAt.getMeasuredWidth();//这里要用getMeasuredWidth，因为getWidth()要在onLayout结束后才有效
            int cHeight = childAt.getMeasuredHeight();
            Log.i(TAG, "onLayout: <----------------");
            Log.i(TAG, "onLayout: cWidth" + cWidth);
            Log.i(TAG, "onLayout: cHeight" + cHeight);
            Log.i(TAG, "onLayout: getHeight==" + getHeight());
            Log.i(TAG, "onLayout: getWidth==" + getWidth());
            Log.i(TAG, "onLayout: ------------------>");
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childAt.getLayoutParams();
            int left = 0, top = 0, right = 0, bottom = 0;
            switch (i) {//因为上面使用过bringChildToFront()把第一个的位置换了
                case 0: //bottomView
                    left = marginLayoutParams.leftMargin;
                    //整体的高度-bottomView的高度-bottomView的bottomMargin得到了bottomMargin的top
                    top = getHeight() - cHeight - marginLayoutParams.bottomMargin - 30;
                    right = left + cWidth + 30;
                    bottom = getHeight() - marginLayoutParams.bottomMargin;
                    childAt.setPadding(0, 30, 0, 0);
                    break;
                case 1://topView

                    left = marginLayoutParams.leftMargin;
                    top = marginLayoutParams.topMargin;
                    right = left + cWidth;
                    bottom = cHeight + marginLayoutParams.topMargin;
                    break;
            }

            Log.i(TAG, "onLayout: <===============");
            Log.i(TAG, "onLayout: getHeight==" + getHeight());
            Log.i(TAG, "onLayout: getWidth==" + getWidth());
            Log.i(TAG, "onLayout: i==" + i);
            Log.i(TAG, "onLayout: left==" + left);
            Log.i(TAG, "onLayout: top==" + top);
            Log.i(TAG, "onLayout: right==" + right);
            Log.i(TAG, "onLayout: bottom==" + bottom);
            Log.i(TAG, "onLayout: ===============>");
            childAt.layout(left, top, right, bottom);
        }
    }


    private class MyViewDragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //指定在哪个view上可以拖动
            return child == topView;//true手指按下时可以拖动
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;  //限制移动范围，不过目前不起作用，不过可以根据返回值是否大于0 判断是否水平移动。即大于0 是水平移动
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1; //大于0表示可以竖直拖动
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //返回的值表示我们想要移动到的位置y
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight() - getPaddingBottom() - elevationHeight;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float percent = (float) top / (getHeight() - changedView.getHeight() - elevationHeight);
            Log.d("OverlyingView", "percent" + percent);
            if (mOpenChangeListener != null) {
                mOpenChangeListener.onScrolling(percent);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                changedView.setElevation(percent * 10);
            }
            bottomView.setScaleX(1 - percent * 0.03f);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == topView) {
                float movePercentage = (float) (releasedChild.getTop()) / (getHeight() - releasedChild.getHeight() - elevationHeight);
                int finalTop = (movePercentage >= .5f) ? getHeight() - releasedChild.getHeight() - elevationHeight : 0;
                mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
                isExpand = movePercentage < .5f;
                invalidate();
            }
        }


    }

}
