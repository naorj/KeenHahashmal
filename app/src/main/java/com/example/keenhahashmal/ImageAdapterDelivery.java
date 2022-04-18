package com.example.keenhahashmal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageAdapterDelivery extends RecyclerView.Adapter<ImageAdapterDelivery.ImageViewHolder>{

    private final Context mContext;
    private final List<DeliveryItem> mdItems;

    public ImageAdapterDelivery(Context context, List<DeliveryItem> deliveryItemsitems){
        mContext=context;
        mdItems=deliveryItemsitems;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.row_delivery_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        DeliveryItem current = mdItems.get(position);
        //holder.ViewDate.setText(changeDateFormat(current.getDateOfCreate()));
        holder.ViewSupplier.setText(current.getNameOfSupplier());
        holder.ViewDate.setText(current.getDateOfCreate());
        holder.ViewIsPaid.setText(current.getIsPaid());

        if(current.getIsPaid().equals("לא שולם")){
            holder.ViewIsPaid.setTextColor(Color.RED);
            holder.sw.setChecked(false);
        }
        else{
            holder.ViewIsPaid.setTextColor(Color.parseColor("#49b675"));
            holder.sw.setChecked(true);
        }

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
        return mdItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private int mDate, mMonth, mYear;
        Date date2;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());

        public TextView ViewDate;
        public TextView ViewIsPaid;
        public TextView ViewPrice;
        public TextView ViewSupplier;
        public ImageView imageView;
        public ImageView imageViewDelete;
        public ImageView imageViewEdit;

        public SwitchCompat sw;

        private FirebaseStorage mStorage;
        private DatabaseReference mDatabaseRef;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewDate = itemView.findViewById(R.id.DateTextView);
            ViewPrice = itemView.findViewById(R.id.PriceTextView);
            ViewIsPaid=itemView.findViewById(R.id.IsPaidTextView);
            ViewSupplier=itemView.findViewById(R.id.supplierTextView);
            imageView=itemView.findViewById(R.id.ItemImageView);
            imageViewEdit=itemView.findViewById(R.id.editicon);
            imageViewDelete=itemView.findViewById(R.id.deleteicon);

            sw=itemView.findViewById(R.id.sw);

            mStorage=FirebaseStorage.getInstance();
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("DeliveryItem");

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent f=new Intent(mContext,FullScreenActivity.class);
                    DeliveryItem selecteditem=mdItems.get(getAdapterPosition());
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
                    DeliveryItem selecteditem=mdItems.get(getAdapterPosition());
                    final DialogPlus dialogPlus=DialogPlus.newDialog(mContext)
                            .setContentHolder(new ViewHolder(R.layout.dialogcontentfordelivery))
                            .setExpanded(true,1150)
                            .create();

                    View myview=dialogPlus.getHolderView();
                    EditText price=myview.findViewById(R.id.price);
                    EditText supp=myview.findViewById(R.id.usupplier);
                    EditText invoice=myview.findViewById(R.id.uinvoice);
                    EditText date=myview.findViewById(R.id.udatecreate);
                    EditText isPaid=myview.findViewById(R.id.uispaid);
                    Button update=myview.findViewById(R.id.usubmit);

                    price.setText(String.valueOf(selecteditem.getPrice()));
                    supp.setText(selecteditem.getNameOfSupplier());
                    invoice.setText(selecteditem.getInvoiceNumber());
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

                    isPaid.setText(selecteditem.getIsPaid());

                    dialogPlus.show();
                    update.setOnClickListener(v -> {

                        double priceTemp;
                        if (price.getText().toString().length() == 0) {
                            priceTemp = 0;
                        } else {
                            priceTemp = Double.parseDouble(price.getText().toString());
                        }

                        //update in search Mode
                        for (int i=0; i<mdItems.size();i++){
                            if (selecteditem.getKey()==mdItems.get(i).getKey()){
                                mdItems.get(i).setDateOfCreate(date.getText().toString());
                                mdItems.get(i).setPrice(priceTemp);
                                mdItems.get(i).setIsPaid(isPaid.getText().toString());
                                mdItems.get(i).setInvoiceNumber(invoice.getText().toString());
                                mdItems.get(i).setNameOfSupplier(supp.getText().toString());
                                notifyDataSetChanged();
                            }
                        }

                        Map<String,Object> map=new HashMap<>();
                        //map.put("price",Double.parseDouble(price.getText().toString()));
                        map.put("price",priceTemp);
                        map.put("nameOfSupplier",supp.getText().toString());
                        map.put("invoiceNumber",invoice.getText().toString());
                        map.put("dateOfCreate",date.getText().toString());
                        map.put("isPaid",isPaid.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("DeliveryItem")
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
                            DeliveryItem selectedItem=mdItems.get(getAdapterPosition());
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
                            mdItems.remove(getAdapterPosition());
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

            //Update only IsPaid in DB when switch is clicked

            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    DeliveryItem selecteditem=mdItems.get(getAdapterPosition());
                    Map<String,Object> map=new HashMap<>();
                    if (isChecked){
                        map.put("isPaid","שולם");
                        FirebaseDatabase.getInstance().getReference().child("DeliveryItem")
                                .child(selecteditem.getKey()).updateChildren(map);
                    }
                    else{
                        map.put("isPaid","לא שולם");
                        FirebaseDatabase.getInstance().getReference().child("DeliveryItem")
                                .child(selecteditem.getKey()).updateChildren(map);
                    }
                }
            });
        }
    }
}
