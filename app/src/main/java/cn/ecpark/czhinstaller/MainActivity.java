package cn.ecpark.czhinstaller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String THE_PAGE =
            "http://code.net.ecpark.cn:8888/tree/iAuto360-Release.git/master/Android%2FProduct";

    private ListView apkListLv;
    private ApkListAdapter mAdapter;
    private ArrayList<ApkListAdapter.ItemData> mData;
    private Handler mHandler;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apkListLv = (ListView) this.findViewById(R.id.main_type_list_lv);
        mData = new ArrayList<>();
        mAdapter = new ApkListAdapter(this, mData);
        apkListLv.setAdapter(mAdapter);
        mHandler = new Handler();

        apkListLv.setOnItemClickListener(this);

        mDialog = ProgressDialog.show(this, null, "Data loading");
        Util.doRequest(
                THE_PAGE,
                new Util.Callback() {
                    @Override
                    public void success(ArrayList<String> content, int code) {
                        mDialog.cancel();
                        for (String str:content){
                            ApkListAdapter.ItemData itemData = new ApkListAdapter.ItemData();
                            itemData.name = str;
                            itemData.url = "Click to get list";
                            mData.add(itemData);
                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void fail(int code) {
                        mDialog.cancel();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("type", mData.get(position).name);
        startActivity(intent);
    }
}
