package com.ambitious.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends DialogFragment{
    public static final String EXTRA_IMAGE_PATH =
            "com.ambitious.criminalintent.image_path";
    public static final String EXTRA_IMAGE_ORIENTATION =
            "com.ambitious.criminalintent.orientation";

    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath, int orientation) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putInt(EXTRA_IMAGE_ORIENTATION, orientation);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mImageView = new ImageView(getActivity());
        String Path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int orientation = getArguments().getInt(EXTRA_IMAGE_ORIENTATION);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), Path, orientation);
        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
