package com.ddvader44.whatsappstatusdownloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    StatusAdapter statusAdapter;
    File[] files;
    ArrayList<Object> filesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipe);

        Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            setupRefreshLayout();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                setupRefreshLayout();
                (
                        new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "Refreshed!", Toast.LENGTH_SHORT).show();
                    }
                },2000);

            }
        });
    }

    private void setupRefreshLayout() {
        filesList.clear();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        statusAdapter = new StatusAdapter(MainActivity.this,getData());
        recyclerView.setAdapter(statusAdapter);
        statusAdapter.notifyDataSetChanged();
    }

    private ArrayList<Object> getData() {
        StatusModel f;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME + "Media/.Statuses";
        File targetDirectory = new File(targetPath);
        files = targetDirectory.listFiles();

       
        if ((files != null ? files.length : 0) > 0) {
            for(int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new StatusModel();
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
                f.setFileName(file.getName());

                if (!f.getUri().toString().endsWith(".nomedia")) {
                    filesList.add(f);
                }
            }
        }
        return filesList;
        }

}