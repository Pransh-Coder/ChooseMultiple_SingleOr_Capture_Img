package com.example.choosemultiple_singleor_capture_img;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button chooseBtn;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    Uri imageUri;
    ArrayList<Uri> uriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        chooseBtn = findViewById(R.id.chooseBtn);
        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        recyclerView.setLayoutManager(layoutManager);

        adapter=new ImageRecyclerAdapter(this,uriList);
        recyclerView.setAdapter(adapter);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery","Choose Multiple Photos","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(Html.fromHtml("<font color='#be0002'>Choose your profile picture</font>"));//"Choose your profile picture"

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                    imageView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                    imageView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }else if (options[item].equals("Choose Multiple Photos")){

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    Intent i = Intent.createChooser(intent, "File");
                    startActivityForResult(i, 2);
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            // Get uri
                            imageUri = data.getData();

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                            // Set image
                            imageView.setImageBitmap(scaled);

                        }catch (Exception e) {
                            Toast.makeText(this, "Oops! Sorry", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                case 2:
                        if (resultCode == Activity.RESULT_OK) {
                            if (data.getClipData() != null) {               //getClipData()- contains an optional list of content URIs if there is more than one item to preview.
                                int count = data.getClipData().getItemCount();
                                Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < count; i++) {
                                    //filling our uriList
                                    uriList.add(data.getClipData().getItemAt(i).getUri());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else if (data.getData() != null) {
                            String imagePath = data.getData().getPath();
                            Toast.makeText(this, ""+imagePath, Toast.LENGTH_SHORT).show();
                        }
                    else {
                        Toast.makeText(this, "You didn't selected an image!", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }
}