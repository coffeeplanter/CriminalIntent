package ru.coffeeplanter.criminalintent;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

public class PhotoViewFragment extends DialogFragment {

    private static final String ARG_PHOTO = "photo";

    private ImageView mPhotoView;

    public static PhotoViewFragment newInstance(File photoFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, photoFile);
        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photoview, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.dialog_photoview);
        final Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
        mPhotoView.setImageBitmap(bitmap);
        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Устанавливаем размер диалогового окна, соответствующий изображению
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                int shift;
                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    shift = 5;
                } else {
                    shift = 100;
                }
                getDialog().getWindow().setLayout(size.x - shift, (size.x - shift) * bitmap.getHeight() / bitmap.getWidth() + 22);
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

}
