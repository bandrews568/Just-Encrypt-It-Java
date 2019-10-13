package github.bandrews568.justencryptit.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import github.bandrews568.justencryptit.R;

public class UiUtils {

    public static void errorToast(Context context, String message) {
        Toast toast = new Toast(context);
        View custom_view = LayoutInflater.from(context).inflate(R.layout.toast_error, null);
        ((TextView) custom_view.findViewById(R.id.tv_toast_message)).setText(message);

        toast.setView(custom_view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void errorDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_dialog_error_message)).setText(message);
        dialog.findViewById(R.id.btn_dialog_error_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
