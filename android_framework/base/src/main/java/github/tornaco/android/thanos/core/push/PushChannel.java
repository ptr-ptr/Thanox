package github.tornaco.android.thanos.core.push;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.Sets;
import github.tornaco.android.thanos.core.annotation.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@Getter
@ToString
public final class PushChannel implements Parcelable {

    public static final PushChannel FCM_GCM = new PushChannel(new String[]{
            "com.google.android.c2dm.intent.RECEIVE",
            "com.google.firebase.MESSAGING_EVENT"
    }, "google:fcm/gcm", "B75F00CB-D413-4E35-BBA1-80BB6DD0ADBB");

    public static final PushChannel MIPUSH = new PushChannel(new String[]{
            "com.xiaomi.mipush.RECEIVE_MESSAGE"
    }, "mi:mipush", "1A733BD6-9FB7-43CF-8CDA-513C0CF83DB7");

    @NonNull
    private String[] actions;
    @NonNull
    private String channelName;

    private String channelId;

    private PushChannel(Parcel in) {
        actions = in.readStringArray();
        channelName = in.readString();
        channelId = in.readString();
    }

    public static final Creator<PushChannel> CREATOR = new Creator<PushChannel>() {
        @Override
        public PushChannel createFromParcel(Parcel in) {
            return new PushChannel(in);
        }

        @Override
        public PushChannel[] newArray(int size) {
            return new PushChannel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(actions);
        parcel.writeString(channelName);
        parcel.writeString(channelId);
    }

    public boolean match(Intent intent) {
        return intent != null && Sets.newHashSet(actions).contains(intent.getAction());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PushChannel that = (PushChannel) o;
        return Arrays.equals(actions, that.actions) &&
                channelName.equals(that.channelName) &&
                channelId.equals(that.channelId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(channelName, channelId);
        result = 31 * result + Arrays.hashCode(actions);
        return result;
    }
}
