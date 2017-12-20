package github.wzm.com.foldingbottombar;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Okline(Hangzhou)co,Ltd<br/>
 * Author: wangzhongming<br/>
 * Email:  wangzhongming@okline.cn</br>
 * Date :  2017/12/20 11:48 </br>
 * Summary:
 */

public class MyPagerAdapter extends PagerAdapter {
    private int[] imageResIds;

    public MyPagerAdapter(int[] imageResIds) {
        this.imageResIds=imageResIds;
    }

    @Override
    public int getCount() {
        return imageResIds.length*1000*100;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        position=position%imageResIds.length;
        imageView.setBackgroundResource(imageResIds[position]);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // super.destroyItem(container, position, object);
        container.removeView((ImageView)object);
    }
}
