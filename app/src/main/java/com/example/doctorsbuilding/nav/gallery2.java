package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.ImageCompressor;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/8/2016.
 */
public class gallery2 extends Activity {
    private ListView mListView;
    private CustomListAdapterGallery2 adapter;
    private int selectedPosition;
    private ActionMode cabMode = null;
    private ActionMode modeState = null;
    private RelativeLayout layout;
    private RelativeLayout insertLayout;
    private View selectedRow;
    private EditText aboutPic;
    private ImageView editPic;
    private ImageView insertPic;
    private ImageButton backBtn;
    private DatabaseAdapter database;
    ArrayList<PhotoDesc> photos = new ArrayList<PhotoDesc>();
    ProgressBar loading_progress;
    ArrayList<Boolean> visist_list;
    TextView pageTitle;
    private ArrayList<PhotoDesc> imagesInWeb = null;

    asyncGetImageIdFromWeb asyncgetImageId;
    asyncGetGalleryPic asyncGetPic;
    asyncDeletePicFromGallery asyncRemovePic;
    asyncChangeGalleryPicDescription asyncUpdatePic;
    asyncSetGalleryPic asyncSetPic;
    asyncDeletePicFromPhone asyncDeleteJunkPic;


    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            mode.setTitle("Options");
            mode.getMenuInflater().inflate(R.menu.popup_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            layout = (RelativeLayout) findViewById(R.id.gallery2_actionBar);
            layout.setVisibility(View.GONE);
            insertPic.setVisibility(View.VISIBLE);
            editPic.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            modeState = mode;
            int id = item.getItemId();
            switch (id) {
                case R.id.delete: {
                    remove();
                    mode.finish();
                    break;
                }
                case R.id.edit: {
                    edit();
                    //mode.finish();
                    break;
                }
                default:
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode = null;
            cabMode = null;
            layout.setVisibility(View.VISIBLE);
            insertPic.setVisibility(View.VISIBLE);
            editPic.setVisibility(View.GONE);
            selectedRow.setBackgroundColor(mListView.getSolidColor());
            mListView.setItemChecked(selectedPosition, false);
            selectedPosition = -1;
            adapter.notifyDataSetChanged();

        }

    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.setStatusBarColor(gallery2.this);
        setContentView(R.layout.activity_gallery2);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncGetPic != null) {
            asyncGetPic.cancel(true);
        }
        if (asyncgetImageId != null) {
            asyncgetImageId.cancel(true);
        }
        if (asyncUpdatePic != null) {
            asyncUpdatePic.cancel(true);
        }
        if (asyncRemovePic != null) {
            asyncRemovePic.cancel(true);
        }
        if (asyncSetPic != null) {
            asyncSetPic.cancel(true);
        }
        if (asyncDeleteJunkPic != null) {
            asyncDeleteJunkPic.cancel(true);
        }

    }

    private void initViews() {
        insertLayout = (RelativeLayout) findViewById(R.id.gallery2_insert_layout);
        mListView = (ListView) findViewById(R.id.gallery2_listView);
        aboutPic = (EditText) findViewById(R.id.gallery2_about);
        editPic = (ImageView) findViewById(R.id.gallery2_apply_edit);
        insertPic = (ImageView) findViewById(R.id.gallery2_apply_image);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("گالری عکس");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.gallery2_insert_layout);
        if (G.UserInfo.getRole() != UserType.Dr.ordinal() && G.UserInfo.getRole() != UserType.secretary.ordinal()) {
            rl.setVisibility(View.GONE);
        }
        database = new DatabaseAdapter(gallery2.this);
        asyncgetImageId = new asyncGetImageIdFromWeb();
        asyncgetImageId.execute();


    }


    private void eventListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                    if (cabMode != null) {
                        return false;
                    }
                    selectedPosition = position;
                    selectedRow = view;
                    mListView.setItemChecked(position, true);
                    mListView.setOnItemClickListener(null);
                    cabMode = startActionMode(modeCallBack);
                    view.setSelected(true);
                    view.setBackgroundColor(Color.parseColor("#BBDEFB"));
                    return true;
                }
                return false;
            }
        });
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                new MessageBox(gallery2.this, String.valueOf(position));
            }
        });
        // registerForContextMenu(mListView);

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncUpdatePic = new asyncChangeGalleryPicDescription(selectedPosition, aboutPic.getText().toString().trim());
                asyncUpdatePic.execute();
            }
        });
        aboutPic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (aboutPic.getText().toString().equals("") && selectedPosition == -1) {
                    insertPic.setVisibility(View.VISIBLE);
                    editPic.setVisibility(View.GONE);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                // getTop() and getBottom() are relative to the ListView,
                //   so if getTop() is negative, it is not fully visible
//                int first = 0;
//                if(mListView.getChildAt(first).getTop() < 0)
//                    first++;
//
//                int last = mListView.getChildCount() - 1;
//                if(mListView.getChildAt(last).getBottom() > mListView.getHeight())
//                    last--;
//
//                // Now loop through your rows
//                for( ; first <= last; first++) {
//                    // Do something
//                    View row = mListView.getChildAt(first);
//                    Toast.makeText(gallery2.this, String.valueOf(first), Toast.LENGTH_SHORT).show();
//                }

            }
        });
        insertPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePic();
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ex) {

                }
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int first = mListView.getFirstVisiblePosition();
                    int last = mListView.getLastVisiblePosition();

                    for (int i = first; i <= last; i++) {
                        if (!visist_list.get(i)) {
                            asyncGetGalleryPic task = new asyncGetGalleryPic();
                            task.execute(String.valueOf(photos.get(i).getId()), String.valueOf(i));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
//        mListView.setOnTouchListener(new View.OnTouchListener() {
//            float height;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                float height = event.getY();
//                if(action == MotionEvent.ACTION_DOWN){
//                    this.height = height;
//                }else if(action == MotionEvent.ACTION_UP){
//                    if(this.height < height){
////                        Log.v(TAG, "Scrolled up");
//                    }else if(this.height > height){
////                        Log.v(TAG, "Scrolled down");
//                        Toast.makeText(gallery2.this, String.valueOf(mListView.getLastVisiblePosition()), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                return false;
//            }
//        });


    }


    private void changePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "?????? ???"), 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

