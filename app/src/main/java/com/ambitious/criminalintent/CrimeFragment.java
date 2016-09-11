package com.ambitious.criminalintent;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {
    private EditText mTitleField;
    private Button mDateButton;
    private ImageButton mImageButton;
    private ImageView mPhotoView;
    private CheckBox mSolvedCheckBox;
    private Crime mCrime;
    public static final String EXTRA_CRIME_ID =
            "com.ambitious.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final String TAG = "CrimeFragment";

    private boolean mIsDeleteCrime;

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void updateDate() {
        Locale locate = new Locale("en", "US");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locate);
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void showPhoto() {
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;

        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path, p.getOrientation());
        }
        mPhotoView.setImageDrawable(b);
    }

    private void deletePhoto(String fileName) {
        PictureUtils.cleanImageView(mPhotoView);
        mCrime.setPhoto(null);

        String path = getActivity().getFileStreamPath(fileName)
                .getAbsolutePath();
        File removeFile = new File(path);
        if (removeFile.exists()) {
            removeFile.delete();
            Log.d(TAG, "delete photo in path " + path);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UUID crimeId = (UUID) getActivity().getIntent()
//                .getSerializableExtra(EXTRA_CRIME_ID);

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

//        setHasOptionsMenu(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getActivity().startActionMode(mCallback);
                v.setSelected(true);
                mIsDeleteCrime = true;
                return true;
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            if (NavUtils.getParentActivityName(getActivity()) != null) {
//                if (getActivity().getActionBar() == null) {
//                    Log.d(TAG, "ActionBar is null");
//                } else {
//                    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//                }
//            }
//        }

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mImageButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;

        if (!hasACamera) {
            mImageButton.setEnabled(false);
        }

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path, p.getOrientation()).show(fm, DIALOG_IMAGE);
            }
        });
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPhotoView.startActionMode(mCallback);
                mPhotoView.setSelected(true);
                return true;
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

       if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
       } else if (requestCode == REQUEST_PHOTO) {
           String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
           if (filename != null) {
               int orientation = getResources().getConfiguration().orientation;
               Log.d(TAG, "the device orientation is " + orientation);

               Photo p = mCrime.getPhoto();
               if (p != null) {
                   deletePhoto(p.getFilename());
               }
               p = new Photo(filename, orientation);
               mCrime.setPhoto(p);
               showPhoto();
           }
       }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                if (NavUtils.getParentActivityName(getActivity()) != null) {
//                    NavUtils.navigateUpFromSameTask(getActivity());
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    private ActionMode.Callback mCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.crime_list_item_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete_crime:
                    if (mIsDeleteCrime) {
                        CrimeLab.get(getActivity()).deleteCrime(mCrime);
                        mode.finish();
                        getActivity().finish();
                    } else {
                        Photo p = mCrime.getPhoto();
                        if (p != null) {
                            deletePhoto(p.getFilename());
                        }
                    }

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };


}
