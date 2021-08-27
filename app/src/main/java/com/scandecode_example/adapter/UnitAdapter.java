package com.scandecode_example.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scandecode_example.R;
import com.scandecode_example.model.DataBean;

import java.util.List;

/**
 * @author xu
 */
public class UnitAdapter extends BaseQuickAdapter<DataBean, BaseViewHolder> {
    public UnitAdapter(@Nullable List<DataBean> data) {
        super(R.layout.view_amongst_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DataBean item) {
        helper.setText(R.id.pName, item.getBarcode());
    }

}
