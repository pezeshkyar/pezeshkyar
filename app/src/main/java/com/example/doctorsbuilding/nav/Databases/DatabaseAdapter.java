package com.example.doctorsbuilding.nav.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.PhotoDesc;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.SubExpert;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hossein on 8/30/2016.
 */
public class DatabaseAdapter {

    private static final int DATABASE_VERSION = 1;
    private static final int imageProfileId = 1;
    private static final String DATABASE_NAME = "nav.sqlite";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_STATE = "tbl_state";
    private static final String STATE_ID = "id";
    private static final String STATE_NAME = "name";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_CITY = "tbl_city";
    private static final String CITY_ID = "id";
    private static final String CITY_STATE_ID = "stateID";
    private static final String CITY_NAME = "name";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_GALLERY = "tbl_Gallery";
    private static final String GALLERY_ID = "id";
    private static final String GALLERY_DESCRIPTION = "description";
    private static final String GALLERY_DATE = "date";
    private static final String GALLERY_DATA = "data";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_IMAGE = "tbl_image";
    private static final String IMAGE_ID = "id";
    private static final String IMAGE_DATA = "data";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_EXPERT = "tbl_expert";
    private static final String EXPERT_ID = "id";
    private static final String EXPERT_NAME = "name";
    ////////////////////////////////////////////////////////////////
    private static final String TABLE_SUB_EXPERT = "tbl_subExpert";
    private static final String SUB_EXPERT_ID = "id";
    private static final String SUB_EXPERT_EXPERT_ID = "expertId";
    private static final String SUB_EXPERT_NAME = "name";
    /////////////////////////////////////////////////////////////////
    private static final String TABLE_MESSAGE = "tbl_message";
    private static final String MESSAGE_ID = "id";
    private static final String MESSAGE_SENDER_USERNAME = "sender_username";
    private static final String MESSAGE_SENDER_FIRSTNAME = "sender_firstname";
    private static final String MESSAGE_SENDER_LASTNAME = "sender_lastname";
    private static final String MESSAGE_SUBJECT = "subject";
    private static final String MESSAGE_CONTENT = "message";
    private static final String MESSAGE_DATE = "date";
    ////////////////////////////////////////////////////////////////////
    private SQLiteDatabase database;
    private Context context;
    private final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pezeshkyar/";


    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public void initialize() {

        try {
            File file = new File(path);
            boolean b = file.mkdirs();
        } catch (Exception ex) {
            new MessageBox(context, "ایجاد دایرکتوری پایگاه داده با مشکل مواجه شده است  !!!").show();
        }

        String create_tbl_image = "create table if not exists " + TABLE_IMAGE + " (" + IMAGE_ID + " integer not null" +
                ", " + IMAGE_DATA + " blob not null, primary key (" + IMAGE_ID + ") )";

        String create_tbl_gallery = "create table if not exists " + TABLE_GALLERY + " ( " + GALLERY_ID + " integer not null, " +
                GALLERY_DATE + " nvarchar(50), " + GALLERY_DESCRIPTION + " nvarchar(200), " +
                GALLERY_DATA + " blob not null, primary key (" + GALLERY_ID + ") )";

        String create_tbl_state = "create table if not exists " + TABLE_STATE + " (" + STATE_ID + " integer not null" +
                ", " + STATE_NAME + " nvarchar(50) not null, primary key (" + STATE_ID + ") )";

        String create_tbl_city = "create table if not exists " + TABLE_CITY + " ( " + CITY_ID +
                " integer not null, " + CITY_STATE_ID + " integer not null, " + CITY_NAME +
                " nvarchar(50) not null, primary key (" + CITY_ID + "), foreign key (" + CITY_STATE_ID +
                ") references " + TABLE_STATE + "(" + STATE_ID + ") )";

        String create_tbl_expert = "create table if not exists " + TABLE_EXPERT + " ( " + EXPERT_ID +
                " integer not null, " + EXPERT_NAME + " nvarchar(50) not null )";

        String create_tbl_subExpert = "create table if not exists " + TABLE_SUB_EXPERT +
                " ( " + SUB_EXPERT_ID + " integer not null, " + SUB_EXPERT_EXPERT_ID +
                " integer not null, " + SUB_EXPERT_NAME + " nvarchar(50) not null, primary key (" + SUB_EXPERT_ID +
                "), foreign key (" + SUB_EXPERT_EXPERT_ID + ") references " + TABLE_EXPERT + "(" + EXPERT_ID + ") )";

        String create_tbl_message = "create table if not exists " + TABLE_MESSAGE + " ( " + MESSAGE_ID +
                " integer not null, " + MESSAGE_SENDER_USERNAME + " nvarchar(50) not null, " + MESSAGE_SENDER_FIRSTNAME +
                " nvarchar(50) not null, " + MESSAGE_SENDER_LASTNAME + " nvarchar(50) not null, " + MESSAGE_SUBJECT +
                " nvarchar(50), " + MESSAGE_CONTENT + " ntext(200) not null, " + MESSAGE_DATE +
                " nvarchar(50) not null, primary key (" + MESSAGE_ID + ") )";

        try {
            openConnection();
            database.execSQL(create_tbl_image);
            database.execSQL(create_tbl_gallery);
            database.execSQL(create_tbl_state);
            database.execSQL(create_tbl_city);
            database.execSQL(create_tbl_expert);
            database.execSQL(create_tbl_subExpert);
            database.execSQL(create_tbl_message);
        } catch (Exception ex) {
            new MessageBox(context, "ایجاد پایگاه داده با مشکل مواجه شده است !!!").show();
        }

    }

