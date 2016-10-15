package com.example.doctorsbuilding.nav;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import org.kobjects.util.Strings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by hossein on 9/8/2016.
 */
public class CustomListAdapterGallery2 extends BaseAdapter {
    /**
     * Created by hossein on 6/12/2016.
     */
    private Context context;
    private ArrayList<PhotoDesc> photos;
    private int lastPosition = -1;

    public CustomListAdapterGallery2(Context context, ArrayList<PhotoDesc> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {

        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        public ImageView imageView;
        public TextView description;

        public Holder(View v) {
            imageView = (ImageView) v.findViewById(R.id.gallery2_image);
            description = (TextView) v.findViewById(R.id.gallery2_description);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.gallery2_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.imageView.setImageBitmap(photos.get(position).getPhoto());
        holder.description.setText(photos.get(position).getDescription());

        setAnimation(rowView, position);
        return rowView;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(1000);

            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }




    //    private class asyncGetpicFromDatabase extends AsyncTask<String, Void, Void>{
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            for()
//            return null;
//        }
//    }
}
