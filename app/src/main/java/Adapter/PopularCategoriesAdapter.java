package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedelivery.R;

import java.util.List;

import Model.PopularCategories;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.MyViewHolder> {
    Context context;
    List<PopularCategories> popularCategoriesList;

    public PopularCategoriesAdapter(Context context, List<PopularCategories> popularCategoryModelList) {
        this.context = context;
        this.popularCategoriesList = popularCategoryModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(popularCategoriesList.get(position).getImage()).into(holder.categoryImg);
        holder.categoryNameTV.setText(popularCategoriesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return popularCategoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView categoryNameTV;

        @BindView(R.id.category_image)
        CircleImageView categoryImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
