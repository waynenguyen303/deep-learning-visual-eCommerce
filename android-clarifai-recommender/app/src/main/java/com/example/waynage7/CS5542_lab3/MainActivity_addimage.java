package com.example.waynage7.CS5542_lab3;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Color;
import clarifai2.dto.prediction.Concept;
import clarifai2.dto.prediction.Logo;


public class MainActivity_addimage extends AppCompatActivity {

    ImageView chosenImage;
    Integer REQUEST_CAMERA=1, SELECT_FILE = 0;
    String ClarifaiID = "qHUs9cdjSiXwiIhoQ02ZwO3QFZet2uNPrxEGjinH";
    String ClarifaiSecret = "vzsuX7ldq5nCyv8kyc9Oh0dq56ugUuczaE-ITuao";

    String Feature = "No Image Selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_addimage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chosenImage =(ImageView) findViewById(R.id.chosenImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                SelectImage();
            }
        });
    }

    // camera icon button selector
    private void SelectImage(){

        final CharSequence[] items={"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_addimage.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // if user chooses the camera option
                if(items[which].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);

                // if user chooses the gallery option
                }else if(items[which].equals("Gallery")){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);

                }else if(items[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //Save camera capture.... needs to unmount SD card or restart emulator to see pictures in gallery
    public void saveToFile(Bitmap image) {

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        FileOutputStream fOut = null;

        try{

            // get timestamp for unique camera photo
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            directory.mkdirs();
            if(!directory.isDirectory())
                directory.mkdirs();
            File file = new File(directory, "Pic"+ts+".jpg");
            fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch(IOException e){System.out.println("Failed");e.printStackTrace();}
        finally{
            try {
                if (fOut != null) {
                    fOut.close();
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            System.out.println("result ok");
            // start clarifai if Camera is chosen
            if (requestCode == REQUEST_CAMERA) {

                Bundle bundle = data.getExtras();
                Bitmap bit = (Bitmap) bundle.get("data");

                //set camera picture to view
                chosenImage.setImageBitmap(bit);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] bitArray = stream.toByteArray();

                saveToFile(bit);

                try {
                    startClarifai(bitArray,ClarifaiID,ClarifaiSecret);
                }
                catch (Exception e) {e.printStackTrace();}

            // start clarifai if Gallery is chosen
            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                Bitmap bit1 = null;

                try {
                    bit1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                }
                catch (IOException e) {e.printStackTrace();}

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit1.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] bitArray = stream.toByteArray();

                // set gallery to view picture
                chosenImage.setImageURI(selectedImageUri);

                try {
                    startClarifai(bitArray,ClarifaiID,ClarifaiSecret);
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    // Clarifai API init
    public void startClarifai(byte[] byteArray, String clarifaiID,String clarifaiSecret){

        System.out.println("Clarify start");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ClarifaiClient client = new ClarifaiBuilder(clarifaiID,clarifaiSecret)
                .buildSync();
        client.getToken();

        ClarifaiResponse response = client.getDefaultModels().logoModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                .executeSync();
        List<ClarifaiOutput<Logo>> pred = (List<ClarifaiOutput<Logo>>) response.get();

        ClarifaiResponse response1 = client.getDefaultModels().apparelModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                .executeSync();
        List<ClarifaiOutput<Concept>> pred1 = (List<ClarifaiOutput<Concept>>) response1.get();

        ClarifaiResponse response2 = client.getDefaultModels().colorModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                .executeSync();
        List<ClarifaiOutput<Color>> pred2 = (List<ClarifaiOutput<Color>>) response2.get();

        System.out.println(pred);
        if (pred.isEmpty()){System.out.println("NO Logo Predictions");}
        if (pred1.isEmpty()){System.out.println("NO Apparel Predictions");}
        if (pred2.isEmpty()){System.out.println("NO Color Predictions");}

        List<Logo> feature = pred.get(0).data();
        List<Concept> feature1 = pred1.get(0).data();
        List<Color> feature2 = pred2.get(0).data();

        //System.out.println(feature.get(0).concepts().get(0).name());
        //System.out.println(feature1.get(0).name());
        //zzzzSystem.out.println(feature2.get(0).webSafeColorName());

        String feat = "Apparel Type:\n";
        String prob = "Accuracy:\n";

        String feat1 = "Logo:\n";
        String prob1 = "Accuracy:\n";

        String feat2 = "Color:\n";
        String prob2 = "Accuracy:\n";

        if (feature1.size() > 0){
            feat = feat + feature1.get(0).name()+"\n";
            prob = prob + feature1.get(0).value()+"\n";
            //System.out.println(feature.get(0).concepts().get(i).name() + " - " + feature.get(0).concepts().get(i).value());
        }

        if(feature.size() > 0){
            System.out.println(feature.size());
            feat1 = feat1 + feature.get(0).concepts().get(0).name()+"\n";
            prob1 = prob1 + feature.get(0).concepts().get(0).value()+"\n";
            //System.out.println(feature.get(0).concepts().get(i).name() + " - " + feature.get(0).concepts().get(i).value());
        }

        if (feature2.size() > 0 & feature2.get(0).webSafeColorName().equals("White") & feature2.get(1).value() > 0.2){
            feat2 = feat2 + feature2.get(1).webSafeColorName()+"\n";
            prob2 = prob2 + feature2.get(1).value()+"\n";
            //System.out.println(feature.get(0).concepts().get(i).name() + " - " + feature.get(0).concepts().get(i).value());
        }
        else {
            feat2 = feat2 +feature2.get(0).webSafeColorName()+"\n";
            prob2 = prob2 + feature2.get(0).value()+"\n";
        }

        //check if main color is truely white or just the white background
        String mainColor = feature2.get(0).webSafeColorName();
        if (feature2.get(0).webSafeColorName().equals("White") & feature2.get(1).value() > 0.2)
        {
            feat2 = feat2 + feature2.get(1).webSafeColorName()+"\n";
            prob2 = prob2 + feature2.get(1).value()+"\n";
            mainColor = feature2.get(1).webSafeColorName();
        }

        // print to Textviews
        TextView featureText = (TextView) findViewById(R.id.feature);
        TextView accuracyText = (TextView) findViewById(R.id.probability);
        TextView featureText1 = (TextView) findViewById(R.id.feature1);
        TextView accuracyText1 = (TextView) findViewById(R.id.probability1);
        TextView featureText2 = (TextView) findViewById(R.id.feature2);
        TextView accuracyText2 = (TextView) findViewById(R.id.probability2);

        featureText.setText(feat1);
        accuracyText.setText(prob1);
        featureText1.setText(feat);
        accuracyText1.setText(prob);
        featureText2.setText(feat2);
        accuracyText2.setText(prob2);



        Feature = mainColor + " " + feature.get(0).concepts().get(0).name()+ " " + feature1.get(0).name();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_addimage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this,product_search.class);
            System.out.println("hello world for you");
            System.out.println(Feature);
            intent.putExtra("ML_RESULTS",Feature);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
