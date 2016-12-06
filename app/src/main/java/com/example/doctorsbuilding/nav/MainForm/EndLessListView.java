package com.example.doctorsbuilding.nav.MainForm;

/**
 * Created by hossein on 10/17/2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;

public class EndLessListView extends ListView implements OnScrollListener {

    private View footer;
    private boolean isLoading;
    private EndlessListener listener;
    private CustomOfficeEndLessAdapter adapter;

    public EndLessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);
    }

    public EndLessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public EndLessListView(Context context) {
        super(context);
        this.setOnScrollListener(this);
    }

    public void setListener(EndlessListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null)
            return;

        if (getAdapter().getCount() == 0)
            return;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            // It is time to add new data. We call the listener
            this.addFooterView(footer);
            isLoading = true;
            listener.loadData();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int first = getFirstVisiblePosition();
            int last = getLastVisiblePosition();

            for (int i = first; i < last; i++) {
                listener.loadImage(i);
            }
        }
    }

    public void setLoadingView(int resId) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater.inflate(resId, null);
        this.addFooterView(footer);

    }

    public void setAdapter(CustomOfficeEndLessAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.removeFooterView(footer);
    }

    public void addNewData(ArrayList<Office> data) {

        this.removeFooterView(footer);
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
        isLoading = false;
    }

    public void stopLoading() {
        this.removeFooterView(footer);
    }

    public EndlessListener setListener() {
        return listener;
    }


    public static interface EndlessListener {
        public void loadData();

        public void loadImage(int position);
    }

}
