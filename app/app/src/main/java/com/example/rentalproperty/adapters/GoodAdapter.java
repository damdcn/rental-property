package com.example.rentalproperty.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.rentalproperty.ProductActivity;
import com.example.rentalproperty.R;
import com.example.rentalproperty.models.Good;

import java.text.SimpleDateFormat;
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
        final Good good = goods.get(position);
        holder.textViewTitle.setText(good.getTitle());
        holder.textViewPrice.setText(Double.toString(good.getPrice()) + " €");
        holder.textViewLocation.setText(good.getLocation());
        holder.textViewDate.setText(new SimpleDateFormat("dd/mm/yyyy").format(good.getDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("TITLE", good.getTitle());
                intent.putExtra("LOCATION", good.getLocation());
                intent.putExtra("DATE", new SimpleDateFormat("dd/mm/yyyy").format(good.getDate()));
                intent.putExtra("PRICE", Double.toString(good.getPrice()) + " €");
                context.startActivity(intent);
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
