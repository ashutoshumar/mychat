package fragment;

import com.ashutosh.mychat.Notifications.MyResponse;
import com.ashutosh.mychat.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
           "Content-Type:application/json",
            "Authorization:key=AAAA-aBBb0w:APA91bGWMLDL_iOyBzhlcLFnO22tpm75NS3EwSPRHrmTrQjbtgbF9alPI7Irk3aXY05IWNKFjxhbMzCwkR2l-yBJix1bmwn_FEiIzsxEdEyrO1w-a7P9lhHAmVWmOmY8ZeaV4v9osq6n"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
