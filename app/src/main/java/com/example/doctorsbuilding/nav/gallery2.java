package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.Fade;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hossein on 9/8/2016.
 */
public class gallery2 extends Activity {
    private ListView mListView;
    private CustomListAdapterGallery2 adapter;
    private ArrayList<Integer> imageIdsInPhone;
    private int selectedPosition;
    private ActionMode cabMode = null;
    private ActionMode modeState = null;
    private RelativeLayout layout;
    private View selectedRow;
    private EditText aboutPic;
    private ImageView editPic;
    private ImageView insertPic;
    private Button backBtn;
    private DatabaseAdapter database;
    private ArrayList<Integer> subscriptionList;
    ArrayList<PhotoDesc> photos = new ArrayList<PhotoDesc>();
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
        setContentView(R.layout.activity_gallery2);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.gallery2_listView);
        aboutPic = (EditText) findViewById(R.id.gallery2_about);
        editPic = (ImageView) findViewById(R.id.gallery2_apply_edit);
        insertPic = (ImageView) findViewById(R.id.gallery2_apply_image);
        backBtn = (Button) findViewById(R.id.gallery2_backBtn);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.gallery2_insert_layout);
        if (G.UserInfo.getRole() != UserType.Dr.ordinal() && G.UserInfo.getRole() != UserType.secretary.ordinal()) {
            rl.setVisibility(View.GONE);
        }
        database = new DatabaseAdapter(gallery2.this);
        subscriptionList = new ArrayList<Integer>();


        if (database.openConnection()) {
            imageIdsInPhone = database.getImageIds();
        }
        asyncGetImageIds task = new asyncGetImageIds();
        task.execute();

    }

    private void eventListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                if (G.UserInfo.getRole() == UserType.Dr.ordinal()) {
                    if (cabMode != null) {
                        return false;
                    }
                    selectedPosition = position;
                    selectedRow = view;
                    mListView.setItemChecked(position, true);
                    mListView.setOnItemClickListener(null);
                    cabMode = startActionMode(modeCallBack);
                    view.setSelected(true);
                    view.setBackgroundColor(Color.parseColor("#332196f3"));
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
                asyncChangeGalleryPicDescription task = new asyncChangeGalleryPicDescription(selectedPosition, aboutPic.getText().toString().trim());
                task.execute();
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
                if (aboutPic.getText().toString().equals("")) {
                    insertPic.setVisibility(View.VISIBLE);
                    editPic.setVisibility(View.GONE);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        insertPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePic();
            }
        });
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
                        asyncSetGalleryPic updateDrPicWS = new asyncSetGalleryPic(scaled, aboutPic.getText().toString());
                        updateDrPicWS.execute();


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
        asyncDeletePicFromGallery task = new asyncDeletePicFromGallery(selectedPosition);
        task.execute();
        return true;

    }

    private boolean edit() {
        aboutPic.setText(photos.get(selectedPosition).getDescription());
        insertPic.setVisibility(View.GONE);
        editPic.setVisibility(View.VISIBLE);
        return true;
    }

    private class asyncGetImageIds extends AsyncTask<String, Void, Void> {

        private ArrayList<Integer> imageIdsInWeb = null;
        private ArrayList<Integer> differenceList = null;
        private String msg = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                imageIdsInWeb = WebService.invokegetAllGalleyPicIdWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                if (imageIdsInWeb != null && imageIdsInWeb.size() > 0) {
                    differenceList = findNewIds(imageIdsInPhone, imageIdsInWeb);
                    if (subscriptionList != null && subscriptionList.size() > 0) {
                        showPhotosInFirstTime(subscriptionList);
                    }

                }
                if (differenceList != null && differenceList.size() > 0) {
                    for (int i = 0; i < differenceList.size(); i++) {
                        asyncGetGalleryPic task = new asyncGetGalleryPic();
                        task.execute(String.valueOf(differenceList.get(i)));
                    }
                }
            }
        }

        private void showPhotosInFirstTime(ArrayList<Integer> photoList) {
            for (int i = 0; i < photoList.size(); i++) {
                photos.add(database.getImageFromGallery(photoList.get(i)));
                adapter = new CustomListAdapterGallery2(gallery2.this, photos);
                mListView.setAdapter(adapter);
            }

        }

        private ArrayList<Integer> findNewIds(ArrayList<Integer> imageIdsInPhone, ArrayList<Integer> imageIdsInWeb) {
            int i = 0;
            ArrayList<Integer> differenceList = new ArrayList<Integer>();
            while (i < imageIdsInWeb.size()) {
                int j = 0;
                while (j < imageIdsInPhone.size()) {
                    if (imageIdsInWeb.get(i).equals(imageIdsInPhone.get(j))) {
                        subscriptionList.add(imageIdsInWeb.get(i));
                        i++;
                        break;
                    }

                    j++;
                }
                if (j == imageIdsInPhone.size()) {
                    differenceList.add(imageIdsInWeb.get(i));
                    i++;
                }

            }
            return differenceList;
        }

    }

    private class asyncGetGalleryPic extends AsyncTask<String, Void, Void> {
        private String msg = null;
        private PhotoDesc photo;
        private int photoId;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                photoId = Integer.parseInt(strings[0]);
                photo = WebService.invokeGetGalleryPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, photoId);
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
                    if (database.openConnection()) {
                        database.saveImageToGallery(photo.getId(), photo.getDate(),
                                photo.getDescription(), DbBitmapUtility.getBytes(photo.getPhoto()));
                        photos.add(photo);
                        adapter = new CustomListAdapterGallery2(gallery2.this, photos);
                        mListView.setAdapter(adapter);
                       // updateView(photos.size());
                    }
                }

            }
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
        int id = -1;
        String description = "";
        Bitmap photo = null;
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
                    asyncGetGalleryPic task = new asyncGetGalleryPic();
                    task.execute(String.valueOf(id));
                }
            }
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
                adapter.notifyDataSetChanged();
            }
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


