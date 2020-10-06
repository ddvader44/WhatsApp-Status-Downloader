package com.ddvader44.whatsappstatusdownloader;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder>{

    private Context context;
    private ArrayList<Object> filesList;

    public StatusAdapter(Context context, ArrayList<Object> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_row,null,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, final int position) {

    final StatusModel files = (StatusModel) filesList.get(position);
    if(files.getUri().toString().endsWith(".mp4")){
        holder.playIcon.setVisibility(View.VISIBLE);
    }else{
        holder.playIcon.setVisibility(View.INVISIBLE);
    }

        Glide.with(context)
                .load(files.getUri())
                .into(holder.saveImage);

    holder.downloadID.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkFolder();
            final String path = ((StatusModel) filesList.get(position)).getPath();
            final File file = new File(path);
            String destPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;
            File destFile = new File(destPath);

            try {
                FileUtils.copyFileToDirectory(file,destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(
                    context,
                    new String[]{destPath + files.getFileName()},
                    new String[]{"*/*"},
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() { }

                        @Override
                        public void onScanCompleted(String path, Uri uri) { }
                    }
            );
            Toast.makeText(context, "Saved to Gallery!",Toast.LENGTH_SHORT).show();
        }
    });


    }

    private void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +  Constants.SAVE_FOLDER_NAME;
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if(!isDirectoryCreated){
            isDirectoryCreated = dir.mkdir();
        }
        if(isDirectoryCreated){
            Log.d("Folder","Directory already exists!");
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{

        ImageView saveImage,playIcon,downloadID;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            saveImage = itemView.findViewById(R.id.mainImageView);
            playIcon = itemView.findViewById(R.id.playButton);
            downloadID = itemView.findViewById(R.id.downloadID);
        }
    }

}
