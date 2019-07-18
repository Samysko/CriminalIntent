package com.bignerdranch.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ImageFragmnet extends DialogFragment {
    public static final String ARG_FILEPATH = "filepath";

    private ImageView mPhotoView;

    private String mPhotoPath;

    public static ImageFragmnet newInstance(String path){
        Bundle bundle = new Bundle();
        bundle.putString(ARG_FILEPATH, path);

        ImageFragmnet imageFragmnet = new ImageFragmnet();
        imageFragmnet.setArguments(bundle);

        return imageFragmnet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mPhotoPath = getArguments().getString(ARG_FILEPATH);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoPath, getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        mPhotoView = view.findViewById(R.id.crime_zoom_image);
        mPhotoView.setImageBitmap(bitmap);


        return new AlertDialog.Builder(getActivity())
                .setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();

    }
}
