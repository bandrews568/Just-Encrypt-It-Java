package github.bandrews568.justencryptit.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import github.bandrews568.justencryptit.R;

public class UiUtils {

    public static void errorToast(Activity activity, String message) {
        Toast toast = new Toast(activity);

        View custom_view = activity.getLayoutInflater().inflate(R.layout.toast_error, null);
        ((TextView) custom_view.findViewById(R.id.tv_toast_message)).setText(message);

        toast.setView(custom_view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
