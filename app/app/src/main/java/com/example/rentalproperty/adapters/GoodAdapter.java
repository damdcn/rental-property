package com.example.rentalproperty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalproperty.MainActivity;
import com.example.rentalproperty.R;
import com.example.rentalproperty.models.Good;

import java.util.ArrayList;
import java.util.List;

public class GoodAdapter extends RecyclerView.Adapter<GoodAdapter.ViewHolder> {

    List<Good> goods;
    Context context;

    public GoodAdapter(List<Good> goods, FragmentActivity activity){
        this.goods = goods;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.good_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Good goodsList = goods.get(position);
        holder.textViewTitle.setText(goodsList.getTitle());
        holder.textViewPrice.setText(Double.toString(goodsList.getPrice()));
        holder.textViewLocation.setText(goodsList.getLocation());
        holder.textViewDate.setText(goodsList.getDate().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(context, goodsList.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodImage;
        TextView textViewTitle, textViewPrice, textViewLocation, textViewDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goodImage = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.goodTextTitle);
            textViewPrice = itemView.findViewById(R.id.goodTextPrice);
            textViewLocation = itemView.findViewById(R.id.goodTextLocation);
            textViewDate = itemView.findViewById(R.id.goodTextDate);
        }
    }
}
