package com.deanlib.lordshunter.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.deanlib.lordshunter.R;
import com.deanlib.lordshunter.Utils;
import com.deanlib.lordshunter.entity.Member;
import com.deanlib.lordshunter.entity.Report;
import com.deanlib.lordshunter.ui.adapter.MemberReportAdapter;
import com.deanlib.lordshunter.ui.adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * 成员报名统计列表
 *
 * @author dean
 * @time 2018/10/12 下午4:21
 */
public class MemberReportListActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    long startTime, endTime;
    List<Member> mMemberList;
    MemberReportAdapter mMemberReportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_report_list);
        ButterKnife.bind(this);

        startTime = getIntent().getLongExtra("startTime", 0);
        endTime = getIntent().getLongExtra("endTime", 0);

        init();
        loadData();
    }

    private void init() {
        listView.setAdapter(mMemberReportAdapter = new MemberReportAdapter(mMemberList = new ArrayList<>()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ViewJump.toReportDetail(MemberReportListActivity.this, mMemberList.get((int) id).getId());
                //todo
            }
        });

    }

    private void loadData() {
        String[] chiNum = getResources().getStringArray(R.array.chi_num);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Report> nameResults = realm.where(Report.class).between("timestamp", startTime, endTime).distinct("name").findAll();
        for (Report report : nameResults) {
            long total = 0;
            for (int j = 0; j < chiNum.length; j++) {
                int level = chiNum.length - j;
                long count = realm.where(Report.class).between("timestamp", startTime, endTime).and().equalTo("name", report.getName())
                        .and().equalTo("group", report.getGroup()).and().equalTo("image.preyLevel", level)
                        .count();
                if (count != 0) {
                    total += Utils.equivalentLv1(level, count);
                }
            }
            mMemberList.add(new Member(report.getName(),report.getGroup(), total));
        }


        if (mMemberList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            Collections.sort(mMemberList);
            tvEmpty.setVisibility(View.GONE);
            mMemberReportAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.layoutBack)
    public void onViewClicked() {
        finish();
    }

}