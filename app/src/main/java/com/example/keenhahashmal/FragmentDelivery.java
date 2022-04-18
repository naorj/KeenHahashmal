package com.example.keenhahashmal;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentDelivery extends Fragment {

    View v;

    ProgressBar progressBar;
    TextView textViewProgress;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    String isPaid;
    RadioButton radioButtonYes;
    RadioButton radioButtonNo;

    EditText dateTXT;
    ImageView selectedImage,cal;
    private int mDate, mMonth, mYear;
    Button gallerybtn ,camerabtn, add;
    String currentPhotoPath, linkToImage;
    StorageReference storageReference;
    AutoCompleteTextView auto;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());
    Date date;

    public FragmentDelivery() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.delivery_fragment,container,false);
        dateTXT=v.findViewById(R.id.dateFormat);
        auto=v.findViewById(R.id.autoCompleteTextView);
        radioButtonNo=v.findViewById(R.id.radioButtonNo);
        radioButtonYes=v.findViewById(R.id.radioButtonYes);
        ArrayAdapter myad=new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.suppliers));
        auto.setAdapter(myad);
        auto.setThreshold(1);
        cal=v.findViewById(R.id.datepicker);
        dateTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar Cal=Calendar.getInstance();
                mDate=Cal.get(Calendar.DAY_OF_MONTH);
                mMonth=Cal.get(Calendar.MONTH);
                mYear=Cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Cal.set(year, month, dayOfMonth);
                        dateTXT.setText(simpleDateFormat.format(Cal.getTime()));
                        date= Cal.getTime();
                    }
                },mYear,mMonth,mDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        progressBar=v.findViewById(R.id.progressBar);
        textViewProgress=v.findViewById(R.id.textProgess);
        progressBar.setVisibility(View.GONE);
        textViewProgress.setVisibility(View.GONE);

        selectedImage=v.findViewById(R.id.imageView);
        camerabtn=v.findViewById(R.id.camerabtn);
        gallerybtn=v.findViewById(R.id.gallerybtn);
        storageReference= FirebaseStorage.getInstance().getReference();

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        // Getting Date And Time
        Calendar calendar= Calendar.getInstance();
        String currentTime= DateFormat.getTimeInstance().format(calendar.getTime());
        String currentDate= DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        TextView textViewDate = v.findViewById(R.id.textViewDate);
        String dateAndTime=currentDate+", "+currentTime;
        textViewDate.setText(dateAndTime);

        // adding new item to DB
        final EditText invoiceNumber=v.findViewById(R.id.editTextPlace);
        final EditText sum=v.findViewById(R.id.editTextSum);
        add = v.findViewById(R.id.dbAdd);
        //Button add = v.findViewById(R.id.dbAdd);
        DAODeliveryItem daoDeliveryItem = new DAODeliveryItem();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateSuccess()){
                    String invoicenum = invoiceNumber.getText().toString();
                    double price;
                    if (sum.getText().toString().length() == 0) {
                        price = 0;
                    } else {
                        price = Double.parseDouble(sum.getText().toString());
                    }

                    if (radioButtonNo.isChecked())
                        isPaid="לא שולם";
                    else
                        isPaid="שולם";

                    DeliveryItem deliveryItem = new DeliveryItem(currentDate, currentTime,
                            price, invoicenum, linkToImage,
                            isPaid, dateTXT.getText().toString(), auto.getText().toString());
                    daoDeliveryItem.add(deliveryItem).addOnSuccessListener(suc ->
                    {
                        Toast.makeText(getContext(), "פריט הוכנס", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }).addOnFailureListener(er -> {
                        Toast.makeText(getContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        return v;
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Camera Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(),contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                selectedImage.setImageURI(contentUri);
                uploadImageToFirebase(imageFileName,contentUri);
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {

        progressBar.setVisibility(View.VISIBLE);
        textViewProgress.setVisibility(View.VISIBLE);

        final StorageReference image = storageReference.child("DeliveryImages/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        linkToImage=uri.toString();
                    }
                });
                Toast.makeText(getContext(), "תמונה הועלתה בהצלחה", Toast.LENGTH_SHORT).show();
                progressBar.setProgress(0);
                textViewProgress.setText("העלאה הושלמה - 100%");
                add.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "העלאה נכשלה", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                add.setEnabled(false);
                double percentage=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressBar.setProgress((int)percentage);
                textViewProgress.setText(percentage+" %");
            }
        });
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        //  Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.keenhahashmal.provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent,CAMERA_REQUEST_CODE);
        }
    }

    private Boolean validateSuccess(){
        if (!radioButtonYes.isChecked() && !radioButtonNo.isChecked()){
            Toast.makeText(getContext(), "נא לציין האם שולם או לא",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}