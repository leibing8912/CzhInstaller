package cn.ecpark.czhinstaller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.OnRefreshListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;

/**
 * @className: DetailActivity
 * @classDescription: 详细列表页，这一页列出所有可供下载的apk
 * @author: swallow
 * @createTime: 2016/1/19
 */
public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SwipyRefreshLayout mRefreshLayout;
    private ListView mLvContent;
    private ArrayList<ApkListAdapter.ItemData> mData;
    private ApkListAdapter mAdapter;
    private Handler mHandler;
    private String mType;
    private String mUrl;
    private ProgressDialog mDialog;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection direction) {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                update(mUrl);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mType = getIntent().getStringExtra("type");
        mUrl = MainActivity.THE_PAGE + "%2F" + mType;

        super.onCreate(savedInstanceState);
        setTitle(mType+" apk");
        setContentView(R.layout.activity_detail);

        //Refresh
        mRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.detail_refresh);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mRefreshLayout.setOnRefreshListener(onRefreshListener);

        //List
        mLvContent = (ListView) findViewById(R.id.detail_apk_list_lv);
        mData = new ArrayList<>();
        mAdapter = new ApkListAdapter(this, mData);
        mLvContent.setAdapter(mAdapter);
        mHandler = new Handler();
        mLvContent.setOnItemClickListener(this);

        //Do initial request
        update(mUrl);
    }

    private void update(String url) {
        mDialog = ProgressDialog.show(this, null, "Data loading...");
        Util.doRequest(
                url,
                new Util.Callback() {
                    @Override
                    public void success(ArrayList<String> content, int code) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(false);
                            }
                        });
                        mDialog.cancel();
                        mData.clear();
                        for (String str : content) {
                            ApkListAdapter.ItemData itemData = new ApkListAdapter.ItemData();
                            itemData.name = getNameFromUrl(str);
                            itemData.url = str;
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(false);
                            }
                        });
                        mDialog.cancel();
                        Toast.makeText(
                                DetailActivity.this,
                                "Get apk list fail.",
                                Toast.LENGTH_LONG
                        );
                    }
                });
    }

    private String getNameFromUrl(String url) {
        String result = url.substring(url.lastIndexOf("/") + 1, url.indexOf(".apk"));
        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(position).url));
        startActivity(intent);
    }
}
