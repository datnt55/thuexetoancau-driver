package grab.com.thuexetoancau.driver.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import grab.com.thuexetoancau.driver.R;

/**
 * Created by DatNT on 8/5/2017.
 */

public class DialogUtils {
    // Show dialog request turn on gps
    public static void settingRequestTurnOnLocation(final Activity mActivity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.gps_notice_content)
                .setCancelable(false)
                .setPositiveButton(R.string.gps_continue,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mActivity.startActivityForResult(callGPSSettingIntent,Defines.REQUEST_LOCATION_ENABLE);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void confirmReiceverTrip(final Activity mActivity, final YesNoListenter listenter) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.confirm_receive_trip)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (listenter != null)
                                    listenter.onYes();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showDialogNetworkError(Context mContext, final TryAgain again){
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.app_name))
                .setMessage(mContext.getString(R.string.error_network))
                .setPositiveButton(mContext.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (again != null)
                            again.onTryAgain();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public static void showCancelTripConfirm(Activity mActivity, final ConfirmListenter listener) {
        // custom dialog
        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_reason_cancel);
        // set the custom dialog components - text, image and button
        final EditText edtCancel = (EditText) dialog.findViewById(R.id.edt_cancel_trip);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onConfirm(edtCancel.getText().toString());
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    public static void showLoginDialog(Activity mActivity, final YesNoListenter listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.logout_content_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                              if (listener != null)
                                  listener.onYes();
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.gps_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (listener != null)
                            listener.onNo();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showCheckTokenDialog(Activity mActivity, final YesNoListenter listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.check_token_content_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (listener != null)
                                    listener.onYes();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public interface TryAgain{
        void onTryAgain();
    }

    public interface YesNoListenter{
        void onYes();
        void onNo();
    }

    public interface ConfirmListenter{
        void onConfirm(String reason);
    }
}
