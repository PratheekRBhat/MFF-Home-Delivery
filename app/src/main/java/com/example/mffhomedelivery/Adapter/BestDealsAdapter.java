package com.example.mffhomedelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.example.mffhomedelivery.R;

import java.util.List;

import Model.BestDeals;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BestDealsAdapter extends LoopingPagerAdapter<BestDeals> {

    @BindView(R.id.img_best_deals)
    ImageView img_best_deal;

    @BindView(R.id.txt_best_deals)
    TextView txt_best_deal;

    Unbinder unbinder;

    public BestDealsAdapter(Context context, List<BestDeals> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.layout_best_deals_items, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        unbinder = ButterKnife.bind(this, convertView);
        Glide.with(convertView).load(itemList.get(listPosition).getImage()).into(img_best_deal);
        txt_best_deal.setText(itemList.get(listPosition).getName());
    }
}