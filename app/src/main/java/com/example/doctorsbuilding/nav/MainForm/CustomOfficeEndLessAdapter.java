package com.example.doctorsbuilding.nav.MainForm;

/**
 * Created by hossein on 10/17/2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.SplashActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

public class CustomOfficeEndLessAdapter extends BaseAdapter {

    private ArrayList<Office> offices;
    private Context context;

    public CustomOfficeEndLessAdapter(Context context, ArrayList<Office> offices) {
        this.context = context;
        this.offices = offices;
    }

    @Override
    public int getCount() {
        return offices.size();
    }

    @Override
    public Office getItem(int position) {
        return offices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return offices.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.offices_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.imageView.setImageBitmap(offices.get(position).getPhoto());
        holder.name.setText(offices.get(position).getFirstname().concat(" " + offices.get(position).getLastname()));
        holder.expert.setText(offices.get(position).getSubExpertName());
        holder.address.setText(offices.get(position).getAddress());
        holder.phone.setText("تلفن : ".concat(offices.get(position).getPhone()));
        holder.officeCode.setText("کد مطب" + ": " + offices.get(position).getId());
        holder.btnDelete.setVisibility(View.GONE);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    G.officeId = offices.get(position).getId();
                    context.startActivity(new Intent(context, SplashActivity.class));
                } else {
                    context.startActivity(new Intent(context, SignInActivity.class));
                }
            }
        });
        holder.btnFavorite.setVisibility(View.VISIBLE);
        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnFavorite.setEnabled(false);
                AsyncInsertOffice task = new AsyncInsertOffice();
                task.holder = holder;
                task.position = position;
                task.execute();
            }
        });

        return rowView;

    }

    public void add(Office office) {
        offices.add(office);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Office> officeha) {
        offices.clear();
        offices.addAll(officeha);
        notifyDataSetChanged();
    }

    public void remove(Office office) {
        offices.remove(office);
        notifyDataSetChanged();
    }

    public void removeAll(ArrayList<Office> officeha) {
        offices.removeAll(officeha);
        notifyDataSetChanged();
    }

    public void update(int position, Office office) {
        offices.set(position, office);
        notifyDataSetChanged();
    }

    class Holder {
        public ImageView imageView;
        public TextView name;
        public TextView expert;
        public TextView address;
        public TextView phone;
        public TextView officeCode;
        public Button btnDelete;
        public Button btnFavorite;

        public Holder(View v) {
            imageView = (ImageView) v.findViewById(R.id.offices_item_image);
            name = (TextView) v.findViewById(R.id.offices_item_name);
            expert = (TextView) v.findViewById(R.id.offices_item_expert);
            address = (TextView) v.findViewById(R.id.offices_item_address);
            phone = (TextView) v.findViewById(R.id.offices_item_phone);
            btnDelete = (Button) v.findViewById(R.id.offices_btn_delete);
            btnFavorite = (Button) v.findViewById(R.id.offices_btn_favorite);
            officeCode = (TextView) v.findViewById(R.id.offices_item_officeCode);
        }
    }

    private class AsyncInsertOffice extends AsyncTask<String, Void, Void> {
        String msg = null;
        String result = null;
        Office office;
        int position;
        CustomOfficeEndLessAdapter.Holder holder;
        DatabaseAdapter database = new DatabaseAdapter(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                office = offices.get(position);
                result = WebService.invokeAddOfficeForUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), office.getId());

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                holder.btnFavorite.setEnabled(true);
                new MessageBox(context, msg).show();
            } else {
                if (result != null && result.toUpperCase().equals("OK")) {
                    Animation pulse = AnimationUtils.loadAnimation(context, R.anim.like_anim);
                    holder.btnFavorite.startAnimation(pulse);
                    holder.btnFavorite.setBackgroundResource(R.drawable.ic_heart_red_24);
                    if (office != null) {
                        office.setMyOffice(false);
                        database = new DatabaseAdapter(context);
                        if (database.openConnection()) {
                            long result = database.insertoffice(office);
                            if (result == -1) {
                                database.updateOffice(office.getId(), office);
                            }
                            database.closeConnection();
                        }
                    }
                } else {
                    holder.btnFavorite.setEnabled(true);
                    new MessageBox(context, result).show();
                }
            }
        }
    }

}
