package com.example.bettabeal.api;

import com.example.bettabeal.model.ArticleResponse;
import com.example.bettabeal.model.CategoryResponse;
import com.example.bettabeal.model.OrderResponse;
import com.example.bettabeal.model.OrdersResponse;
import com.example.bettabeal.model.SignUpRequest;
import com.example.bettabeal.model.SignUpResponse;
import com.example.bettabeal.model.LoginRequest;
import com.example.bettabeal.model.LoginResponse;
import com.example.bettabeal.model.CustomerResponse;
import com.example.bettabeal.model.UpdateBiodataRequest;
import com.example.bettabeal.model.UpdateProfileImageRequest;
import com.example.bettabeal.model.BaseResponse;
import com.example.bettabeal.model.AddressRequest;
import com.example.bettabeal.model.AddressResponse;
import com.example.bettabeal.model.DistrictResponse;
import com.example.bettabeal.model.PoscodeResponse;
import com.example.bettabeal.model.Address;
import com.example.bettabeal.model.AddressListResponse;
import com.example.bettabeal.model.MainAddressResponse;
import com.example.bettabeal.model.ProductResponse;

import com.example.bettabeal.model.CategoryProductResponse;
import com.example.bettabeal.model.ProductDetailResponse;
import com.example.bettabeal.model.CartResponse;
import com.example.bettabeal.model.AddToCartRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface    ApiService {
   

    @Multipart
    @POST("api/seller/products")
    Call<ResponseBody> createProduct(
        @Header("Authorization") String token,
        @Part("category_id") RequestBody categoryId,
        @Part("product_name") RequestBody productName,
        @Part("description") RequestBody description,
        @Part("price") RequestBody price,
        @Part("stock_quantity") RequestBody stockQuantity,
        @Part MultipartBody.Part profile_image
    );

    @POST("api/register/customer")
    Call<SignUpResponse> registerCustomer(@Body SignUpRequest request);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/customers/{id}")
    Call<CustomerResponse> getCustomer(
        @Path("id") int userId,
        @Header("Authorization") String token
    );

    @POST("api/customer/biodata")
    Call<BaseResponse> updateBiodata(
        @Header("Authorization") String token,
        @Body UpdateBiodataRequest request
    );

    @Multipart
    @POST("api/customer/biodata")
    Call<BaseResponse> updateProfileImage(
        @Header("Authorization") String token,
        @Part MultipartBody.Part profile_image
    );

    @GET("api/districts")
    Call<DistrictResponse> getDistricts(@Header("Authorization") String token);

    @GET("api/districts/{districtId}/poscodes")
    Call<PoscodeResponse> getPoscodesByDistrict(
        @Header("Authorization") String token,
        @Path("districtId") int districtId
    );

    @GET("api/poscodes")
    Call<PoscodeResponse> getAllPoscodes(@Header("Authorization") String token);

    @POST("api/addresses")
    Call<AddressResponse> createAddress(
        @Header("Authorization") String token,
        @Body AddressRequest request
    );

    @GET("api/addresses")
    Call<AddressListResponse> getAddresses(@Header("Authorization") String token);

    @POST("api/addresses")
    Call<AddressResponse> saveAddress(@Header("Authorization") String token, @Body Address address);

    @GET("api/addresses/{addressId}")
    Call<AddressResponse> getAddress(
        @Header("Authorization") String token,
        @Path("addressId") int addressId
    );

    @PUT("api/addresses/{addressId}")
    Call<AddressResponse> updateAddress(
        @Header("Authorization") String token,
        @Path("addressId") int addressId,
        @Body AddressRequest request
    );

    @PUT("api/addresses/{id}/main")
    Call<AddressResponse> setMainAddress(
        @Header("Authorization") String token,
        @Path("id") Long addressId
    );

    @DELETE("api/addresses/{id}")
    Call<BaseResponse> deleteAddress(
        @Header("Authorization") String token,
        @Path("id") Long addressId
    );

    @GET("api/products")
    Call<ProductResponse> getProducts();

    @GET("api/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/categories/{categoryId}/products")
    Call<CategoryProductResponse> getProductsByCategory(@Path("categoryId") int categoryId);


    @GET("api/products/{productId}")
    Call<ProductDetailResponse> getProductDetail(@Path("productId") long productId);

    @Headers({
        "Accept: application/json",
        "Content-Type: application/json"
    })
    @POST("api/cart/add")
    Call<CartResponse> addToCart(
        @Header("Authorization") String token,
        @Body AddToCartRequest request
    );

    @GET("api/cart")
    Call<CartResponse> getCart(@Header("Authorization") String token);

    @PUT("api/cart/items/{cartItemId}")
    Call<CartResponse> updateCartItemQuantity(
        @Header("Authorization") String token,
        @Path("cartItemId") int cartItemId,
        @Query("quantity") int quantity
    );

    @PUT("api/cart/decrement/{cartItemId}")
    Call<CartResponse> decreaseCartItem(
        @Header("Authorization") String token,
        @Path("cartItemId") int cartItemId  
    );

    @DELETE("api/cart/items/{cart_item_id}")
Call<CartResponse> deleteCartItem(
    @Header("Authorization") String token,
    @Path("cart_item_id") int cartItemId
);

    @GET("/api/article")
    Call<ArticleResponse> getArticles();

    @POST("/api/orders")
    Call<OrderResponse> createOrder(
        @Header("Authorization") String token,
        @Body Map<String, Long> request
    );

    @GET("/api/orders")
    Call<OrdersResponse> getOrders(
        @Header("Authorization") String token,
        @Query("page") int page
    );

}
