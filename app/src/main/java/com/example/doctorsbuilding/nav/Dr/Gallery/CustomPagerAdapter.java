package com.example.doctorsbuilding.nav.Dr.Gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.doctorsbuilding.nav.R;

/**
 * Created by hossein on 6/14/2016.
 */
public class CustomPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int[] images;

    public CustomPagerAdapter(Context context, int[]images) {
        this.context = context;
        this.images = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.gallery_image_item, container, false);
        ImageView imgView = (ImageView) view.findViewById(R.id.galleryImageItem_imgView);
        imgView.setImageResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }

}
