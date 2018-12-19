package com.cloudmanx.commonlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bigkoo.pickerview.AddressPickerPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hello_world)
    TextView mHelloWorld;

    AddressPickerPresenter addressPickerPresenter = new AddressPickerPresenter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.hello_world)
    public void onViewClicked() {

        addressPickerPresenter.loadJsonData();
        addressPickerPresenter.selectAddress(mHelloWorld);
    }
}
