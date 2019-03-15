package com.scandecode_example.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scandecode_example.R;

import java.util.List;

/**
 * @author xu
 */
public class UnitAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public UnitAdapter(@Nullable List<String> data) {
        super(R.layout.view_amongst_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.pName, item);
    }

}
