package com.example.pathfromuri;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.pathfromuri.Model.CommonModel;
import com.example.pathfromuri.Webservice.Api;
import com.example.pathfromuri.Webservice.CommonFunction;
import com.example.pathfromuri.Webservice.Constants;
import com.example.pathfromuri.Webservice.RetrofitClient;
import com.example.pathfromuri.Webservice.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int PROFILE_CAMERA = 1;
    public static final int PROFILE_GALLERY = 2;
    ProgressDialog progressDialog;

    ImageView image;
    Button open, upload;
    Dialog dialog;
    ImageView gallery, camera, close;

    String filePath = "", imageUrl;
    Bitmap profile_bitmap;
    TextView choose;
    Uri profile_uri;
    CommonFunction commonFunction;
    Uri photoURI;
    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonFunction = new CommonFunction(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");

        image = findViewById(R.id.image);
        open = findViewById(R.id.open);
        upload = findViewById(R.id.upload);

        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_sample, null);
        dialog = new Dialog(this);
        dialog.setContentView(modalbottomsheet);
        dialog.setTitle("Choose item");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        camera = dialog.findViewById(R.id.camera);
        choose = dialog.findViewById(R.id.choose);

        close = dialog.findViewById(R.id.close);
        gallery = dialog.findViewById(R.id.gallery);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(PROFILE_CAMERA, PROFILE_GALLERY);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload_priscription();
                ImageUpload();
            }
        });


    }

    private void ImageUpload() {
        progressDialog.show();


        Retrofit retrofit = commonFunction.createRetrofitObjectWithHeader(Constants.API_BASE_URL);
        Api Apiinterface = retrofit.create(Api.class);


        MultipartBody.Part image = null;


        Log.e("path", imageUrl);

        if (!imageUrl.equals("") && imageUrl != null) {
            File file = new File(imageUrl);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        Call<CommonModel> call = RetrofitClient.getInstance().getApi().Upload(image);
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(Call<CommonModel> call, retrofit2.Response<CommonModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {

                    if (response.body().isStatus()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onFailure(Call<CommonModel> call, Throwable t) {

            }
        });

      /*  Apiinterface.Upload(image).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.e("resp", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Successfull !!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Not Uploaded !!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error", t.getMessage());
            }
        });*/

    }

    private void checkPermission(final int profileCamera, final int profileGallery) {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            dialog.show();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, profileCamera);*/
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                        }
                        if (photoFile != null) {
                            photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    "com.example.pathfromuri.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, profileCamera);


                        }
                    }
                    dialog.dismiss();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent logoIntent = new Intent();
                    logoIntent.setType("image/*");
                    logoIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(logoIntent, "Select Picture"), profileGallery);
                    dialog.dismiss();
                }
            });


        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageUrl = image.getAbsolutePath();
        return image;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_GALLERY) {

            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                profile_uri = imageUri;
                imageUrl = RealPathUtil.getRealPath(getApplicationContext(), profile_uri);
                Log.e("path", imageUrl);
                try {
                    profile_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //filePath = getPath(profile_uri);
                if (filePath != null) {

                } else {
                    Toast.makeText(this, "No Image Selected !!", Toast.LENGTH_SHORT).show();
                }
                image.setImageBitmap(profile_bitmap);
            }
        }
        if (requestCode == PROFILE_CAMERA) {


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            profile_bitmap = BitmapFactory.decodeFile(imageUrl, options);

            /*Uri imageUri = getImageUri(getApplicationContext(), profile_bitmap);
            String path = RealPathUtil.getRealPath(getApplicationContext(), imageUri);*/
            // Log.e("path", path);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            savePhoto(profile_bitmap, imageFileName);
            Log.e("path", imageUrl);

            image.setImageBitmap(profile_bitmap);
        }


    }

    private void Upload_priscription() {
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "http://www.koratty.co.in/app_ctrl/addComplaintsPic",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.e("resp", new String(response.data));


                        try {
                            JSONObject ob = new JSONObject(new String(response.data));
                            if (ob.getBoolean("status")) {
                                Toast.makeText(MainActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MainActivity.this, "Something Went wrong !!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (profile_bitmap == null) {
                    params.put("image", "");
                }
                Log.e("params", String.valueOf(params));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (profile_bitmap != null) {
                    params.put("image", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(profile_bitmap)));
                }

                Log.e("data", String.valueOf(params));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }


    public void savePhoto(Bitmap imaginative, String filenameone) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, filenameone);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            imaginative.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageUrl = directory.getAbsolutePath() + "/" + filenameone;

    }

}