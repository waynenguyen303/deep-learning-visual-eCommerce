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

import org.json.JSONObject;

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
import clarifai2.dto.prediction.Concept;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity_addimage extends AppCompatActivity {

    ImageView chosenImage;
    Integer REQUEST_CAMERA=1, SELECT_FILE = 0;
    String ClarifaiID = "qHUs9cdjSiXwiIhoQ02ZwO3QFZet2uNPrxEGjinH";
    String ClarifaiSecret = "vzsuX7ldq5nCyv8kyc9Oh0dq56ugUuczaE-ITuao";

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


                            onClickSpark(chosenImage);


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


                            onClickSpark(chosenImage);

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
        ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                .executeSync();
        List<ClarifaiOutput<Concept>> pred = (List<ClarifaiOutput<Concept>>) response.get();

        if (pred.isEmpty()){System.out.println("NO Predictions");}
        List<Concept> feature = pred.get(0).data();

        String feat = "Features:\n";
        String prob = "Accuracy:\n";

        for (int i =0;i< feature.size(); i++){
            feat = feat + feature.get(i).name()+"\n";
            prob = prob + feature.get(i).value()+"\n";
            System.out.println(feature.get(i).name() + " - " + feature.get(i).value());
        }

        // print to Textviews
        TextView featureText = (TextView) findViewById(R.id.feature);
        TextView accuracyText = (TextView) findViewById(R.id.probability);
        featureText.setText(feat);
        accuracyText.setText(prob);
    }



    // For Spark API
    public void onClickSpark(ImageView image) {

        String url = "http://10.0.2.2:8080/get_custom";

//192.168.1.103
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) image.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

        byte[] bytes1;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        try {
            while ((bytesRead = bis.read(buffer)) != -1) {
                bytearrayoutputstream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes1 = bytearrayoutputstream.toByteArray();

        String encodedString = Base64.encodeToString(bytes1, Base64.DEFAULT);




        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
        try {

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), encodedString);

            System.out.println(encodedString);

            Request request = new Request.Builder().url(url).post(requestBody).header("Accept", "text/plain").build();
            System.out.println("begin cllient---------------------------");

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage()+" ererefefqwfsd");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    System.out.println(response.code());
                    Log.d("okHttp", result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(result);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



















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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