    public void update() {
        try {
            database.execSQL("drop table if exists " + TABLE_IMAGE);
            database.execSQL("drop table if exists " + TABLE_STATE);
            database.execSQL("drop table if exists " + TABLE_CITY);
            database.execSQL("drop table if exists " + TABLE_EXPERT);
            database.execSQL("drop table if exists " + TABLE_SUB_EXPERT);
            database.execSQL("drop table if exists " + TABLE_MESSAGE);
            database.execSQL("drop table if exists " + TABLE_GALLERY);
            initialize();

        } catch (Exception ex) {
            new MessageBox(context, "بروز رسانی پایگاه داده با مشکل مواجه شده است !!!").show();
        }
    }

    public boolean openConnection() {
        try {
            this.database = SQLiteDatabase.openOrCreateDatabase(path + DATABASE_NAME, null);
        } catch (Exception ex) {
            new MessageBox(context, "مشکلی در باز کردن کانکشن پایگاه داده بوجود آمده است !!!").show();
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        try {
            database.close();
        } catch (Exception ex) {
            new MessageBox(context, "مشکلی در بستن کانکشن پایگاه داده بوجود آمده است !!!").show();
            return false;
        }
        return true;
    }

    public long saveImageProfile(int id, byte[] image) {
        long result = -1;
        try {
            deleteImageProfile(imageProfileId);
            ContentValues cv = new ContentValues();
            cv.put(IMAGE_ID, id);
            cv.put(IMAGE_DATA, image);
            result = database.insert(TABLE_IMAGE, null, cv);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره کردن عکس با مشکل مواجه شده است !!!").show();
        }
        return result;
    }

    public Bitmap getImageProfile(int id) {
        Bitmap imageProfile = null;
        try {
            String query = "select " + IMAGE_DATA + " from " + TABLE_IMAGE + " where " + IMAGE_ID + " = " + imageProfileId;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(IMAGE_DATA));
                imageProfile = DbBitmapUtility.getImage(image);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت عکس با مشکل مواجه شده است !!!").show();
        }
        return imageProfile;
    }

    public long saveImageToGallery(int id, String date, String description, byte[] image) {
        long result = -1;
        try {
            ContentValues cv = new ContentValues();
            cv.put(GALLERY_ID, id);
            cv.put(GALLERY_DESCRIPTION, description);
            cv.put(GALLERY_DATE, date);
            cv.put(GALLERY_DATA, image);
            result = database.insert(TABLE_GALLERY, null, cv);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره کردن عکس با مشکل مواجه شده است !!!").show();
        }
        return result;
    }

