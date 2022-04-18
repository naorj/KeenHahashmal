package com.example.keenhahashmal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<Item> mItems;
    public ImageAdapter(Context context, List<Item> items){
        mContext=context;
        mItems=items;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.row_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Item current = mItems.get(position);
        //holder.ViewDate.setText(changeDateFormat(current.getDateOfCreate()));
        holder.ViewDate.setText(current.getDateOfCreate());
        //holder.ViewInvoice.setText(current.getInvoiceNumber());
        holder.ViewSupplier.setText(current.getNameOfSupplier());
        String price="₪"+String.valueOf(current.getPrice());
        holder.ViewPrice.setText(price);

            Glide.with(this.mContext)
                    .load(current.getUri())
                    .error(R.drawable.unnamed)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.01f)
                    .override(200,200)
                    .dontAnimate()
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
       return mItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private int mDate, mMonth, mYear;
        Date date2;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());

        public TextView ViewDate;
        //public TextView ViewInvoice;
        public TextView ViewSupplier;
        public TextView ViewPrice;
        public ImageView imageView;
        public ImageView imageViewDelete;
        public ImageView imageViewEdit;

        private FirebaseStorage mStorage;
        private DatabaseReference mDatabaseRef;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewDate = itemView.findViewById(R.id.DateTextView);
            ViewPrice = itemView.findViewById(R.id.PriceTextView);
            ViewSupplier=itemView.findViewById(R.id.SupplierTextView);
            //ViewInvoice=itemView.findViewById(R.id.InvoiceTextView);
            imageView=itemView.findViewById(R.id.ItemImageView);
            imageViewEdit=itemView.findViewById(R.id.editicon);
            imageViewDelete=itemView.findViewById(R.id.deleteicon);

            mStorage=FirebaseStorage.getInstance();
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("Item");

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent f=new Intent(mContext,FullScreenActivity.class);
                    Item selecteditem=mItems.get(getAdapterPosition());
                    if(selecteditem.getUri()!=null){
                        f.setData(Uri.parse(selecteditem.getUri()));
                        mContext.startActivity(f);
                    }
                    else{
                        Toast.makeText(mContext,"אין תמונה להצגה",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item selecteditem=mItems.get(getAdapterPosition());
                    final DialogPlus dialogPlus=DialogPlus.newDialog(mContext)
                            .setContentHolder(new ViewHolder(R.layout.dialogcontent))
                            .setExpanded(true,1300)
                            .create();

                    View myview=dialogPlus.getHolderView();
                    EditText price=myview.findViewById(R.id.price);
                    EditText supp=myview.findViewById(R.id.usupplier);
                    EditText invoice=myview.findViewById(R.id.uinvoice);
                    EditText date=myview.findViewById(R.id.udatecreate);
                    EditText exgroup=myview.findViewById(R.id.uexpensegroup);
                    EditText extype=myview.findViewById(R.id.uexpensetype);
                    Button update=myview.findViewById(R.id.usubmit);

                    price.setText(String.valueOf(selecteditem.getPrice()));
                    supp.setText(selecteditem.getNameOfSupplier());
                    invoice.setText(selecteditem.getInvoiceNumber());
                    //date.setText(changeDateFormat(selecteditem.getDateOfCreate()));
                    date.setText(selecteditem.getDateOfCreate());

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

                    exgroup.setText(selecteditem.getExpenseGroup());
                    extype.setText(selecteditem.getExpenseType());

                    dialogPlus.show();
                    update.setOnClickListener(v -> {

                        double priceTemp;
                        if (price.getText().toString().length() == 0) {
                            priceTemp = 0;
                        } else {
                            priceTemp = Double.parseDouble(price.getText().toString());
                        }

                        //update in search Mode
                        for (int i=0; i<mItems.size();i++){
                            if (selecteditem.getKey()==mItems.get(i).getKey()){
                                mItems.get(i).setDateOfCreate(date.getText().toString());
                                mItems.get(i).setPrice(priceTemp);
                                mItems.get(i).setExpenseGroup(exgroup.getText().toString());
                                mItems.get(i).setExpenseType(extype.getText().toString());
                                mItems.get(i).setInvoiceNumber(invoice.getText().toString());
                                mItems.get(i).setNameOfSupplier(supp.getText().toString());
                                notifyDataSetChanged();
                            }
                        }

                        Map<String,Object> map=new HashMap<>();
                        //map.put("price",Double.parseDouble(price.getText().toString()));
                        map.put("price",priceTemp);
                        map.put("nameOfSupplier",supp.getText().toString());
                        map.put("invoiceNumber",invoice.getText().toString());
                        map.put("dateOfCreate",date.getText().toString());
                        map.put("expenseGroup",exgroup.getText().toString());
                        map.put("expenseType",extype.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Item")
                                .child(selecteditem.getKey()).updateChildren(map)
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
                            Item selectedItem=mItems.get(getAdapterPosition());
                            String selectedKey= selectedItem.getKey();
                            StorageReference imageref;
                            if(selectedItem.getUri()!=null){
                                imageref=mStorage.getReferenceFromUrl(selectedItem.getUri());
                                imageref.delete().addOnSuccessListener(aVoid -> {
                                    mDatabaseRef.child(selectedKey).removeValue();
                                    Toast.makeText(mContext, "פריט נמחק", Toast.LENGTH_SHORT).show();
                                });
                            }
                            else{
                                Toast.makeText(mContext, "פריט נמחק", Toast.LENGTH_SHORT).show();
                                mDatabaseRef.child(selectedKey).removeValue();
                            }
                            // those 2 lines let the item disappear in search mode
                            mItems.remove(getAdapterPosition());
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
    public String changeDateFormat(String date){
        Date parsed;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        try {
            parsed = df_input.parse(date);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
        }
        return outputDate;
    }
}