package com.obiapp.activities_fragments.activity_add_ads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.obiapp.R;
import com.obiapp.activities_fragments.FragmentMapTouchListener;
import com.obiapp.activities_fragments.activite_swear.SwearActivity;
import com.obiapp.adapters.ImageAdsAdapter;
import com.obiapp.adapters.SpinnerDepartmentAdapter;
import com.obiapp.adapters.SpinnerGovernorateAdapter;
import com.obiapp.databinding.ActivityAddAdsBinding;
import com.obiapp.databinding.ItemAddAdsBinding;
import com.obiapp.language.Language;
import com.obiapp.models.AddAdsModel;
import com.obiapp.models.DepartmentDataModel;
import com.obiapp.models.DepartmentModel;
import com.obiapp.models.GovernorateDataModel;
import com.obiapp.models.GovernorateModel;
import com.obiapp.models.ItemAddAds;
import com.obiapp.models.ItemAddAdsDataModel;
import com.obiapp.models.PlaceGeocodeData;
import com.obiapp.models.PlaceMapDetailsData;
import com.obiapp.models.SpinnerTypeAdapter;
import com.obiapp.models.StatusResponse;
import com.obiapp.models.TypeDataModel;
import com.obiapp.models.TypeModel;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.share.Common;
import com.obiapp.tags.Tags;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAdsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ActivityAddAdsBinding binding;
    private String lang;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long currentPosition = 0;
    private boolean playWhenReady = true;
    private Uri videoUri = null;
    private List<String> imagesUriList;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2, VIDEO_REQ = 3;
    private double lat = 0.0, lng = 0.0;
    private String address = "";
    private GoogleMap mMap;
    private Marker marker;
    private float zoom = 15.0f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private FragmentMapTouchListener fragment;
    private List<DepartmentModel> departmentModelList;
    private List<TypeModel> typeModelList;
    private SpinnerDepartmentAdapter spinnerDepartmentAdapter;
    private SpinnerTypeAdapter spinnerTypeAdapter;

    private ImageAdsAdapter imageAdsAdapter;
    private boolean isVideoAvailable = false;
    private List<ItemAddAds> itemAddAdsList;
    private List<View> viewList;
    private AddAdsModel model;
    private Preferences preferences;
    private UserModel userModel;

    private List<GovernorateModel> governorateModelList;
    private SpinnerGovernorateAdapter spinnerGovernorateAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_ads);
        initView();
    }


    private void initView() {
        governorateModelList = new ArrayList<>();
        typeModelList = new ArrayList<>();
        model = new AddAdsModel();
        viewList = new ArrayList<>();
        itemAddAdsList = new ArrayList<>();
        departmentModelList = new ArrayList<>();
        imagesUriList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setModel(model);
        binding.setLang(lang);


        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setId(0);
        departmentModel.setTitle(getString(R.string.ch_dept));
        departmentModelList.add(departmentModel);


        TypeModel typeModel = new TypeModel();
        typeModel.setId(0);
        typeModel.setTitle(getString(R.string.ch_types));
        typeModelList.add(typeModel);

        updateUI();

        binding.recView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdsAdapter = new ImageAdsAdapter(imagesUriList, this);
        binding.recView.setAdapter(imageAdsAdapter);


        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.edtSearch.getText().toString();
                if (!TextUtils.isEmpty(query)) {
                    Common.CloseKeyBoard(this, binding.edtSearch);
                    Search(query);
                    return false;
                }
            }
            return false;
        });
        spinnerDepartmentAdapter = new SpinnerDepartmentAdapter(departmentModelList, this);
        binding.spinnerCategory.setAdapter(spinnerDepartmentAdapter);


        spinnerTypeAdapter = new SpinnerTypeAdapter(typeModelList, this);
        binding.spinnerType.setAdapter(spinnerTypeAdapter);

        GovernorateModel governorateModel = new GovernorateModel(0, "إختر المحافظة", "Choose governorate");
        governorateModelList.add(governorateModel);

        spinnerGovernorateAdapter = new SpinnerGovernorateAdapter(governorateModelList, this);
        binding.spinnerGovernate.setAdapter(spinnerGovernorateAdapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                typeModelList.clear();
                TypeModel typeModel = new TypeModel();
                typeModel.setId(0);
                typeModel.setTitle(getString(R.string.ch_types));
                typeModelList.add(typeModel);
                spinnerTypeAdapter.notifyDataSetChanged();

                if (i == 0) {

                    model.setCategory_id(0);
                    if (itemAddAdsList.size() > 0) {
                        removeItems();
                    }
                } else {
                    model.setCategory_id(departmentModelList.get(i).getId());
                    getTypes(model.getCategory_id());

                    getItems(model.getCategory_id());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerGovernate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    model.setGovernate_id(0);
                } else {
                    model.setGovernate_id(governorateModelList.get(i).getId());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    typeModelList.clear();
                    TypeModel typeModel = new TypeModel();
                    typeModel.setId(0);
                    typeModel.setTitle(getString(R.string.ch_types));
                    typeModelList.add(typeModel);
                    model.setType_id(0);

                    spinnerTypeAdapter.notifyDataSetChanged();
                    if (itemAddAdsList.size() > 0) {
                        removeItems();
                    }
                } else {
                    model.setType_id(typeModelList.get(i).getId());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.checkboxSwear.setOnClickListener(view -> {
            if (binding.checkboxSwear.isChecked()) {
                model.setSwear(true);
                Intent intent = new Intent(this, SwearActivity.class);
                startActivity(intent);
            } else {
                model.setSwear(false);

            }

            binding.setModel(model);
        });
        binding.flUploadImage.setOnClickListener(view -> {
            openSheet();
        });

        binding.flGallery.setOnClickListener(view -> checkReadPermission());

        binding.flCamera.setOnClickListener(view -> checkCameraPermission());

        binding.btnCancel.setOnClickListener(view -> {
            closeSheet();
        });


        binding.flUploadVideo.setOnClickListener(view -> {
            checkVideoPermission();
        });



        binding.llBack.setOnClickListener(view -> back());

        binding.btnSend.setOnClickListener(view -> checkDataValid());
        getDepartment();
        getGovernorate();


    }

    private void addItems() {

        removeItems();

        List<ItemAddAds> itemAddAdsList = new ArrayList<>();
        for (ItemAddAds itemAddAds : this.itemAddAdsList) {
            itemAddAds.setValue_of_attribute("");
            ItemAddAdsBinding itemAddAdsBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.item_add_ads, null, false);
            itemAddAdsBinding.tvTitle.setText(itemAddAds.getTitle_of_attribute());
            itemAddAdsBinding.edt.setHint(itemAddAds.getTitle_of_attribute());
            itemAddAdsBinding.edt.setTag(itemAddAds.getId());
            itemAddAdsBinding.setModel(itemAddAds);
            itemAddAdsList.add(itemAddAds);
            binding.llAdditionViews.addView(itemAddAdsBinding.getRoot());
            viewList.add(itemAddAdsBinding.getRoot());
        }
        model.setItemAddAdsList(itemAddAdsList);

    }

    private void removeItems() {
        binding.llAdditionViews.removeAllViews();
        viewList.clear();
    }

    private void getDepartment() {
        try {

            Api.getService(Tags.base_url)
                    .getDepartment()
                    .enqueue(new Callback<DepartmentDataModel>() {
                        @Override
                        public void onResponse(Call<DepartmentDataModel> call, Response<DepartmentDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        departmentModelList.clear();
                                        DepartmentModel departmentModel = new DepartmentModel();
                                        departmentModel.setId(0);
                                        departmentModel.setTitle(getString(R.string.ch_dept));
                                        departmentModelList.add(departmentModel);

                                        departmentModelList.addAll(response.body().getData());
                                        runOnUiThread(() -> spinnerDepartmentAdapter.notifyDataSetChanged());

                                    }
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                    Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DepartmentDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(AddAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }


    }

    private void getTypes(int category_id) {
        try {
            Api.getService(Tags.base_url)
                    .getTypes(category_id)
                    .enqueue(new Callback<TypeDataModel>() {
                        @Override
                        public void onResponse(Call<TypeDataModel> call, Response<TypeDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        typeModelList.clear();
                                        TypeModel typeModel = new TypeModel();
                                        typeModel.setId(0);
                                        typeModel.setTitle(getString(R.string.ch_types));
                                        typeModelList.add(typeModel);
                                        typeModelList.addAll(response.body().getData());
                                        runOnUiThread(() -> spinnerTypeAdapter.notifyDataSetChanged());
                                    } else {

                                    }
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                    Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TypeDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(AddAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void getGovernorate() {
        try {

            Api.getService(Tags.base_url)
                    .getGovernorate()
                    .enqueue(new Callback<GovernorateDataModel>() {
                        @Override
                        public void onResponse(Call<GovernorateDataModel> call, Response<GovernorateDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        governorateModelList.clear();
                                        GovernorateModel governorateModel = new GovernorateModel(0, "إختر المحافظة", "Choose governorate");

                                        governorateModelList.add(governorateModel);

                                        governorateModelList.addAll(response.body().getData());
                                        runOnUiThread(() -> spinnerGovernorateAdapter.notifyDataSetChanged());

                                    }
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                    Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GovernorateDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(AddAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }


    }


    private void initPlayer(Uri uri) {

        if (isVideoAvailable) {
            binding.flPlayerView.setVisibility(View.GONE);
            DataSource.Factory factory = new DefaultDataSourceFactory(this, "Ta3leem_live");


            if (player == null) {
                player = new SimpleExoPlayer.Builder(this).build();
                binding.player.setPlayer(player);
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);
                player.prepare(mediaSource);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            } else {

                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            release();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            release();
        }
    }


    private void release() {
        if (player != null) {
            currentWindow = player.getCurrentWindowIndex();
            currentPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer(videoUri);
    }

    public void back() {
        finish();
    }

    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }


    public void checkDataValid() {
        model.setImagesList(imagesUriList);
        for (int index = 0; index < model.getItemAddAdsList().size(); index++) {
            ItemAddAds itemAddAds = model.getItemAddAdsList().get(index);
            View view = viewList.get(index);
            LinearLayout linearLayout = (LinearLayout) view;
            EditText editText = linearLayout.findViewWithTag(itemAddAds.getId());
            if (itemAddAds.getValue_of_attribute().isEmpty()) {
                editText.setError(getString(R.string.field_required));
            } else {
                editText.setError(null);

            }
        }
        if (model.isDataValid(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (videoUri != null && model.getItemAddAdsList().size() > 0) {
                addAdsWithVideoWithList();
            } else if (videoUri == null && model.getItemAddAdsList().size() == 0) {
                addAdsWithoutVideoWithoutList();

            } else if (videoUri != null && model.getItemAddAdsList().size() == 0) {
                addAdsWithVideoWithoutList();

            } else if (videoUri == null && model.getItemAddAdsList().size() > 0) {
                addAdsWithoutVideoWithList();

            }
        }
    }

    private void addAdsWithoutVideoWithList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody category_id_part = Common.getRequestBodyText(String.valueOf(model.getCategory_id()));
        RequestBody governorate_id_part = Common.getRequestBodyText(String.valueOf(model.getGovernate_id()));
        RequestBody type_id_part = Common.getRequestBodyText(String.valueOf(model.getType_id()));
        RequestBody price_part = Common.getRequestBodyText(String.valueOf(model.getPrice()));
        RequestBody details_part = Common.getRequestBodyText(String.valueOf(model.getDetails()));

        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part main_image_part = Common.getMultiPart(this, Uri.parse(imagesUriList.get(0)), "main_image");


        Map<String, RequestBody> map = new HashMap<>();
        for (int index = 0; index < model.getItemAddAdsList().size(); index++) {
            map.put("product_details[" + index + "][title_of_attribute]", Common.getRequestBodyText(model.getItemAddAdsList().get(index).getTitle_of_attribute()));
            map.put("product_details[" + index + "][value_of_attribute]", Common.getRequestBodyText(model.getItemAddAdsList().get(index).getValue_of_attribute()));

        }


        Api.getService(Tags.base_url)
                .addAdsWithoutVideoWithList("Bearer " + userModel.getData().getToken(), category_id_part, governorate_id_part, type_id_part, title_part, price_part, address_part, lat_part, lng_part, details_part, main_image_part, getMultipartImage(), map)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                            {
                                Log.e("mmmmmmmmmm", response.code() + "__" + response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc", t.getMessage());

                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addAdsWithVideoWithoutList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody category_id_part = Common.getRequestBodyText(String.valueOf(model.getCategory_id()));
        RequestBody governorate_id_part = Common.getRequestBodyText(String.valueOf(model.getGovernate_id()));
        RequestBody type_id_part = Common.getRequestBodyText(String.valueOf(model.getType_id()));
        RequestBody price_part = Common.getRequestBodyText(String.valueOf(model.getPrice()));
        RequestBody details_part = Common.getRequestBodyText(String.valueOf(model.getDetails()));

        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part main_image_part = Common.getMultiPart(this, Uri.parse(imagesUriList.get(0)), "main_image");
        MultipartBody.Part video_part = Common.getMultiPartVideo(this, videoUri, "video");

        Api.getService(Tags.base_url)
                .addAdsWithVideoWithoutList("Bearer " + userModel.getData().getToken(), category_id_part, governorate_id_part, type_id_part, title_part, price_part, address_part, lat_part, lng_part, details_part, main_image_part, video_part, getMultipartImage())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                            {
                                Log.e("mmmmmmmmmm", response.code() + "__" + response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc", t.getMessage());

                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void addAdsWithoutVideoWithoutList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody category_id_part = Common.getRequestBodyText(String.valueOf(model.getCategory_id()));
        RequestBody governorate_id = Common.getRequestBodyText(String.valueOf(model.getGovernate_id()));
        RequestBody type_id = Common.getRequestBodyText(String.valueOf(model.getType_id()));
        RequestBody details_part = Common.getRequestBodyText(String.valueOf(model.getDetails()));

        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part main_image_part = Common.getMultiPart(this, Uri.parse(imagesUriList.get(0)), "main_image");

        Api.getService(Tags.base_url)
                .addAdsWithoutVideoWithoutList("Bearer " + userModel.getData().getToken(), category_id_part, governorate_id, type_id, title_part, price_part, address_part, lat_part, lng_part, details_part, main_image_part, getMultipartImage())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                            {
                                Log.e("mmmmmmmmmm", response.code() + "__" + response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc", t.getMessage());

                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void addAdsWithVideoWithList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody title_part = Common.getRequestBodyText(model.getName());
        RequestBody category_id_part = Common.getRequestBodyText(String.valueOf(model.getCategory_id()));
        RequestBody governorate_id_part = Common.getRequestBodyText(String.valueOf(model.getGovernate_id()));
        RequestBody type_id_part = Common.getRequestBodyText(String.valueOf(model.getType_id()));
        RequestBody details_part = Common.getRequestBodyText(String.valueOf(model.getDetails()));

        RequestBody price_part = Common.getRequestBodyText(model.getPrice());

        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part main_image_part = Common.getMultiPart(this, Uri.parse(imagesUriList.get(0)), "main_image");
        MultipartBody.Part video_part = Common.getMultiPartVideo(this, videoUri, "video");


        Map<String, RequestBody> map = new HashMap<>();
        for (int index = 0; index < model.getItemAddAdsList().size(); index++) {
            map.put("product_details[" + index + "][title_of_attribute]", Common.getRequestBodyText(model.getItemAddAdsList().get(index).getTitle_of_attribute()));
            map.put("product_details[" + index + "][value_of_attribute]", Common.getRequestBodyText(model.getItemAddAdsList().get(index).getValue_of_attribute()));

        }


        Api.getService(Tags.base_url)
                .addAdsWithVideoWithList("Bearer " + userModel.getData().getToken(), category_id_part, governorate_id_part, type_id_part, title_part, price_part, address_part, lat_part, lng_part, details_part, main_image_part, video_part, getMultipartImage(), map)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                            {
                                Log.e("mmmmmmmmmm", response.code() + "__" + response.errorBody());

                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc", t.getMessage());

                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private List<MultipartBody.Part> getMultipartImage() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (String path : imagesUriList) {
            Uri uri = Uri.parse(path);
            MultipartBody.Part part = Common.getMultiPart(this, uri, "images[]");
            parts.add(part);
        }
        return parts;
    }

    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }

    public void checkVideoPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, VIDEO_REQ);
        } else {
            displayVideoIntent(VIDEO_REQ);
        }
    }

    private void displayVideoIntent(int video_req) {
        Intent intent = new Intent();

        if (video_req == VIDEO_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("video/*");
            startActivityForResult(intent, video_req);

        }
    }

    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                initGoogleApi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == VIDEO_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayVideoIntent(requestCode);
            } else {
                Toast.makeText(this, R.string.vid_pem_denied, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            startLocationUpdate();
        } else if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (imagesUriList.size() < 6) {
                imagesUriList.add(uri.toString());
                imageAdsAdapter.notifyItemInserted(imagesUriList.size() - 1);
            } else {
                Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                if (imagesUriList.size() < 6) {
                    imagesUriList.add(uri.toString());
                    imageAdsAdapter.notifyItemInserted(imagesUriList.size() - 1);

                } else {
                    Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == VIDEO_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            new VideoTask().execute(uri);
        }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }


    private void Search(String query) {

        String fields = "id,place_id,name,geometry,formatted_address";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, "ar", getString(R.string.map_key))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {

                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getCandidates().size() > 0) {

                                address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtSearch.setText(address);
                                LatLng latLng = new LatLng(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(), response.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                                addMarker(latLng);
                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {

                            Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getGeoData(final double lat, double lng) {
        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, "ar", getString(R.string.map_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().getResults().size() > 0) {
                                address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                binding.edtSearch.setText(address);
                                model.setLat(lat);
                                model.setLng(lng);
                                model.setAddress(address);

                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {
                            Toast.makeText(AddAdsActivity.this, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(AddAdsActivity.this, fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddAdsActivity.this, new String[]{fineLocPerm}, loc_req);
        } else {
            mMap.setMyLocationEnabled(true);
            initGoogleApi();
        }
    }

    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(AddAdsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private void updateUI() {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));
            checkPermission();

            mMap.setOnMapClickListener(latLng -> {
                lat = latLng.latitude;
                lng = latLng.longitude;
                LatLng latLng2 = new LatLng(lat, lng);
                addMarker(latLng2);
                getGeoData(lat, lng);

            });


        }
    }

    private void addMarker(LatLng latLng) {
        model.setLat(latLng.latitude);
        model.setLng(latLng.longitude);
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        } else {
            marker.setPosition(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(AddAdsActivity.this, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(AddAdsActivity.this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        getGeoData(lat, lng);
        addMarker(new LatLng(lat, lng));
        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(AddAdsActivity.this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    public void deleteImage(int adapterPosition) {
        if (imagesUriList.size() > 0) {
            imagesUriList.remove(adapterPosition);
            imageAdsAdapter.notifyItemRemoved(adapterPosition);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VideoTask extends AsyncTask<Uri, Void, Long> {
        MediaMetadataRetriever retriever;
        private Uri uri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retriever = new MediaMetadataRetriever();
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            uri = uris[0];
            retriever.setDataSource(AddAdsActivity.this, uris[0]);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(time) / 1000;
            retriever.release();
            return duration;
        }

        @Override
        protected void onPostExecute(Long duration) {
            super.onPostExecute(duration);
            if (duration <= 59) {
                isVideoAvailable = true;
                videoUri = uri;
                model.setVideo_url(videoUri.toString());
                initPlayer(videoUri);

            } else {
                Toast.makeText(AddAdsActivity.this, R.string.length_video_shouldnot_exceed, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getItems(int sub_department_id) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ;
        Api.getService(Tags.base_url)
                .getItemsAds(sub_department_id)
                .enqueue(new Callback<ItemAddAdsDataModel>() {
                    @Override
                    public void onResponse(Call<ItemAddAdsDataModel> call, Response<ItemAddAdsDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            if (response.body().getStatus() == 200) {

                                if (response.body() != null && response.body().getData() != null) {
                                    if (response.body().getData().size() > 0) {
                                        itemAddAdsList.clear();
                                        itemAddAdsList.addAll(response.body().getData());
                                        Log.e("size", itemAddAdsList.size() + "__");
                                        model.setHasExtraItems(true);
                                        addItems();
                                    } else {
                                        model.setHasExtraItems(false);
                                        model.setItemAddAdsList(new ArrayList<>());
                                        removeItems();

                                    }
                                } else {
                                    Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }


                            } else {
                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            dialog.dismiss();

                            if (response.code() == 500) {
                                Toast.makeText(AddAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(AddAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ItemAddAdsDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(AddAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }


}