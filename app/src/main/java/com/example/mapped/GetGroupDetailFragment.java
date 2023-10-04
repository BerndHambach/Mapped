package com.example.mapped;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.mapped.databinding.FragmentGetGroupDetailBinding;

import java.io.File;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GetGroupDetailFragment extends Fragment {

    private FragmentGetGroupDetailBinding binding;
    private Permissions appPermission;
    private Uri imageUri;

    public static final int GALLERY_REQUEST_CODE = 105;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGetGroupDetailBinding.inflate(inflater, container, false);

       // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Group Detail");
        appPermission = new Permissions();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = binding.edtGroupName.getText().toString().trim();
                if(groupName.isEmpty()){
                    binding.edtGroupName.setError("Filed is required");
                    binding.edtGroupName.requestFocus();
                } else {
                    if(imageUri == null) {
                        Toast.makeText(requireContext(), "Select Group Image", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("GroupName", groupName);
                        bundle.putString("GroupImage", imageUri.toString());

                        GroupMemberFragment memberFragment = new GroupMemberFragment();
                        memberFragment.setArguments(bundle);

                        getFragmentManager().beginTransaction().replace(R.id.groupContainer, memberFragment)
                                .commit();

                    }
                }
            }
        });

        binding.imgPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName ="JPEG_" + timeStamp + "."+getFileExtension(contentUri);
                binding.imgGroup.setImageURI(contentUri);
            }
        }
    }
    private String getFileExtension(Uri contentUri) {

        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
}