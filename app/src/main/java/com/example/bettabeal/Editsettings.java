package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.BaseResponse;
import com.example.bettabeal.model.CustomerResponse;
import com.example.bettabeal.model.Customer;
import com.example.bettabeal.model.UpdateBiodataRequest;

import android.app.AlertDialog;
import android.os.Environment;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;

import com.example.bettabeal.utils.SessionManager;

public class Editsettings extends Fragment {

    private EditText usernameEditText, dateOfBirthEditText, phoneNumberEditText, emailEditText;
    private SessionManager sessionManager;
    private ImageView ivProfileImage;
    private Button btnAddProfilePhoto;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Uri photoURI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editsettings, container, false);

        initializeViews(view);
        setupListeners(view);

        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());

        // Load current user data
        loadCurrentUserData();

        return view;
    }

    private void initializeViews(View view) {
        usernameEditText = view.findViewById(R.id.usernameEditText);
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnAddProfilePhoto = view.findViewById(R.id.btnAddProfilePhoto);
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.save).setOnClickListener(v -> updateBiodata());
        view.findViewById(R.id.backsettings).setOnClickListener(v -> goBackToSettings());
        ivProfileImage.setOnClickListener(v -> openImagePicker());
        btnAddProfilePhoto.setOnClickListener(v -> openImagePicker());
    }

    private void loadCurrentUserData() {
        String token = sessionManager.getToken();
        int userId = sessionManager.getUserId();

        if (token.isEmpty() || userId == -1) {
            showToast("Error: Token or User ID is missing");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading user data...");
        progressDialog.show();

        RetrofitClient.getInstance()
            .getApiService()
            .getCustomer(userId, "Bearer " + token)
            .enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        CustomerResponse customerResponse = response.body();
                        if ("000".equals(customerResponse.getCode())) {
                            updateUIWithCustomerData(customerResponse.getCustomer());
                        } else {
                            showToast(customerResponse.getMessage());
                        }
                    } else {
                        handleErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    handleNetworkError(t);
                }
            });
    }

    private void updateUIWithCustomerData(Customer customer) {
        usernameEditText.setText(customer.getFull_name());
        dateOfBirthEditText.setText(customer.getBirth_date());
        phoneNumberEditText.setText(customer.getPhone_number());
        emailEditText.setText(customer.getEmail());

        String profileImage = customer.getProfile_image();
        if (profileImage != null && !profileImage.isEmpty()) {
            String fullUrl = "https://api-bettabeal.dgeo.id" + profileImage;
            Glide.with(requireContext())
                .load(fullUrl)
                .into(ivProfileImage);
            ivProfileImage.setVisibility(View.VISIBLE);
            btnAddProfilePhoto.setVisibility(View.GONE);
        }
    }

    private void updateBiodata() {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Error: Token is missing");
            return;
        }

        UpdateBiodataRequest request = new UpdateBiodataRequest(
            usernameEditText.getText().toString().trim(),
            dateOfBirthEditText.getText().toString().trim(),
            phoneNumberEditText.getText().toString().trim(),
            emailEditText.getText().toString().trim()
        );

        RetrofitClient.getInstance()
            .getApiService()
            .updateBiodata("Bearer " + token, request)
            .enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call,
                                     Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BaseResponse baseResponse = response.body();
                        if ("000".equals(baseResponse.getCode())) {
                            showToast("Biodata updated successfully");
                            goBackToSettings();
                        } else {
                            showToast(baseResponse.getMessage());
                        }
                    } else {
                        showToast("Failed to update biodata");
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    showToast("Error: Unable to connect");
                }
            });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void goBackToSettings() {
        // Go back to the SettingsFragment
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide Bottom Navigation when on EditSettingsFragment
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Show Bottom Navigation when exiting EditSettingsFragment
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateProfileImage(Uri imageUri) {
        String token = sessionManager.getToken();
        if (token.isEmpty()) {
            showToast("Error: Token is missing");
            return;
        }

        try {
            // Compress image first
            File compressedFile = compressImage(imageUri);
            if (compressedFile == null) {
                showToast("Error: Could not process image");
                return;
            }

            // Get MIME type
            String mimeType = getActivity().getContentResolver().getType(imageUri);
            if (mimeType == null) {
                mimeType = "image/jpeg"; // default mime type
            }

            // Create RequestBody instance from file
            RequestBody requestFile = RequestBody.create(
                MediaType.parse(mimeType),
                compressedFile
            );

            // Log file details for debugging
            Log.d("UpdateImage", "File size: " + compressedFile.length());
            Log.d("UpdateImage", "File path: " + compressedFile.getAbsolutePath());
            Log.d("UpdateImage", "MIME type: " + mimeType);

            // MultipartBody.Part is used to send also the actual filename
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                "profile_image",
                compressedFile.getName(),
                requestFile
            );

            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            RetrofitClient.getInstance()
                .getApiService()
                .updateProfileImage("Bearer " + token, body)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse baseResponse = response.body();
                            if ("000".equals(baseResponse.getCode())) {
                                showToast("Profile image updated successfully");
                                Glide.with(requireContext())
                                    .load(imageUri)
                                    .into(ivProfileImage);
                                ivProfileImage.setVisibility(View.VISIBLE);
                                btnAddProfilePhoto.setVisibility(View.GONE);
                            } else {
                                showToast(baseResponse.getMessage());
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("UpdateImage", "Error Response: " + errorBody);
                                showToast("Failed to update image: " + errorBody);
                            } catch (Exception e) {
                                Log.e("UpdateImage", "Error: " + e.getMessage());
                                showToast("Failed to update image");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("UpdateImage", "Network Error: " + t.getMessage());
                        showToast("Error: " + t.getMessage());
                    }
                });

        } catch (Exception e) {
            Log.e("UpdateImage", "Exception: " + e.getMessage());
            showToast("Error: " + e.getMessage());
        }
    }

    // Add this new method for image compression
    private File compressImage(Uri imageUri) {
        try {
            // Get input stream from Uri
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);

            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Get new input stream since the first one was consumed
            inputStream = getActivity().getContentResolver().openInputStream(imageUri);

            // Decode bitmap with actual dimensions
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Fix orientation
            ExifInterface exif = new ExifInterface(getActivity().getContentResolver().openInputStream(imageUri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    // No rotation needed
                    break;
            }

            // Apply rotation if needed
            if (orientation != ExifInterface.ORIENTATION_UNDEFINED) {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            // Calculate new dimensions while maintaining aspect ratio
            int maxDimension = 1024; // Max width or height
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            float scale = Math.min((float) maxDimension / originalWidth, (float) maxDimension / originalHeight);

            int newWidth = Math.round(originalWidth * scale);
            int newHeight = Math.round(originalHeight * scale);

            // Create scaled bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Create file for compressed image
            File compressedFile = new File(getActivity().getCacheDir(), "compressed_" + System.currentTimeMillis() + ".jpg");

            // Write compressed bitmap to file
            FileOutputStream fos = new FileOutputStream(compressedFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();

            // Recycle bitmaps to free memory
            bitmap.recycle();
            resizedBitmap.recycle();

            return compressedFile;
        } catch (Exception e) {
            Log.e("CompressImage", "Error: " + e.getMessage());
            return null;
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            String fileName = getFileName(uri);
            File file = new File(getActivity().getCacheDir(), fileName);
            InputStream input = getActivity().getContentResolver().openInputStream(uri);
            OutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024]; // 4kb buffer
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
            output.close();
            input.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void openImagePicker() {
        // Check camera permission first
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                dispatchTakePictureIntent();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        try {
            // Cek apakah ada aplikasi kamera
            PackageManager packageManager = getActivity().getPackageManager();
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Toast.makeText(getActivity(), "Device doesn't have camera", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Buat file untuk menyimpan foto
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);

            // Dapatkan URI menggunakan FileProvider
            photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.bettabeal.fileprovider",
                    photoFile);

            // Tambahkan URI ke intent
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            // Tambahkan grant URI permission
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // Start activity
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);

        } catch (Exception e) {
            Log.e("Camera", "Error: " + e.getMessage());
            Toast.makeText(getActivity(), "Error opening camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show dialog again
                openImagePicker();
            } else {
                Toast.makeText(getActivity(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                // Result from Gallery
                imageUri = data.getData();
            } else if (requestCode == CAMERA_REQUEST) {
                // Result from Camera
                imageUri = photoURI;
            }

            if (imageUri != null) {
                // Show the selected/captured image
                Glide.with(this)
                    .load(imageUri)
                    .into(ivProfileImage);
                ivProfileImage.setVisibility(View.VISIBLE);
                btnAddProfilePhoto.setVisibility(View.GONE);

                // Upload the image
                updateProfileImage(imageUri);
            }
        }
    }

    private void checkCameraPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.CAMERA)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 100);
            }
        }
    }

    private void handleErrorResponse(Response<CustomerResponse> response) {
        try {
            String errorBody = response.errorBody() != null ?
                response.errorBody().string() : "Unknown error";
            Log.e("LoadData", "Error Response: " + errorBody);
            showToast("Failed to load user data");
        } catch (Exception e) {
            Log.e("LoadData", "Error: " + e.getMessage());
            showToast("Failed to load user data");
        }
    }

    private void handleNetworkError(Throwable t) {
        Log.e("LoadData", "Network Error: " + t.getMessage());
        showToast("Error: Unable to connect");
    }
}
