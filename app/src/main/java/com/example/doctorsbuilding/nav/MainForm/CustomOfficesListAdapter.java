package com.example.doctorsbuilding.nav.MainForm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MainActivity;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.PhotoDesc;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.SplashActivity;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 11/17/2016.
 */
public class CustomOfficesListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Office> offices;

    public CustomOfficesListAdapter(Context context, ArrayList<Office> offices) {
        this.context = context;
        this.offices = offices;
    }

    @Override
    public int getCount() {
        return offices.size();
    }

    @Override
    public Object getItem(int position) {
        return offices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        public ImageView imageView;
        public TextView name;
        public TextView expert;
        public TextView address;
        public TextView phone;
        public TextView officeCode;
        public Button btnDelete;

        public Holder(View v) {
            imageView = (ImageView) v.findViewById(R.id.offices_item_image);
            name = (TextView) v.findViewById(R.id.offices_item_name);
            expert = (TextView) v.findViewById(R.id.offices_item_expert);
            address = (TextView) v.findViewById(R.id.offices_item_address);
            phone = (TextView) v.findViewById(R.id.offices_item_phone);
            btnDelete = (Button) v.findViewById(R.id.offices_btn_delete);
            officeCode = (TextView) v.findViewById(R.id.offices_item_officeCode);
        }
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
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

        return rowView;
    }

}
