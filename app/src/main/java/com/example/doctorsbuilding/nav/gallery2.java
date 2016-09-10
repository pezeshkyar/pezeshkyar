package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Util.MessageBox;

import java.util.ArrayList;

/**
 * Created by hossein on 9/8/2016.
 */
public class gallery2 extends Activity {
    private ListView mListView;
    private CustomListAdapterGallery2 adapter;
    private ArrayList<String> items;
    private int selectedPosition;
    private ActionMode cabMode = null;
    private ActionMode modeState=  null;
    private RelativeLayout layout;
    private View selectedRow;
    private EditText aboutPic;
    private ImageView editPic;
    private ImageView insertPic;
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
//            insertPic.setVisibility(View.VISIBLE);
//            editPic.setVisibility(View.GONE);
            selectedRow.setBackgroundColor(mListView.getSolidColor());
            mListView.setItemChecked(selectedPosition, false);
            selectedPosition = -1;
            adapter.notifyDataSetChanged();

        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery2);
        mListView = (ListView) findViewById(R.id.gallery2_listView);
        aboutPic = (EditText)findViewById(R.id.gallery2_about);
        editPic =(ImageView)findViewById(R.id.gallery2_apply_edit);
        insertPic = (ImageView) findViewById(R.id.gallery2_apply_image);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        items = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            items.add(" گاهی اوقات نیاز است تا اقدام به جراحی لثه کنیم. مثلا در زمان کشیدن دندان عقل باید حتما لثه را شکاف دهیم و این یکی از زمان هایی است که نیاز به این کار ضروری می باشد.");
        }
        adapter = new CustomListAdapterGallery2(gallery2.this, items);
        mListView.setAdapter(adapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                if (cabMode != null){
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
                items.set(selectedPosition, aboutPic.getText().toString());
                modeState.finish();
                aboutPic.setText("");
                insertPic.setVisibility(View.VISIBLE);
                editPic.setVisibility(View.GONE);
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
                if(aboutPic.getText().toString().equals("")){
                    insertPic.setVisibility(View.VISIBLE);
                    editPic.setVisibility(View.GONE);
                }
            }
        });

    }


    private boolean remove(){
        items.remove(selectedPosition);
        adapter.notifyDataSetChanged();
        return true;

    }
    private boolean edit(){
        aboutPic.setText(items.get(selectedPosition));
        insertPic.setVisibility(View.GONE);
        editPic.setVisibility(View.VISIBLE);
        return true;
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

}
