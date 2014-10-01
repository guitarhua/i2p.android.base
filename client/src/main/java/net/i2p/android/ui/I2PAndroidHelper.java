package net.i2p.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import net.i2p.android.lib.client.R;
import net.i2p.android.router.service.IRouterState;

/**
 * @author str4d
 * @since 0.2
 */
public class I2PAndroidHelper {
    public static final String URI_I2P_ANDROID = "net.i2p.android";
    public static final String URI_I2P_ANDROID_DONATE = "net.i2p.android.donate";
    public static final String URI_I2P_ANDROID_LEGACY = "net.i2p.android.legacy";

    public static final int REQUEST_START_I2P = 9857;

    private Context mContext;
    private boolean mTriedBindState;
    private IRouterState mStateService;

    public I2PAndroidHelper(Context context) {
        mContext = context;
    }

    /**
     * Try to bind to I2P Android. Call this method from
     * {@link android.app.Activity#onStart()}.
     */
    public void bind() {
        Intent i2pIntent = new Intent(IRouterState.class.getName());
        try {
            mTriedBindState = mContext.bindService(
                    i2pIntent, mStateConnection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            // Old version of I2P Android (pre-0.9.13), cannot use
            mStateService = null;
            mTriedBindState = false;
        }
    }

    /**
     * Unbind from I2P Android. Call this method from
     * {@link android.app.Activity#onStop()}.
     */
    public void unbind() {
        if (mTriedBindState)
            mContext.unbindService(mStateConnection);
        mTriedBindState = false;
    }

    private ServiceConnection mStateConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mStateService = IRouterState.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mStateService = null;
        }
    };

    /**
     * Check if I2P Android is installed.
     * @return true if I2P Android is installed, false otherwise.
     */
    public boolean isI2PAndroidInstalled() {
        return isAppInstalled(URI_I2P_ANDROID) ||
                isAppInstalled(URI_I2P_ANDROID_DONATE) ||
                isAppInstalled(URI_I2P_ANDROID_LEGACY);
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * Show dialog - install I2P Android from market or F-Droid.
     * @param activity the Activity this method has been called from.
     */
    public void promptToInstall(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.install_i2p_android)
                .setMessage(R.string.you_must_have_i2p_android)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String uriMarket = activity.getString(R.string.market_i2p_android);
                        Uri uri = Uri.parse(uriMarket);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.show();
    }

    /**
     * Check if I2P Android is running. If {@link net.i2p.android.ui.I2PAndroidHelper#bind()}
     * has not been called previously, this will always return false.
     * @return true if I2P Android is running, false otherwise.
     */
    public boolean isI2PAndroidRunning() {
        if (mStateService == null)
            return false;

        try {
            return mStateService.isStarted();
        } catch (RemoteException e) {
            // TODO: log
            return false;
        }
    }

    /**
     * Show dialog - request that I2P Android be started.
     * @param activity the Activity this method has been called from.
     */
    public void requestI2PAndroidStart(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.start_i2p_android)
                .setMessage(R.string.would_you_like_to_start_i2p_android)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent("net.i2p.android.router.START_I2P");
                        activity.startActivityForResult(i, REQUEST_START_I2P);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
}