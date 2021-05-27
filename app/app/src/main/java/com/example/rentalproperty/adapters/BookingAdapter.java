package com.example.rentalproperty.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.rentalproperty.ProductActivity;
import com.example.rentalproperty.R;
import com.example.rentalproperty.models.Booking;
import com.example.rentalproperty.models.Good;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> implements Filterable {

    List<Good> goods;
    List<Good> goodsFull;
    List<Booking> bookings;
    Context context;

    public BookingAdapter(List<Booking> bookings, List<Good> goods, FragmentActivity activity){
        this.goods = goods;
        goodsFull = new ArrayList<>(goods);
        this.bookings = bookings;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.booking_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Good good = goods.get(position);
        final Booking booking = bookings.get(position);
        holder.textViewTitle.setText(good.getTitle());
        holder.textViewPrice.setText(((int)booking.getPrice()) + " €");
        holder.textViewLocation.setText(good.getLocation());
        holder.textViewAddress.setText(good.getAddress());
        holder.textViewDate.setText(
                "From : " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(booking.getArrival())) + "\n" +
                "To      : " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(booking.getDeparture()))
        );

        Picasso.get()
                .load(good.getImageUrl())
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.imageViewProduct);


        holder.imageViewTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Goods");
                                ref.child(good.getId()).child("bookings").child(booking.getId()).removeValue();
                                Toast.makeText(context, context.getText(R.string.deleted_book), Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.sure_delete_book).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
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
        ImageView imageViewProduct, imageViewTrash;
        TextView textViewTitle, textViewPrice, textViewLocation, textViewDate, textViewAddress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProduct = itemView.findViewById(R.id.imageView);
            imageViewTrash = itemView.findViewById(R.id.imageView2);
            textViewTitle = itemView.findViewById(R.id.booking_title);
            textViewDate = itemView.findViewById(R.id.booking_from);
            textViewPrice = itemView.findViewById(R.id.booking_price);
            textViewLocation = itemView.findViewById(R.id.booking_location);
            textViewAddress = itemView.findViewById(R.id.booking_address);
        }
    }
}
