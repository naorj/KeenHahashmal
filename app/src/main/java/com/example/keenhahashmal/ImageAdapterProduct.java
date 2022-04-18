package com.example.keenhahashmal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapterProduct extends RecyclerView.Adapter<ImageAdapterProduct.ImageViewHolder>{

    private final Context mContext;
    private final List<Product> mProducts;

    public ImageAdapterProduct(Context context, List<Product> lProduct){
        mContext=context;
        mProducts=lProduct;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.row_product_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterProduct.ImageViewHolder holder, int position) {
        Product current = mProducts.get(position);

        holder.ViewDate.setText(current.getDateOfCreate());
        holder.ViewPlace.setText(current.getSupplier());
        holder.ViewName.setText(current.getName());
        holder.ViewDescription.setText(current.getDescription());
        String price="₪"+String.valueOf(current.getPrice());
        holder.ViewPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private int mDate, mMonth, mYear;
        Date date2;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());

        public TextView ViewDate;
        public TextView ViewName;
        public TextView ViewPrice;
        public TextView ViewDescription;
        public TextView ViewPlace;
        public ImageView imageViewDelete;
        public ImageView imageViewEdit;

        private DatabaseReference mDatabaseRef;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewDate = itemView.findViewById(R.id.DateTextView);
            ViewPrice = itemView.findViewById(R.id.PriceTextView);
            ViewName=itemView.findViewById(R.id.ProductTextView);
            ViewDescription=itemView.findViewById(R.id.DescriptionTextView);
            ViewPlace=itemView.findViewById(R.id.PlaceTextView);
            imageViewEdit=itemView.findViewById(R.id.editicon);
            imageViewDelete=itemView.findViewById(R.id.deleteicon);

            mDatabaseRef= FirebaseDatabase.getInstance().getReference("Product");

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product selecteditem=mProducts.get(getAdapterPosition());

                    final DialogPlus dialogPlus=DialogPlus.newDialog(mContext)
                            .setContentHolder(new ViewHolder(R.layout.dialogcontentforproduct))
                            .setExpanded(true,1150)
                            .create();

                    View myview=dialogPlus.getHolderView();
                    EditText priceOfBuy=myview.findViewById(R.id.price);
                    EditText supp=myview.findViewById(R.id.place);
                    EditText name=myview.findViewById(R.id.name);
                    EditText date=myview.findViewById(R.id.date);
                    EditText description=myview.findViewById(R.id.description);
                    Button update=myview.findViewById(R.id.usubmit);

                    priceOfBuy.setText(String.valueOf(selecteditem.getPrice()));
                    supp.setText(selecteditem.getSupplier());
                    name.setText(selecteditem.getName());
                    date.setText(selecteditem.getDateOfCreate());
                    description.setText(selecteditem.getDescription());

                    date.setOnClickListener((View v) -> {
                        final Calendar Cal=Calendar.getInstance();
                        mDate=Cal.get(Calendar.DAY_OF_MONTH);
                        mMonth=Cal.get(Calendar.MONTH);
                        mYear=Cal.get(Calendar.YEAR);
                        DatePickerDialog datePickerDialog=new DatePickerDialog(mContext, (datePicker, year, month, dayOfMonth) -> {
                            Cal.set(year, month, dayOfMonth);
                            date.setText(simpleDateFormat.format(Cal.getTime()));
                            date2=Cal.getTime();
                        },mYear,mMonth,mDate);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                        datePickerDialog.show();
                    });

                    dialogPlus.show();
                    //Log.d("TAG",String.valueOf(getAdapterPosition()));

                    update.setOnClickListener(v -> {

                        double priceTemp;
                        if (priceOfBuy.getText().toString().length() == 0) {
                            priceTemp = 0;
                        } else {
                            priceTemp = Double.parseDouble(priceOfBuy.getText().toString());
                        }

                        //update in search Mode
                        for (int i=0; i<mProducts.size();i++){
                            if (selecteditem.getmKey()==mProducts.get(i).getmKey()){
                                mProducts.get(i).setName(name.getText().toString());
                                mProducts.get(i).setPrice(priceTemp);
                                mProducts.get(i).setDescription(description.getText().toString());
                                mProducts.get(i).setSupplier(supp.getText().toString());
                                mProducts.get(i).setDateOfCreate(date.getText().toString());
                                notifyDataSetChanged();
                            }
                        }

                        Map<String,Object> map=new HashMap<>();
                        //map.put("price",Double.parseDouble(price.getText().toString()));
                        map.put("price",priceTemp);
                        map.put("supplier",supp.getText().toString());
                        map.put("name",name.getText().toString());
                        map.put("dateOfCreate",date.getText().toString());
                        map.put("description",description.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Product")
                                .child(selecteditem.getmKey()).updateChildren(map)
                                .addOnSuccessListener(unused -> {
                                    dialogPlus.dismiss();
                                    try {
                                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                })
                                .addOnFailureListener(e -> dialogPlus.dismiss());
                        Toast.makeText(mContext, "נתוני פריט עודכנו", Toast.LENGTH_SHORT).show();
                    });
                }
            });

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                    builder.setTitle("מחיקת פריט");
                    builder.setMessage("האם ברצונך למחוק את הפריט?");
                    builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Product selectedItem=mProducts.get(getAdapterPosition());
                            String selectedKey= selectedItem.getmKey();
                            mDatabaseRef.child(selectedKey).removeValue();
                            Toast.makeText(mContext, "פריט נמחק", Toast.LENGTH_SHORT).show();

                            // those 2 lines let the item disappear in search mode
                            mProducts.remove(getAdapterPosition());
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}