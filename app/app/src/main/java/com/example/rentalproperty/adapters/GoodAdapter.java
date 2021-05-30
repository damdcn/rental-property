package com.example.rentalproperty.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalproperty.MainActivity;
import com.example.rentalproperty.ProductActivity;
import com.example.rentalproperty.R;
import com.example.rentalproperty.models.Booking;
import com.example.rentalproperty.models.Good;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoodAdapter extends RecyclerView.Adapter<GoodAdapter.ViewHolder> implements Filterable {

    List<Good> goods;
    List<Good> goodsFull;
    Context context;

    public GoodAdapter(List<Good> goods, FragmentActivity activity){
        this.goods = goods;
        goodsFull = new ArrayList<>(goods);
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
        holder.textViewDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(good.getDate()));

        Picasso.get()
                .load(good.getImageUrl().isEmpty() ? null : good.getImageUrl())
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.imageViewProduct);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("ID", good.getId());
                intent.putExtra("TITLE", good.getTitle());
                intent.putExtra("LOCATION", good.getLocation());
                intent.putExtra("ADDRESS", good.getAddress());
                intent.putExtra("CATEGORY", good.getCategory());
                intent.putExtra("DESCRIPTION", good.getDescription());
                intent.putExtra("MAXSTAY", good.getMaxStaying());
                intent.putExtra("PHONE", good.getContactNumber());
                intent.putExtra("IMG_URL", good.getImageUrl());
                intent.putExtra("AUTHOR_ID", good.getAuthorId());
                intent.putExtra("DATE", new SimpleDateFormat("dd/MM/yyyy").format(good.getDate()));
                intent.putExtra("PRICE", Double.toString(good.getPrice()) + " €");
                intent.putExtra("RATE", good.getRate());
                intent.putExtra("BOOKINGS", good.getBookings());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    @Override
    public Filter getFilter() {
        return goodFilter;
    }

    private Filter goodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Good> filterdList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filterdList.addAll(goodsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Good good : goodsFull) {
                    if(good.getTitle().toLowerCase().contains(filterPattern) || good.getLocation().toLowerCase().contains(filterPattern)){
                        filterdList.add(good);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterdList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            goods.clear();
            goods.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewTitle, textViewPrice, textViewLocation, textViewDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProduct = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.goodTextTitle);
            textViewPrice = itemView.findViewById(R.id.goodTextPrice);
            textViewLocation = itemView.findViewById(R.id.goodTextLocation);
            textViewDate = itemView.findViewById(R.id.goodTextDate);
        }
    }
}
