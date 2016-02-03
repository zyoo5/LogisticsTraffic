package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.CollectListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;

/**
 * Created by ZhangZy on 2015/7/7.
 */
public class CollectActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = CollectActivity.class.getSimpleName();
    private final boolean MY_COLLECT = true;
    private final boolean OTHER_COLLECT = false;

    private boolean pageState;
    private View myCollectType;
    private View otherCollectType;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collect);
        setPageName("收藏夹");
        setPageState(MY_COLLECT);
        //// TODO: 2016-1-28  收藏夹列表
        requestFavouriteList();
    }

    private void requestFavouriteList(){

        HttpHelper.getInstance().get(AppURL.GetFavouriteList, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }


    public void setPageState(boolean pageState) {
        this.pageState = pageState;
        if (myCollectType == null)
            myCollectType = findViewById(R.id.collect_me_type);
        if (otherCollectType == null)
            otherCollectType = findViewById(R.id.collect_other_type);
        if (listView == null)
            listView = (ListView) findViewById(R.id.collect_list);
        listView.setAdapter(null);
        listView.setOnItemClickListener(this);

        if (pageState == MY_COLLECT) {
            myCollectType.setSelected(true);
            otherCollectType.setSelected(false);
            CollectListAdapter adapter = new CollectListAdapter(true, null);
            listView.setAdapter(adapter);
            adapter.setData(User.getInstance().getCollectionList());
        } else if (pageState == OTHER_COLLECT) {
            myCollectType.setSelected(false);
            otherCollectType.setSelected(true);
            listView.setAdapter(new CollectListAdapter(false, User.getInstance().getCollectionList()));
        }
    }


    public void onClick_ChangeType(View view) {
        if (view != null) {
            setPageState(view.getId() == R.id.collect_me_type);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(DetailCompany.class);
    }
}
