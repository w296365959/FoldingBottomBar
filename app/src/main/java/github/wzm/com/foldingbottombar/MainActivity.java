package github.wzm.com.foldingbottombar;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.VP);
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
        int[] images = {R.mipmap.wom1, R.mipmap.wom3, R.mipmap.wom4, R.mipmap.mon2, R.mipmap.mon3};
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(images);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setCurrentItem(10002);//跳转到一个中间的页面，实现左右都可以滑动
        viewPager.setPageMargin(22);//页面的间隔
        viewPager.setOffscreenPageLimit(2);//保存页数要大于3页，写2表示左右各保存2页
        viewPager.setPageTransformer(true, new RotateDownPageTransformer());
        // viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

    }

    public class RotateDownPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {//position 在中间是0，左边是-1+偏移量，右边是1+偏移量
            int width = view.getWidth();
            int height = view.getHeight();
            //position值可以小数， 单大致范围是左边-1 ,中间 0  ,右边 1
            float factor = width * (0.5f - 0.5f * position); //(-1,0),(0,1)旋转中心就是0到width/2
            //即从左边到中间，旋转中心是在width到width/2，从中间到右边，旋转中心是从width/2到0
            view.setPivotX(factor);
            view.setPivotY(height);
            view.setRotation(15 * position);//Z轴上更具position 旋转 -15度到0到+15度

        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;//
        private static final float MIN_ALPHA = 0.65f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(MIN_ALPHA);
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                    view.setScaleX(1 + 0.3f * position);
                    view.setScaleY(1 + 0.3f * position);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);

                    view.setScaleX(1 - 0.3f * position);
                    view.setScaleY(1 - 0.3f * position);
                }

                // Scale the page down (between MIN_SCALE and 1)

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
                view.setAlpha(MIN_ALPHA);
            }
        }
    }
}