//                        ImageCompressor img = new ImageCompressor();
//                        img.compressImage(data.getDataString());
//                        int x = 0;
                        asyncSetPic = new asyncSetGalleryPic(scaled, aboutPic.getText().toString());
                        asyncSetPic.execute();

//                        currentUser.image = resizedBitmap;
//                        Database.UpdateCurrentUser(currentUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private boolean remove() {
        asyncRemovePic = new asyncDeletePicFromGallery(selectedPosition);
        asyncRemovePic.execute();
        return true;

    }

    private boolean edit() {
        aboutPic.setText(photos.get(selectedPosition).getDescription());
        insertPic.setVisibility(View.GONE);
        editPic.setVisibility(View.VISIBLE);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(aboutPic, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ex) {
        }

        return true;
    }


    private boolean updateListView(int position, PhotoDesc photo) {
        int first = mListView.getFirstVisiblePosition();
        int last = mListView.getLastVisiblePosition();
        if (position < first || position > last) {
            return false;
        } else {
            View convertView = mListView.getChildAt(position - first);
            ImageView image = (ImageView) convertView.findViewById(R.id.gallery2_image);
            image.setImageBitmap(photo.getPhoto());
            return true;
        }
    }


    private void updateView(int index) {
        View v = mListView.getChildAt(index - mListView.getFirstVisiblePosition());

        if (v == null)
            return;

        ImageView imageView = (ImageView) v.findViewById(R.id.gallery2_image);
        imageView.setImageBitmap(photos.get(index).getPhoto());

        TextView someText = (TextView) v.findViewById(R.id.gallery2_description);
        someText.setText(photos.get(index).getDescription());
    }

    private class asyncSetGalleryPic extends AsyncTask<String, Void, Void> {

        String msg = null;
        Bitmap photo = null;
        int id = -1;
        String description = "";
        ProgressDialog dialog_wait;

        public asyncSetGalleryPic(Bitmap photo, String description) {
            this.photo = photo;
            this.description = description;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog_wait = ProgressDialog.show(gallery2.this, "", "در حال ذخیره سازی عکس ...");
            dialog_wait.getWindow().setGravity(Gravity.END);
            dialog_wait.setCancelable(true);
            mListView.setEnabled(false);
            insertPic.setClickable(false);

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                id = WebService.invokeSetGalleryPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(),
                        G.officeId, photo, description);
            } catch (PException ex) {
                msg = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog_wait.dismiss();
                new MessageBox(gallery2.this, msg).show();
            } else {
                if (id != -1) {
                    dialog_wait.dismiss();
                    Toast.makeText(gallery2.this, "عملیات ثبت با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                    PhotoDesc aks = new PhotoDesc();
                    aks.setId(id);
                    aks.setPhoto(photo);
                    aks.setDate("");
                    aks.setDescription(description);
                    photos.add(aks);
                    if (mListView.getChildCount() == 0) {
                        visist_list = new ArrayList<Boolean>();
                        visist_list.add(true);
                        adapter = new CustomListAdapterGallery2(gallery2.this, photos);
                        mListView.setAdapter(adapter);
                    } else {
                        visist_list.add(true);
                        adapter.notifyDataSetChanged();
                        mListView.setSelection(photos.size() - 1);
                    }
                }
            }
            mListView.setEnabled(true);
            insertPic.setClickable(true);
        }
    }

    private class asyncDeletePicFromGallery extends AsyncTask<String, Void, Void> {

        String msg = null;
        int picId;
        int position;
        ProgressDialog dialog_wait;

        public asyncDeletePicFromGallery(int position) {
            this.picId = photos.get(position).getId();
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog_wait = ProgressDialog.show(gallery2.this, "", "در حال حذف عکس ...");
            dialog_wait.getWindow().setGravity(Gravity.END);
            dialog_wait.setCancelable(true);
            mListView.setEnabled(false);
            insertPic.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                WebService.invokeDeleteFromGalleryWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, picId);
            } catch (PException ex) {
                msg = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog_wait.dismiss();
                new MessageBox(gallery2.this, msg).show();
            } else {
                dialog_wait.dismiss();
                database.deleteImageFromGallery(picId);
                Toast.makeText(gallery2.this, "عملیات حذف با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                photos.remove(position);
                visist_list.remove(position);
                adapter.notifyDataSetChanged();
            }
            mListView.setEnabled(true);
            insertPic.setClickable(true);
        }
    }

    private class asyncChangeGalleryPicDescription extends AsyncTask<String, Void, Void> {

        String msg = null;
        int picId;
        int position;
        String description;
        ProgressDialog dialog_wait;

        public asyncChangeGalleryPicDescription(int position, String description) {
            this.picId = photos.get(position).getId();
            this.description = description;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog_wait = ProgressDialog.show(gallery2.this, "", "در حال بروز رسانی عکس ...");
            dialog_wait.getWindow().setGravity(Gravity.END);
            dialog_wait.setCancelable(true);
            mListView.setEnabled(false);
            insertPic.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                WebService.invokeChangeGalleryPicDescriptionWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, picId, description);
            } catch (PException ex) {
                msg = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog_wait.dismiss();
                new MessageBox(gallery2.this, msg).show();
            } else {
                dialog_wait.dismiss();

                database.updateImageInGallery(picId, description);
                Toast.makeText(gallery2.this, "عملیات بروز رسانی عکس با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                PhotoDesc photoDesc = photos.get(position);
                photoDesc.setDescription(description);
                photos.set(position, photoDesc);
                ////////////////////////////////////
                updateView(photos.size() - 1);
                ////////////////////////////////////
                modeState.finish();
                aboutPic.setText("");
                insertPic.setVisibility(View.VISIBLE);
                editPic.setVisibility(View.GONE);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ex) {
                }
            }
            mListView.setEnabled(true);
            insertPic.setClickable(true);
        }
    }


    private class asyncGetImageIdFromWeb extends AsyncTask<String, Void, Void> {

        private String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (visist_list == null)
                loading_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                imagesInWeb = WebService.invokeGetPhotoDescsWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loading_progress.setVisibility(View.GONE);
            if (msg != null) {
                new MessageBox(gallery2.this, msg).show();
            } else {

                if (imagesInWeb != null && imagesInWeb.size() > 0) {
                    initSlideShow(imagesInWeb);
                    asyncDeleteJunkPic = new asyncDeletePicFromPhone();
                    asyncDeleteJunkPic.execute();
                } else {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        insertLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    private void initSlideShow(ArrayList<PhotoDesc> imagesInWeb) {

        visist_list = new ArrayList<Boolean>();
        photos = new ArrayList<PhotoDesc>();
        for (int i = 0; i < imagesInWeb.size(); i++) {
            visist_list.add(false);
            PhotoDesc aks = new PhotoDesc();
            aks.setId(imagesInWeb.get(i).getId());
            aks.setDescription(imagesInWeb.get(i).getDescription());
            aks.setDate(imagesInWeb.get(i).getDate());
            aks.setPhoto(BitmapFactory.decodeResource(getResources(), R.mipmap.image_placeholder));
            photos.add(aks);
        }
        adapter = new CustomListAdapterGallery2(gallery2.this, photos);
        mListView.setAdapter(adapter);
        if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
            insertLayout.setVisibility(View.VISIBLE);
        }

        int imageCount = 1;
        if (imagesInWeb.size() > 1)
            imageCount = 2;
        for (int i = 0; i < imageCount; i++) {
            visist_list.set(i, true);
            asyncGetGalleryPic task = new asyncGetGalleryPic();
            task.execute(String.valueOf(photos.get(i).getId()), String.valueOf(i));
        }

    }

    private class asyncDeletePicFromPhone extends AsyncTask<String, Void, Void> {
        private String msg = null;
        ArrayList<Integer> imageInPhone;
        ArrayList<Integer> imageIdsInWeb;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (database.openConnection()) {
                    imageInPhone = database.getImageIds();
                    imageIdsInWeb = new ArrayList<Integer>();
                    for (int i = 0; i < imagesInWeb.size(); i++) {
                        imageIdsInWeb.add(imagesInWeb.get(i).getId());
                    }
                    imageInPhone.removeAll(imageIdsInWeb);
                }

            } catch (Exception ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(gallery2.this, "خطایی در حذف عکس رخ داده است .").show();
            } else {
                for (int i = 0; i < imageInPhone.size(); i++) {
                    database.deleteImageFromGallery(imageInPhone.get(i));
                }
            }
        }
    }

    private class asyncGetGalleryPic extends AsyncTask<String, Void, Void> {
        private String msg = null;
        private PhotoDesc photo;
        private int photoId;
        private int currentPageNum;
        private boolean existInPhone = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {

                photoId = Integer.parseInt(strings[0]);
                currentPageNum = Integer.parseInt(strings[1]);
                visist_list.set(currentPageNum, true);
                if (database.openConnection()) {
                    photo = database.getImageFromGallery(photoId);
                    // photo.setDescription(imagesInWeb.get(currentPageNum).getDescription());
                }
                if (photo == null) {
                    existInPhone = false;
                    photo = WebService.invokeGetGalleryPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                            , G.officeId, photoId);
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(gallery2.this, msg).show();
            } else {
                if (photo != null) {
                    if (!existInPhone) {
                        if (database.openConnection()) {
                            database.saveImageToGallery(photo.getId(), photo.getDate(),
                                    photo.getDescription(), DbBitmapUtility.getBytes(photo.getPhoto()));
                        }
                    }

                    photos.set(currentPageNum, photo);
                    adapter.notifyDataSetChanged();

                }
            }

        }
    }

}


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        if (v.getId() == R.id.gallery2_listView) {
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
////            menu.setHeaderTitle("salam");
//            String[] menuItems = getResources().getStringArray(R.array.list_popup_menu);
//            for (int i = 0; i < menuItems.length; i++) {
//                menu.add(Menu.NONE, i, i, menuItems[i]);
//            }
//        }
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int menuItemIndex = item.getItemId();
//        String[] menuItems = getResources().getStringArray(R.array.list_popup_menu);
//        String menuItemName = menuItems[menuItemIndex];
//        String listItemName = items.get(info.position);
//
//        return true;
//    }