    public PhotoDesc getImageFromGallery(int id) {
        PhotoDesc gallery = new PhotoDesc();
        try {
            String query = "select " + GALLERY_ID + " , " + GALLERY_DESCRIPTION + " , " + GALLERY_DATE + " , " + GALLERY_DATA + " from " +
                    TABLE_GALLERY + " where " + GALLERY_ID + " = " + id;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(GALLERY_DATA));
                gallery.setId(cursor.getInt(cursor.getColumnIndex(GALLERY_ID)));
                gallery.setDate(cursor.getString(cursor.getColumnIndex(GALLERY_DATE)));
                gallery.setPhoto(DbBitmapUtility.getImage(image));
                gallery.setDescription(cursor.getString(cursor.getColumnIndex(GALLERY_DESCRIPTION)));

            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت عکس با مشکل مواجه شده است !!!").show();
        }
        return gallery;
    }

    public void deleteImageFromGallery(int id) {
        try {
            String query = "delete from " + TABLE_GALLERY + " where " + GALLERY_ID + " = " + id;
            database.execSQL(query);

        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف عکس با مشکل مواجه شده است !!!").show();
        }

    }

    public void updateImageInGallery(int picId, String description) {

        try {
            String query = "UPDATE "+TABLE_GALLERY+ " SET " + GALLERY_DESCRIPTION + "  = '" + description + "' " +
                    "WHERE " + GALLERY_ID + " = " + picId;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات بروز رسانی عکس با مشکل مواجه شده است !!!").show();
        }
    }

    public ArrayList<Integer> getImageIds() {
        ArrayList<Integer> imageIds = new ArrayList<Integer>();
        try {
            String query = "select " + GALLERY_ID + " from " + TABLE_GALLERY;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                imageIds.add(cursor.getInt(cursor.getColumnIndex(GALLERY_ID)));
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت شناسه عکس بامشکل مواجه شده است !!!").show();
        }
        return imageIds;
    }

    private void deleteImageProfile(int id) {
        Bitmap imgProfile = getImageProfile(imageProfileId);
        try {
            if (imgProfile != null) {
                String query = "delete from " + TABLE_IMAGE + " where " + IMAGE_ID + " = " + id;
                database.execSQL(query);
            }
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف عکس با مشکل مواجه شده است !!!").show();
        }

    }

    public long insertStates(ArrayList<State> states) {
        long id = -1;
        try {
            ContentValues values;
            for (State s : states) {
                values = new ContentValues();
                values.put(STATE_ID, s.GetStateID());
                values.put(STATE_NAME, s.GetStateName());
                id = database.insert(TABLE_STATE, null, values);
            }

        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره استان ها با مشکل مواجه شده است !!!").show();
        }
        return id;
    }

    public ArrayList<State> getStates() {
        State state = null;
        ArrayList<State> states = new ArrayList<State>();
        try {
            String query = "select " + STATE_ID + ", " + STATE_NAME + " from " + TABLE_STATE;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                state = new State();
                state.SetStateID(cursor.getInt(cursor.getColumnIndex(STATE_ID)));
                state.SetStateName(cursor.getString(cursor.getColumnIndex(STATE_NAME)));
                states.add(state);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت اسامی استان ها با مشکل مواجه شده است !!!").show();
        }
        return states;
    }

    public void deleteStates() {
        try {
            String query = "delete from " + TABLE_STATE;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف استان ها با مشکل مواجه شد !!!");
        }
    }

    public long insertCities(ArrayList<City> cities) {
        long id = -1;
        try {
            ContentValues values;
            for (City c : cities) {
                values = new ContentValues();
                values.put(CITY_ID, c.GetCityID());
                values.put(CITY_STATE_ID, c.GetStateID());
                values.put(CITY_NAME, c.GetCityName());
                id = database.insert(TABLE_CITY, null, values);
            }

        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره شهر ها با مشکل مواجه شده است !!!").show();
        }
        return id;
    }

    public ArrayList<City> getCities() {
        City city = null;
        ArrayList<City> cities = new ArrayList<City>();
        try {
            String query = "select " + CITY_ID + ", " + CITY_STATE_ID + ", " + CITY_NAME + " from " + TABLE_CITY;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                city = new City();
                city.SetCityID(cursor.getInt(cursor.getColumnIndex(CITY_ID)));
                city.SetStateID(cursor.getInt(cursor.getColumnIndex(CITY_STATE_ID)));
                city.SetCityName(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
                cities.add(city);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت اسامی شهر ها با مشکل مواجه شده است !!!").show();
        }
        return cities;
    }

    public void deleteCities() {
        try {
            String query = "delete from " + TABLE_CITY;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف شهر ها با مشکل مواجه شده است !!!").show();
        }
    }

    public long insertExperts(ArrayList<Expert> experts) {
        long id = -1;
        try {
            ContentValues cv;
            for (Expert e : experts) {
                cv = new ContentValues();
                cv.put(EXPERT_ID, e.getId());
                cv.put(EXPERT_NAME, e.getName());
                id = database.insert(TABLE_EXPERT, null, cv);
            }
        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره تخصص ها با مشکل مواجه شده است !!!").show();
        }
        return id;
    }

    public ArrayList<Expert> getExperts() {
        Expert expert = null;
        ArrayList<Expert> experts = new ArrayList<Expert>();
        try {
            String query = "select " + EXPERT_ID + ", " + EXPERT_NAME + " from " + TABLE_EXPERT;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                expert = new Expert();
                expert.setId(cursor.getInt(cursor.getColumnIndex(EXPERT_ID)));
                expert.setName(cursor.getString(cursor.getColumnIndex(EXPERT_NAME)));
                experts.add(expert);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت اسامی تخصص ها با مشکل مواجه شده است !!!").show();
        }
        return experts;
    }

    public void deleteExperts() {
        try {
            String query = "delete from " + TABLE_EXPERT;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف تخصص ها با مشکل مواجه شده است !!!").show();
        }
    }

    public long insertSubExperts(ArrayList<SubExpert> subExperts) {
        long result = -1;
        try {
            ContentValues cv;
            for (SubExpert se : subExperts) {
                cv = new ContentValues();
                cv.put(SUB_EXPERT_ID, se.getId());
                cv.put(SUB_EXPERT_EXPERT_ID, se.getExpertId());
                cv.put(SUB_EXPERT_NAME, se.getName());
                result = database.insert(TABLE_SUB_EXPERT, null, cv);
            }
        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره زیر تخصص ها با مشکل مواجه شده است !!!").show();
        }
        return result;
    }

    public ArrayList<SubExpert> getSubExperts() {
        SubExpert subExpert = null;
        ArrayList<SubExpert> subExperts = new ArrayList<SubExpert>();
        try {
            String query = "select " + SUB_EXPERT_ID + ", " + SUB_EXPERT_EXPERT_ID + ", " + SUB_EXPERT_NAME + " from " + TABLE_SUB_EXPERT;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                subExpert = new SubExpert();
                subExpert.setId(cursor.getInt(cursor.getColumnIndex(SUB_EXPERT_ID)));
                subExpert.setExpertId(cursor.getInt(cursor.getColumnIndex(SUB_EXPERT_EXPERT_ID)));
                subExpert.setName(cursor.getString(cursor.getColumnIndex(SUB_EXPERT_NAME)));
                subExperts.add(subExpert);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت اسامی زیر تخصص ها با مشکل مواجه شده است !!!").show();
        }
        return subExperts;
    }

    public void deleteSubExperts() {
        try {
            String query = "delete from " + TABLE_SUB_EXPERT;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف زیر تخصص ها با مشکل مواجه شده است !!!").show();
        }
    }

    public long insertMessages(ArrayList<MessageInfo> messages) {
        long result = -1;
        try {
            ContentValues cv = null;
            for (MessageInfo m : messages) {
                cv = new ContentValues();
                cv.put(MESSAGE_ID, m.getId());
                cv.put(MESSAGE_SENDER_USERNAME, m.getSenderUsername());
                cv.put(MESSAGE_SENDER_FIRSTNAME, m.getSenderFirstName());
                cv.put(MESSAGE_SENDER_LASTNAME, m.getSenderLastName());
                cv.put(MESSAGE_SUBJECT, m.getSubject());
                cv.put(MESSAGE_CONTENT, m.getMessage());
                cv.put(MESSAGE_DATE, m.getDate());
                result = database.insert(TABLE_MESSAGE, null, cv);

            }
        } catch (Exception ex) {
            new MessageBox(context, "عملیات ذخیره پیام ها با مشکل مواجه شده است !!!").show();
        }
        return result;
    }

    public ArrayList<MessageInfo> getMessages() {
        MessageInfo message = null;
        ArrayList<MessageInfo> messages = new ArrayList<MessageInfo>();
        try {
            String query = "select " + MESSAGE_ID + ", " + MESSAGE_SENDER_USERNAME + ", " + MESSAGE_SENDER_FIRSTNAME +
                    ", " + MESSAGE_SENDER_LASTNAME + ", " + MESSAGE_SUBJECT + ", " + MESSAGE_CONTENT +
                    ", " + MESSAGE_DATE + " from " + TABLE_MESSAGE;
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                message = new MessageInfo();
                message.setId(cursor.getInt(cursor.getColumnIndex(MESSAGE_ID)));
                message.setSenderUsername(cursor.getString(cursor.getColumnIndex(MESSAGE_SENDER_USERNAME)));
                message.setSenderFirstName(cursor.getString(cursor.getColumnIndex(MESSAGE_SENDER_FIRSTNAME)));
                message.setSenderLastName(cursor.getString(cursor.getColumnIndex(MESSAGE_SENDER_LASTNAME)));
                message.setSubject(cursor.getString(cursor.getColumnIndex(MESSAGE_SUBJECT)));
                message.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE_CONTENT)));
                message.setDate(cursor.getString(cursor.getColumnIndex(MESSAGE_DATE)));
                messages.add(message);
            }
            cursor.close();
        } catch (Exception ex) {
            new MessageBox(context, "عملیات دریافت پیام ها با مشکل مواجه شده است !!!").show();
        }
        return messages;
    }

    public void deleteMessages() {
        try {
            String query = "delete from " + TABLE_MESSAGE;
            database.execSQL(query);
        } catch (Exception ex) {
            new MessageBox(context, "عملیات حذف پیام ها با مشکل مواجه شده است !!!").show();
        }
    }

}
