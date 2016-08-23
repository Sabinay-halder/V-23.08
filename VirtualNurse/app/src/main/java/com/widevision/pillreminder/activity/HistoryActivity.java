package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.widevision.pillreminder.R;
import com.widevision.pillreminder.database.HistoryListTable;
import com.widevision.pillreminder.util.Constants;
import com.widevision.pillreminder.util.PreferenceConnector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HistoryActivity extends Activity  implements View.OnClickListener {
    @Bind(R.id.history_lists)
    ListView mHistoryList;
    @Bind(R.id.noHistoryItemsList)
    TextView mNoItemInList;
    @Bind(R.id.img_all)
    ImageView mImgAll;
    @Bind(R.id.img_bydrug)
    ImageView mImgBydrug;
    @Bind(R.id.img_bydate)
    ImageView mImgByDate;
    private LayoutInflater mInflater;
    private HistoryListViewAdapter mAdapter;
    List<HistoryListTable> historyListItems;
    List<HistoryListTable> historyListItemsByDrug;

    private DatePickerDialog fromdatepick,todatepick;
    String fromdate,todate,prefDateFormate="",x="",y="",todaydate;
    int a,defdate,defmon,defyear;
    Date mystart, myend,min,minto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.history);
        ButterKnife.bind(this);
        prefDateFormate= PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
        mImgAll.setOnClickListener(this);
        mImgBydrug.setOnClickListener(this);
        mImgByDate.setOnClickListener(this);
        mInflater = LayoutInflater.from(this);
        mImgByDate.setImageResource(R.drawable.by_date_click);
        Calendar nows = Calendar.getInstance();
        defdate=nows.get(Calendar.DATE);
        defmon=nows.get(Calendar.MONTH);
        defyear=nows.get(Calendar.YEAR);

        Calendar now = Calendar.getInstance();
        int yy = now.get(Calendar.YEAR);
        int mm = now.get(Calendar.MONTH);
        mm=mm+1;
        int dd = now.get(Calendar.DAY_OF_MONTH);
        String month = mm < 10 ? "0" + mm : "" + mm;
        String day = dd < 10 ? "0" + dd : "" + dd;
        todaydate = day + "-" + (month) + "-" + yy;
        initListWithToday();
    }

    private void initListWithData() {
        new AdptAll().execute();
    }
    private void initListWithToday() {
        new AdptToday().execute();
    }

    private void initListWithDate() {
        new AdptDate().execute();
    }

    private void initListWithDrugName() {
        new Adpt().execute();
    }

    @Override
    public void onClick(View v) {
        if (Constants.buttonEnable) {
            Constants.setButtonEnable();
            switch (v.getId()) {
                case R.id.img_bydrug:
                    mImgBydrug.setImageResource(R.drawable.druh_click);
                    mImgAll.setImageResource(R.drawable.all_button);
                    mImgByDate.setImageResource(R.drawable.by_date);
                    initListWithDrugName();
                    break;
                case R.id.img_all:

                    mImgBydrug.setImageResource(R.drawable.by_drug);
                    mImgAll.setImageResource(R.drawable.all_click);
                    mImgByDate.setImageResource(R.drawable.by_date);
                    initListWithData();
                    break;
                case R.id.img_bydate:
                    mImgBydrug.setImageResource(R.drawable.by_drug);
                    mImgAll.setImageResource(R.drawable.all_button);
                    mImgByDate.setImageResource(R.drawable.by_date_click);

                    LayoutInflater li = LayoutInflater.from(getParent());
                    View promptsView = li.inflate(R.layout.date_range_dialog, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParent());
                    alertDialogBuilder.setView(promptsView);
                    final TextView from = (TextView) promptsView.findViewById(R.id.from_select);
                    final TextView to = (TextView) promptsView.findViewById(R.id.to_select);
                    final LinearLayout fromlayout = (LinearLayout) promptsView.findViewById(R.id.from_layout);
                    final LinearLayout tolayout = (LinearLayout) promptsView.findViewById(R.id.to_layout);
                    // set dialog message
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar now = Calendar.getInstance();
                    int yy = now.get(Calendar.YEAR);
                    int mm = now.get(Calendar.MONTH);
                    mm=mm+1;
                    int dd = now.get(Calendar.DAY_OF_MONTH);
                    String month = mm < 10 ? "0" + mm : "" + mm;
                    String day = dd < 10 ? "0" + dd : "" + dd;
                      fromdate = day + "-" + (month) + "-" + yy;
                      todate = day + "-" + (month) + "-" + yy;
                       myend = new Date();
                       mystart = new Date();
                    try {
                        myend = dateFormat.parse(todate);
                        mystart = dateFormat.parse(fromdate);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    prefDateFormate=PreferenceConnector.readString(getApplicationContext(), "dateselectionformate", "");
                    if(prefDateFormate.equals("MM-DD-YYYY")){
                        from.setText(month+"-"+day+"-"+yy);
                        to.setText(month+"-"+day+"-"+yy);
                    }
                    else if(prefDateFormate.equals("DD-MM-YYYY")){
                        from.setText(fromdate);
                        to.setText(todate);
                    }
                   else if(prefDateFormate.equals("YYYY-MM-DD")){
                        from.setText(yy+"-"+month+"-"+day);
                        to.setText(yy+"-"+month+"-"+day);
                    }

                    fromlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar now = Calendar.getInstance();

                            fromdatepick = new DatePickerDialog(getParent(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                    int a=monthOfYear+1;
                                    String month = a < 10 ? "0" + a : "" + a;
                                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                    fromdate = day + "-" + month + "-" + year;
                                    defdate= dayOfMonth;
                                    defmon= monthOfYear;
                                    defyear=year;
                                    if(prefDateFormate.equals("MM-DD-YYYY")){
                                        from.setText(month + "-" + day + "-" + year);
                                    }
                                    else if(prefDateFormate.equals("DD-MM-YYYY")){
                                        from.setText(fromdate);
                                    }
                                   else if(prefDateFormate.equals("YYYY-MM-DD")){
                                        from.setText(year + "-" + month + "-" + day);
                                    }
                                    x = fromdate;
                                    try {
                                        mystart = dateFormat.parse(x);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                    fromdatepick.dismiss();

                                }

                            },defyear,defmon,defdate);

                            fromdatepick.setCancelable(false);
                            fromdatepick.show();
                        }
                    });
                    tolayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar now = Calendar.getInstance();

                            todatepick = new DatePickerDialog(getParent(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                    int a=monthOfYear+1;
                                    String month = a < 10 ? "0" + a : "" + a;
                                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                    todate = day + "-" + month + "-" + year;
                                    defdate= dayOfMonth;
                                    defmon= monthOfYear;
                                    defyear=year;

                                    if(prefDateFormate.equals("MM-DD-YYYY")){
                                        to.setText(month + "-" + day + "-" + year);
                                    }
                                    else if(prefDateFormate.equals("DD-MM-YYYY")){
                                        to.setText(todate);
                                    }
                                   else if(prefDateFormate.equals("YYYY-MM-DD")){
                                        to.setText(year + "-" + month + "-" + day);
                                    }
                                    y = todate;


                                    try {
                                        myend = dateFormat.parse(y);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    todatepick.dismiss();

                                }

                            }, defyear,defmon,defdate);
                            todatepick.setCancelable(false);
                            todatepick.show();
                        }
                    });
                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if ((myend!=null)&&(myend.before(mystart))){
                                Constants.alert(getParent(), "From date should be less than To date.");
                            }
                            else {
                                initListWithDate();
                            }

                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog1 = alertDialogBuilder.create();
                    alertDialog1.show();
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initListWithData();


    }

    public class HistoryListViewAdapter extends BaseAdapter {
        private List<HistoryListTable> mData;
        private LayoutInflater mInflater;

        public HistoryListViewAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public List<HistoryListTable> getData() {
            return mData;
        }

        public void setData(List<HistoryListTable> data) {
            this.mData = data;
            if (data != null) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (mData != null && !mData.isEmpty()) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mData != null && !mData.isEmpty()) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View row = convertView;
            ViewHolder1 holder = null;
            row = mInflater.inflate(R.layout.history_list_items, parent, false);
            holder = new ViewHolder1(row);
            if (mData != null && !mData.isEmpty()) {
                HistoryListTable currentPerson = mData.get(position);
                initValues(holder, currentPerson, position);
            }

            return row;
        }

        public void initValues(ViewHolder1 holder, HistoryListTable history, int position) {

            holder.drugName.setText(history.drugName);
            if(prefDateFormate.equals("MM-DD-YYYY")){
                String[] split=history.date.split("-");
                String fordate = split[0];
                String formonth = split[1];
                String foryear=split[2];
                holder.takendate.setText(formonth + "-" + fordate + "-" + foryear);
            }
            else if(prefDateFormate.equals("DD-MM-YYYY")){
                holder.takendate.setText(history.date);
            }
            else if(prefDateFormate.equals("YYYY-MM-DD")){
                String[] split=history.date.split("-");
                String fordate = split[0];
                String formonth = split[1];
                String foryear=split[2];
                holder.takendate.setText(foryear + "-" + formonth + "-" + fordate);
            }

            holder.quantity.setText("Quantity: " + history.dosage);
            String a = history.takenTime;

            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                final Date dateObj = sdf.parse(a);
                String aaas=new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase();
                String[] str_array = aaas.split(":");
                String stringa = str_array[0];
                String  stringb = str_array[1];
                if(stringa.equals("0")){
                    stringa="12";
                    holder.takentime.setText(stringa+":"+stringb);
                }
                else {
                    holder.takentime.setText(new SimpleDateFormat("K:mm a").format(dateObj).toUpperCase());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (history.status.equals("taken")) {
                holder.drugImages.setImageResource(R.drawable.taken);
            } else if (history.status.equals("skipped")) {
                holder.drugImages.setImageResource(R.drawable.skipped);
            }


        }

        class ViewHolder1 {
            TextView drugName;
            TextView takendate;
            ImageView drugImages;
            TextView takentime;
            TextView quantity;


            public ViewHolder1(View view) {
                // TODO Auto-generated constructor stub
                takendate = (TextView) view
                        .findViewById(R.id.takendate);

                drugName = (TextView) view
                        .findViewById(R.id.listDrugNameTextView);
                drugImages = (ImageView) view
                        .findViewById(R.id.listShowImageView);

                takentime = (TextView) view.findViewById(R.id.listTimeTextView);
                quantity = (TextView) view.findViewById(R.id.listUnitTextView);

            }
        }
    }

    public class Adpt extends AsyncTask<Void, List<HistoryListTable>, List<HistoryListTable>> {
        ProgressDialog progressDialog;

        @Override
        protected List<HistoryListTable> doInBackground(Void... voids) {

            historyListItemsByDrug = new Select().from(HistoryListTable.class).orderBy("_drugName asc").execute();
            return historyListItemsByDrug;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getParent());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading data");
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(List<HistoryListTable> aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            mAdapter = new HistoryListViewAdapter(getParent());
            mAdapter.setData(historyListItemsByDrug);
            mHistoryList.setAdapter(mAdapter);

            mHistoryList.setEmptyView(mNoItemInList);

        }
    }

    public class AdptAll extends AsyncTask<Void, List<HistoryListTable>, List<HistoryListTable>> {
        ProgressDialog progressDialog1;

        @Override
        protected List<HistoryListTable> doInBackground(Void... voids) {
            historyListItems = new Select().from(HistoryListTable.class).orderBy("Id desc").execute();
            return historyListItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(getParent());
            progressDialog1.setMessage("Loading data");
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(List<HistoryListTable> aVoid) {
            super.onPostExecute(aVoid);
            progressDialog1.dismiss();
            mAdapter = new HistoryListViewAdapter(getParent());
            mAdapter.setData(historyListItems);
            mHistoryList.setAdapter(mAdapter);
            mHistoryList.setEmptyView(mNoItemInList);

        }
    }

 public class AdptDate extends AsyncTask<Void, List<HistoryListTable>, List<HistoryListTable>> {
        ProgressDialog progressDialog3;
        List<HistoryListTable> historyListItemsdate;
        Cursor cursor = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog3 = new ProgressDialog(getParent());
            progressDialog3.setMessage("Loading data");
            progressDialog3.setCancelable(false);
            progressDialog3.show();
        }

        @Override
        protected List<HistoryListTable> doInBackground(Void... voids) {
            historyListItemsdate = new Select().from(HistoryListTable.class).where("_date >= ? and _date <= ?",fromdate,todate).execute();
            return historyListItemsdate;
        }

        @Override
        protected void onPostExecute(List<HistoryListTable> aVoid) {
            super.onPostExecute(aVoid);
            progressDialog3.dismiss();
            mAdapter = new HistoryListViewAdapter(getParent());
            mAdapter.setData(historyListItemsdate);
            mHistoryList.setAdapter(mAdapter);
        }
    }

    public class AdptToday extends AsyncTask<Void, List<HistoryListTable>, List<HistoryListTable>> {
        ProgressDialog progressDialog4;
        List<HistoryListTable> historyListItemsdatess;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog4 = new ProgressDialog(getParent());
            progressDialog4.setMessage("Loading data");
            progressDialog4.setCancelable(false);
            progressDialog4.show();
        }

        @Override
        protected List<HistoryListTable> doInBackground(Void... voids) {
            historyListItemsdatess = new Select().from(HistoryListTable.class).where("_date = ?", todaydate).execute();
            return historyListItemsdatess;
        }

        @Override
        protected void onPostExecute(List<HistoryListTable> aVoid) {
            super.onPostExecute(aVoid);
            progressDialog4.dismiss();
            mAdapter = new HistoryListViewAdapter(getParent());
            mAdapter.setData(historyListItemsdatess);
            mHistoryList.setAdapter(mAdapter);
        }
    }
}