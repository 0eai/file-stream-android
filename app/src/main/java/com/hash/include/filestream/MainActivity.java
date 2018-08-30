package com.hash.include.filestream;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hash.include.filestream.model.Fs;
import com.hash.include.filestream.viewholder.FsViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    public static String protocolKey = "protocol";
    public static String addressKey = "address";
    public static String portKey = "port";
    public static String spName = "filestream";

    private static String url;

    private LinkedList<String> prevUrl = new LinkedList<String>();
    private LinkedList<String> paths = new LinkedList<String>();
    private List<Fs> fsList = new ArrayList<>();
    private List<Fs> fsListFolder = new ArrayList<>();
    private List<Fs> fsListFile = new ArrayList<>();
    private FsAdapter mAdapter;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private TextView path_;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(spName, MODE_PRIVATE);
        editor = sp.edit();

        recyclerView = findViewById(R.id.recycler_view);
        path_ = findViewById(R.id.path_);

        mAdapter = new FsAdapter(fsList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prevUrl.addFirst(getBaseUrl());
        Log.d(TAG, "size: " + prevUrl.size());
        url = getBaseUrl();
        new GetFs().execute();
    }

    public class GetFs  extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray fsArray = new JSONArray(jsonStr);
                    fsList.clear();
                    fsListFolder.clear();
                    fsListFile.clear();
                    // looping through All Contacts

                    for (int i = 0; i < fsArray.length(); i++) {
                        JSONObject f = fsArray.getJSONObject(i);

                        String type = f.getString("type");
                        String path = f.getString("path");
                        String name = f.getString("name");
                        String extname = null;
                        if (type.equals("file")) {
                            extname = f.getString("extname");
                        }

                        String size = "0 BYTES";
                        if (type.equals("file")) {
                            double s = f.getDouble("size");
                            if (s < 1024) {
                                size = s + " BYTES";
                            } else if (s > 1024 && s < (1024 * 1024)) {
                                size = String.format("%.2f", (s / 1024)) + " KB";
                            } else if (s > (1024 * 1024) && s < (1024 * 1024 * 1024)) {
                                size = String.format("%.2f", (s / (1024 * 1024))) + " MB";
                            } else if (s > (1024 * 1024 * 1024)) {
                                size = String.format("%.2f", (s / (1024 * 1024 * 1024))) + " GB";
                            }
                        }
                        String atimeMs = f.getString("atimeMs");
                        String mtimeMs = f.getString("mtimeMs");
                        String ctimeMs = f.getString("ctimeMs");
                        String birthtimeMs = f.getString("birthtimeMs");

                        Fs fs = new Fs(type, path, name, extname, size, atimeMs, mtimeMs, ctimeMs, birthtimeMs);
                        // adding contact to contact list
                        if (type.equals("dir")){
                            fsListFolder.add(fs);
                        } else {
                            fsListFile.add(fs);
                        }
                    }
                    if (fsArray.length() > 0) {
                        fsList.addAll(fsListFolder);
                        fsList.addAll(fsListFile);
                    }
                    if (paths.isEmpty()) {
                        paths.addFirst(fsArray.getJSONObject(0).getString("path"));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            path_.setText(paths.getFirst());
            mAdapter.notifyDataSetChanged();
        }

    }

    public class FsAdapter extends RecyclerView.Adapter<FsViewHolder> {

        private List<Fs> fs;
        private Context context;

        public FsAdapter(List<Fs> fs, Context context) {
            this.fs = fs;
            this.context = context;
        }

        @Override
        public FsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                             int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new FsViewHolder(view);
        }


        @Override
        public void onBindViewHolder(FsViewHolder holder, final int position) {
            final Fs item = fs.get(position);
            holder.icon.setImageResource(item.getType().equals("file")?R.drawable.file:R.drawable.folder);
            holder.name.setText(item.getName());
            holder.size.setText(item.getSize());
            holder.size.setVisibility(item.getType().equals("dir")?View.GONE:View.VISIBLE);
            holder.download.setVisibility(item.getType().equals("dir")?View.GONE:View.VISIBLE);
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getType().equals("dir")) {
                        String id = item.getName().replace(" ", "%20");
                        String path = item.getPath().replace(" ", "%20");
                        url = getBaseUrl() + "dir?id=" + id + "&&path=" + path;
                        prevUrl.addFirst(url);
                        paths.addFirst(item.getPath() + "/" + item.getName());
                        Log.d(TAG, "size: " + prevUrl.size());
                        new GetFs().execute();
                    } else {
                        showDetailsDialog(item);
                    }
                }
            });

            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = item.getName().replace(" ", "%20");
                    String path = item.getPath().replace(" ", "%20");
                    String dUrl = getBaseUrl() + "download?id=" + id + "&&path=" + path;
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(dUrl));
                    startActivity(i);
                }
            });
            holder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDetailsDialog(item);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return fs.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "size: " + prevUrl.size());
        if (!prevUrl.isEmpty()) {
            prevUrl.pollFirst();
            paths.pollFirst();
            if (!prevUrl.isEmpty()) {
                url = prevUrl.getFirst();
                new GetFs().execute();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public  String getBaseUrl() {
        return sp.getString(protocolKey, "http") + "://" + sp.getString(addressKey,"10.58.3.146") + ":" + sp.getString(portKey,"3003") + "/";
    }

    public void showAddressDialog() {
        final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_address);
        dialog.setCancelable(true);
        final EditText protocolEditText = dialog.findViewById(R.id.protocol_edittext);
        final EditText addressEditText = dialog.findViewById(R.id.address_edittext);
        final EditText portEditText = dialog.findViewById(R.id.port_edittext);
        protocolEditText.setText(sp.getString(protocolKey, "http"));
        addressEditText.setText(sp.getString(addressKey, "10.58.3.146"));
        portEditText.setText(sp.getString(portKey, "3003"));
        Button sendButton = dialog.findViewById(R.id.dialog_save);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(protocolKey, protocolEditText.getText().toString());
                editor.putString(addressKey, addressEditText.getText().toString());
                editor.putString(portKey, portEditText.getText().toString());
                editor.apply();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDetailsDialog(Fs model) {
        final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_details);
        dialog.setCancelable(false);
        final TextView name = dialog.findViewById(R.id.dialog_name_text);
        final TextView path = dialog.findViewById(R.id.dialog_path_text);
        final TextView size = dialog.findViewById(R.id.dialog_size_text);
        final TextView atime = dialog.findViewById(R.id.dialog_atimeMs_text);
        final TextView ctime = dialog.findViewById(R.id.dialog_ctimeMs_text);
        final TextView mtime = dialog.findViewById(R.id.dialog_mtimeMs_text);
        final TextView btime = dialog.findViewById(R.id.dialog_birthtimeMs_text);

        name.setText(model.getName());
        path.setText(model.getPath());
        size.setText(model.getSize());
        atime.setText(model.getAtimeMs());
        ctime.setText(model.getCtimeMs());
        mtime.setText(model.getMtimeMs());
        btime.setText(model.getBirthtimeMs());

        Button sendButton = dialog.findViewById(R.id.dialog_ok);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_refresh) {
            new GetFs().execute();
            return true;
        }
        if (id == R.id.menu_address) {
            showAddressDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
