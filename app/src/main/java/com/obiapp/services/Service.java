package com.obiapp.services;

import com.obiapp.models.AdminMessageDataModel;
import com.obiapp.models.AdminRoomDataModel;
import com.obiapp.models.AllCatogryModel;
import com.obiapp.models.CouponDataModel;
import com.obiapp.models.DepartmentDataModel;
import com.obiapp.models.ItemAddAdsDataModel;
import com.obiapp.models.MessageDataModel;
import com.obiapp.models.NotificationDataModel;
import com.obiapp.models.OtherProfileDataModel;
import com.obiapp.models.PlaceGeocodeData;
import com.obiapp.models.PlaceMapDetailsData;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.RoomDataModel;
import com.obiapp.models.RoomDataModel2;
import com.obiapp.models.SettingDataModel;
import com.obiapp.models.SingleAdminMessageDataModel;
import com.obiapp.models.SingleCouponModel;
import com.obiapp.models.SingleMessageDataModel;
import com.obiapp.models.SingleProductDataModel;
import com.obiapp.models.SliderModel;
import com.obiapp.models.StatusResponse;
import com.obiapp.models.TypeDataModel;
import com.obiapp.models.UserModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("address") String address,
                                       @Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("software_type") String software_type
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("address") RequestBody address,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("software_type") RequestBody software_type,
                                    @Part MultipartBody.Part logo


    );


    @FormUrlEncoded
    @POST("api/updateProfile")
    Call<UserModel> updateProfileWithoutImage(@Header("Authorization") String bearer_token,
                                              @Field("name") String name,
                                              @Field("phone_code") String phone_code,
                                              @Field("phone") String phone,
                                              @Field("address") String address,
                                              @Field("latitude") double latitude,
                                              @Field("longitude") double longitude,
                                              @Field("software_type") String software_type
    );

    @Multipart
    @POST("api/updateProfile")
    Call<UserModel> updateProfileWithImage(@Header("Authorization") String bearer_token,
                                           @Part("name") RequestBody name,
                                           @Part("phone_code") RequestBody phone_code,
                                           @Part("phone") RequestBody phone,
                                           @Part("address") RequestBody address,
                                           @Part("latitude") RequestBody latitude,
                                           @Part("longitude") RequestBody longitude,
                                           @Part("software_type") RequestBody software_type,
                                           @Part MultipartBody.Part logo


    );

    @GET("api/home-link-filter")
    Call<ProductsDataModel> getProducts(
            @Query(value = "user_id") String user_id,
            @Query(value = "search_key") String search_key
    );
    @GET("api/home-sliders")
    Call<SliderModel> getSlider();
    @GET("api/offerSlider")
    Call<ProductsDataModel> getLatestOffer();

    @GET("api/allOffers")
    Call<ProductsDataModel> getOffer();

    @GET("api/all-categories")
    Call<DepartmentDataModel> getDepartment();

    @FormUrlEncoded
    @POST("api/my-chat-rooms")
    Call<RoomDataModel> getRooms(@Header("Authorization") String user_token,
                                 @Field("user_id") int user_id
    );

    @GET("api/productFilterByCategory")
    Call<ProductsDataModel> getFilteredProducts(@Query(value = "category_id") int category_id,
                                                @Query(value = "sub_category_id") int sub_category_id,
                                                @Query(value = "using_user_location") String using_my_location,
                                                @Query(value = "using_price") String using_price,
                                                @Query(value = "type_id") int type_id

    );

    @FormUrlEncoded
    @POST("api/getTypesByCatIdOrSubCatId")
    Call<TypeDataModel> getTypes(@Field("category_id") int category_id,
                                 @Field("sub_category_id") int sub_category_id

    );

    @FormUrlEncoded
    @POST("api/singleProduct")
    Call<SingleProductDataModel> getProductById(@Field("product_id") int product_id,
                                                @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("api/favouriteProductToggle")
    Call<StatusResponse> like_disliked(@Header("Authorization") String user_token,
                                       @Field("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("api/makeReport")
    Call<StatusResponse> report(@Header("Authorization") String user_token,
                                @Field("product_id") int product_id,
                                @Field("title") String title
    );

    @FormUrlEncoded
    @POST("api/allAttribuitesBySubCategoryId")
    Call<ItemAddAdsDataModel> getItemsAds(@Field("sub_category_id") int sub_category_id

    );

    @GET("api/myCoupons")
    Call<CouponDataModel> getMyCoupon(@Header("Authorization") String user_token);


    @FormUrlEncoded
    @POST("api/singleCoupon")
    Call<SingleCouponModel> getSingleCoupon(@Field("coupon_id") int coupon_id,
                                            @Field("user_id") int user_id
    );

    @Multipart
    @POST("api/makeNewCoupon")
    Call<StatusResponse> addCoupon(@Header("Authorization") String user_token,
                                   @Part("title") RequestBody title,
                                   @Part("brand_title") RequestBody brand_title,
                                   @Part("coupon_code") RequestBody coupon_code,
                                   @Part("offer_value") RequestBody offer_value,
                                   @Part("from_date") RequestBody from_date,
                                   @Part MultipartBody.Part image

    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithVideoWithList(@Header("Authorization") String user_token,
                                                 @Part("category_id") RequestBody category_id,
                                                 @Part("sub_category_id") RequestBody sub_category_id,
                                                 @Part("title") RequestBody title,
                                                 @Part("price") RequestBody price,
                                                 @Part("old_price") RequestBody old_price,
                                                 @Part("address") RequestBody address,
                                                 @Part("latitude") RequestBody latitude,
                                                 @Part("longitude") RequestBody longitude,
                                                 @Part("have_offer") RequestBody have_offer,
                                                 @Part("offer_value") RequestBody offer_value,
                                                 @Part MultipartBody.Part main_image,
                                                 @Part MultipartBody.Part vedio,
                                                 @Part List<MultipartBody.Part> images,
                                                 @Part("types[]") List<RequestBody> types,
                                                 @PartMap() Map<String, RequestBody> map
    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithoutVideoWithoutList(@Header("Authorization") String user_token,
                                                       @Part("category_id") RequestBody category_id,
                                                       @Part("sub_category_id") RequestBody sub_category_id,
                                                       @Part("title") RequestBody title,
                                                       @Part("price") RequestBody price,
                                                       @Part("old_price") RequestBody old_price,
                                                       @Part("address") RequestBody address,
                                                       @Part("latitude") RequestBody latitude,
                                                       @Part("longitude") RequestBody longitude,
                                                       @Part("have_offer") RequestBody have_offer,
                                                       @Part("offer_value") RequestBody offer_value,
                                                       @Part MultipartBody.Part main_image,
                                                       @Part List<MultipartBody.Part> images,
                                                       @Part("types[]") List<RequestBody> types);


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithoutVideoWithList(@Header("Authorization") String user_token,
                                                    @Part("category_id") RequestBody category_id,
                                                    @Part("sub_category_id") RequestBody sub_category_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("price") RequestBody price,
                                                    @Part("old_price") RequestBody old_price,
                                                    @Part("address") RequestBody address,
                                                    @Part("latitude") RequestBody latitude,
                                                    @Part("longitude") RequestBody longitude,
                                                    @Part("have_offer") RequestBody have_offer,
                                                    @Part("offer_value") RequestBody offer_value,
                                                    @Part MultipartBody.Part main_image,
                                                    @Part List<MultipartBody.Part> images,
                                                    @Part("types[]") List<RequestBody> types,
                                                    @PartMap() Map<String, RequestBody> map

    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithVideoWithoutList(@Header("Authorization") String user_token,
                                                    @Part("category_id") RequestBody category_id,
                                                    @Part("sub_category_id") RequestBody sub_category_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("price") RequestBody price,
                                                    @Part("old_price") RequestBody old_price,
                                                    @Part("address") RequestBody address,
                                                    @Part("latitude") RequestBody latitude,
                                                    @Part("longitude") RequestBody longitude,
                                                    @Part("have_offer") RequestBody have_offer,
                                                    @Part("offer_value") RequestBody offer_value,
                                                    @Part MultipartBody.Part main_image,
                                                    @Part MultipartBody.Part vedio,
                                                    @Part List<MultipartBody.Part> images,
                                                    @Part("types[]") List<RequestBody> types
    );

    @FormUrlEncoded
    @POST("api/makeActionOnCoupon")
    Call<StatusResponse> couponAction(@Header("Authorization") String user_token,
                                      @Field("coupon_id") int coupon_id,
                                      @Field("like_kind") String like_kind
    );

    @FormUrlEncoded
    @POST("api/deleteCoupon")
    Call<StatusResponse> deleteCoupon(@Header("Authorization") String user_token,
                                      @Field("coupon_id") int coupon_id);


    @FormUrlEncoded
    @POST("api/firebase-tokens")
    Call<StatusResponse> updateFirebaseToken(@Field("firebase_token") String firebase_token,
                                             @Field("user_id") int user_id,
                                             @Field("software_type") String software_type

    );

    @FormUrlEncoded
    @POST("api/firebase/token/delete")
    Call<StatusResponse> deleteFirebaseToken(@Field("firebase_token") String firebase_token,
                                             @Field("user_id") int user_id

    );

    @POST("api/logout")
    Call<StatusResponse> logout(@Header("Authorization") String user_token);


    @GET("api/myProducts")
    Call<ProductsDataModel> getMyProducts(@Header("Authorization") String user_token);

    @FormUrlEncoded
    @POST("api/deleteProduct")
    Call<StatusResponse> deleteProduct(@Header("Authorization") String user_token,
                                       @Field("product_id") int product_id

    );

    @GET("api/myFavouriteProducts")
    Call<ProductsDataModel> getMyFavorite(@Header("Authorization") String user_token);


    @GET("api/my-notifications")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String user_token);

    @FormUrlEncoded
    @POST("api/notification/remove")
    Call<StatusResponse> deleteNotification(@Header("Authorization") String user_token,
                                            @Field("notification_id") int notification_id
    );

    @FormUrlEncoded
    @POST("api/contactUs")
    Call<StatusResponse> contactUs(@Field("name") String name,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("message") String message
    );

    @FormUrlEncoded
    @POST("api/single-chat-room")
    Call<MessageDataModel> getChatMessages(@Header("Authorization") String bearer_token,
                                           @Field("room_id") int room_id

    );

    @FormUrlEncoded
    @POST("api/message/send")
    Call<SingleMessageDataModel> sendChatMessage(@Header("Authorization") String bearer_token,
                                                 @Field("room_id") int room_id,
                                                 @Field("from_user_id") int from_user_id,
                                                 @Field("to_user_id") int to_user_id,
                                                 @Field("message_kind") String message_kind,
                                                 @Field("date") long date,
                                                 @Field("message") String message


    );

    @Multipart
    @POST("api/message/send")
    Call<SingleMessageDataModel> sendChatAttachment(@Header("Authorization") String bearer_token,
                                                    @Part("room_id") RequestBody room_id,
                                                    @Part("from_user_id") RequestBody from_user_id,
                                                    @Part("to_user_id") RequestBody to_user_id,
                                                    @Part("message_kind") RequestBody message_kind,
                                                    @Part("date") RequestBody date,
                                                    @Part MultipartBody.Part attachment
    );


    @FormUrlEncoded
    @POST("api/chatRoom/get")
    Call<RoomDataModel2> createRoom(@Header("Authorization") String user_token,
                                    @Field("from_user_id") int from_user_id,
                                    @Field("to_user_id") int to_user_id
    );


    @GET("api/adminChatRoomGet")
    Call<AdminRoomDataModel> createAdminRoom(@Header("Authorization") String user_token

    );

    @FormUrlEncoded
    @POST("api/allChatRoomData")
    Call<AdminMessageDataModel> getAdminChatMessage(@Header("Authorization") String user_token,
                                                    @Field("room_id") int from_user_id);


    @FormUrlEncoded
    @POST("api/sendMessageToAdmin")
    Call<SingleAdminMessageDataModel> sendAdminChatMessage(@Header("Authorization") String bearer_token,
                                                           @Field("room_id") int room_id,
                                                           @Field("message_kind") String message_kind,
                                                           @Field("date") long date,
                                                           @Field("message") String message


    );

    @Multipart
    @POST("api/sendMessageToAdmin")
    Call<SingleAdminMessageDataModel> sendAdminChatAttachment(@Header("Authorization") String bearer_token,
                                                              @Part("room_id") RequestBody room_id,
                                                              @Part("message_kind") RequestBody message_kind,
                                                              @Part("date") RequestBody date,
                                                              @Part MultipartBody.Part attachment
    );


    @GET("api/app/info")
    Call<SettingDataModel> getSettings();


    @FormUrlEncoded
    @POST("api/profileOfOther")
    Call<OtherProfileDataModel> getOtherProfile(@Header("Authorization") String bearer_token,
                                                @Field("user_id") int user_id,
                                                @Field("other_user_id") int other_user_id


    );

    @FormUrlEncoded
    @POST("api/followUserToggle")
    Call<StatusResponse> follow_un_follow(@Header("Authorization") String bearer_token,
                                          @Field("other_user_id") int other_user_id


    );
    @GET("api/categories")
    Call<AllCatogryModel> getcategories(

    );

}