package student.inti.goapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class ResetPassword {
    private Activity activity;
    private AlertDialog alertDialog;

    ResetPassword(Activity myActivity){
        activity = myActivity;
    }

    void startLodingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.reset,null));
        builder.setCancelable(true);

        alertDialog=builder.create();
        alertDialog.show();
    }
    void dissmissDialog(){
        alertDialog.dismiss();
    }
}