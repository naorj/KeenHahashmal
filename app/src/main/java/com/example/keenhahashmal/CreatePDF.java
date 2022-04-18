package com.example.keenhahashmal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class CreatePDF extends AppCompatActivity {
    public static String font="/assets/Fonts/arialuni.ttf";
    EditText input_minimal, input_maximal,email;
    Button btn_minimal, btn_maximal, create;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Context context;
    Calendar calendar = Calendar.getInstance();
    ArrayList<Item> list = new ArrayList<>();
    Date date_minimal;
    Date date_maximal;
    String input1, input2;
    CheckBox mEmailcheck, mExgroup, mExtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Environment.isExternalStorageManager())
        { }
        else
        {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(permissionIntent);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Spinner mySpinner=findViewById(R.id.spinnerexpensegroup);
        Spinner mySpinner2=findViewById(R.id.spinnerexpensetype);
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(CreatePDF.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.expenseGroup));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setEnabled(false);
        ArrayAdapter<String> myAdapter2=new ArrayAdapter<String>(CreatePDF.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.expenseType));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);
        mySpinner2.setEnabled(false);

        context = this;
        create = findViewById(R.id.create);
        input_minimal = findViewById(R.id.input_minimal);
        input_maximal = findViewById(R.id.input_maximal);
        btn_minimal = findViewById(R.id.btn_minimal);
        btn_maximal = findViewById(R.id.btn_maximal);
        mExgroup=findViewById(R.id.check_exgroup);
        mExtype=findViewById(R.id.check_extype);
        mEmailcheck=findViewById(R.id.check_email);
        email=findViewById(R.id.email);
        mEmailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailcheck.isChecked()){
                    email.setEnabled(true);
                }else{
                    email.setEnabled(false);
                }
            }
        });

        mExgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExgroup.isChecked()){
                    mySpinner.setEnabled(true);
                }else{
                    mySpinner.setEnabled(false);
                }
            }
        });

        mExtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExtype.isChecked()){
                    mySpinner2.setEnabled(true);
                }else{
                    mySpinner2.setEnabled(false);
                }
            }
        });

        btn_minimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding keyboard by press the minimal date btn
                try {
                    InputMethodManager imm = (InputMethodManager)CreatePDF.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(CreatePDF.this.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        input_minimal.setText(simpleDateFormat.format(calendar.getTime()));
                        date_minimal = calendar.getTime();

                        input1 = input_minimal.getText().toString();
                        input2 = input_maximal.getText().toString();
                        if (input1.isEmpty() || input2.isEmpty()) {
                            create.setEnabled(false);
                        } else {
                            create.setEnabled(true);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        btn_maximal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        input_maximal.setText(simpleDateFormat.format(calendar.getTime()));
                        date_maximal = calendar.getTime();

                        input1 = input_maximal.getText().toString();
                        input2 = input_minimal.getText().toString();

                        if (input1.isEmpty() || input2.isEmpty()){
                            create.setEnabled(false);
                        }else {
                            create.setEnabled(true);
                        }
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query=database.child("Item").orderByChild("dateOfCreate").startAt(input2).
                            endAt(input1);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Item user = item.getValue(Item.class);
                            if (!mExgroup.isChecked() && !mExtype.isChecked()){
                                list.add(user);
                            }
                            else if(mExgroup.isChecked() && mExtype.isChecked()){
                                if(user.getExpenseGroup().equals(mySpinner.getSelectedItem().toString()) &&
                                        user.getExpenseType().equals(mySpinner2.getSelectedItem().toString())){
                                        list.add(user);
                                }
                            }
                            else if (!mExgroup.isChecked() && mExtype.isChecked()){
                                if (user.getExpenseType().equals(mySpinner2.getSelectedItem().toString())){
                                    list.add(user);
                                }
                            }
                            else if (mExgroup.isChecked() && !mExtype.isChecked()){
                                if (user.getExpenseGroup().equals(mySpinner.getSelectedItem().toString())){
                                    list.add(user);
                                }
                            }
                        }
                        if(list.size()==0){
                            Toast.makeText(CreatePDF.this, "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                createPDF();
                            } catch (DocumentException | IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void createPDF() throws IOException, DocumentException {

        BaseFont unicode = BaseFont.createFont(font,BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        Font arialunifont=new Font(unicode,12);

        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path,input2+"to"+input1+".pdf");
        Document doc =new Document();
        PdfWriter.getInstance(doc,new FileOutputStream(file));
        doc.open();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Chunk date=new Chunk(currentDate);

        doc.add(date);
        doc.add(new Paragraph("\n\n"));
        Chunk title=new Chunk("Keen Hahashmal - Items Report: "+input2+" to "+input1);

        Phrase phrase = new Phrase();

        phrase.add(title);

        Paragraph paragraph=new Paragraph();
        paragraph.add(phrase);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        doc.add(paragraph);
        doc.add(new Paragraph("\n"));
        PdfPTable pdfPTable=new PdfPTable(7);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        PdfPCell pdfPCell=new PdfPCell(new Phrase("#מספר",arialunifont));
        PdfPCell pdfPCell2=new PdfPCell(new Phrase("קבוצת הוצאות",arialunifont));
        PdfPCell pdfPCell3=new PdfPCell(new Phrase("סוג הוצאה",arialunifont));
        PdfPCell pdfPCell4=new PdfPCell(new Phrase("מספר קבלה",arialunifont));
        PdfPCell pdfPCell5=new PdfPCell(new Phrase("שם ספק",arialunifont));
        PdfPCell pdfPCell6=new PdfPCell(new Phrase("תאריך הוצאה",arialunifont));
        PdfPCell pdfPCell7=new PdfPCell(new Phrase("מחיר",arialunifont));
        pdfPTable.addCell(pdfPCell);
        pdfPTable.addCell(pdfPCell2);
        pdfPTable.addCell(pdfPCell3);
        pdfPTable.addCell(pdfPCell4);
        pdfPTable.addCell(pdfPCell5);
        pdfPTable.addCell(pdfPCell6);
        pdfPTable.addCell(pdfPCell7);
        addItems(pdfPTable,arialunifont);
        doc.add(pdfPTable);
        doc.close();
        if (mEmailcheck.isChecked()){

            String[] mailto={email.getText().toString()};
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);

            ShareCompat.IntentBuilder intentBuilder=ShareCompat.IntentBuilder.from(this).addStream(uri);
            intentBuilder.addEmailTo(mailto);
            intentBuilder.setSubject("כעין החשמל - דוח הוצאות תקופתי");
            intentBuilder.setText("מצורף בזאת דוח הוצאות תקופתי לתאריכים: "+input2+"-"+input1);
            intentBuilder.getIntent().setType("text/plain");

            Intent chooser=intentBuilder.createChooserIntent();

            startActivity(chooser);

        }
        Toast.makeText(this,"נוצר PDF",Toast.LENGTH_SHORT).show();
    }

    private PdfPTable addItems(PdfPTable table, Font auf){
        double total=0;
        for (int i=0; i<list.size();i++){
            table.addCell((i+1)+"#");
            PdfPCell pdfPCell=new PdfPCell(new Phrase(list.get(i).getExpenseGroup(),auf));
            table.addCell(pdfPCell);
            PdfPCell pdfPCell1=new PdfPCell(new Phrase(list.get(i).getExpenseType(),auf));
            table.addCell(pdfPCell1);
            table.addCell(String.valueOf(list.get(i).getInvoiceNumber()));
            PdfPCell pdfPCell2=new PdfPCell(new Phrase(list.get(i).getNameOfSupplier(),auf));
            table.addCell(pdfPCell2);
            //table.addCell(list.get(i).getDateOfCreate());
            table.addCell(changeDateFormat(list.get(i).getDateOfCreate()));
            table.addCell(String.valueOf(list.get(i).getPrice()));
            total=total+list.get(i).getPrice();
        }

        PdfPCell pdfPCell=new PdfPCell(new Phrase("סך הכל",auf));
        pdfPCell.setColspan(6);
        table.addCell(pdfPCell);
        PdfPCell pdfPCell1=new PdfPCell(new Phrase("₪"+String.valueOf(total),auf));
        pdfPCell1.setColspan(1);
        table.addCell(pdfPCell1);
        return table;
    }

    public String changeDateFormat(String date){
        Date parsed = null;
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