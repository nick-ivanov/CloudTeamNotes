package nickivanov.pro.teamnotes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsDialogFragment extends DialogFragment {
    public static final String PREFS_NAME = "TeamNotesPrefs";

    public static final String SETTING_NAME = "setting_name";
    public static final String SETTING_NAME_DEFAULT_VALUE = "";

    public static final String SETTING_SERVER = "setting_server";
    public static final String SETTING_SERVER_DEFAULT_VALUE = "";

    public static final String SETTING_TOKEN = "setting_token";
    public static final String SETTING_TOKEN_DEFAULT_VALUE = "";

    public static final String SETTING_PWD = "setting_pwd";
    public static final String SETTING_PWD_DEFAULT_VALUE = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);


        View v =  inflater.inflate(R.layout.settings_dialog, container, false);

        Button okButton = (Button) v.findViewById(R.id.ok_button);
        Button notnowButton = (Button) v.findViewById(R.id.notnow_button);

        final EditText nameEditText = (EditText) v.findViewById(R.id.settings_name_input);
        nameEditText.setText(settings.getString(SETTING_NAME, SETTING_NAME_DEFAULT_VALUE));

        final EditText serverEditText = (EditText) v.findViewById(R.id.settings_server_input);
        serverEditText.setText(settings.getString(SETTING_SERVER, SETTING_SERVER_DEFAULT_VALUE));

        final EditText tokenEditText = (EditText) v.findViewById(R.id.settings_token_input);
        tokenEditText.setText(settings.getString(SETTING_TOKEN, SETTING_TOKEN_DEFAULT_VALUE));

        final EditText pwdEditText = (EditText) v.findViewById(R.id.settings_pwd_input);
        pwdEditText.setText(settings.getString(SETTING_PWD, SETTING_PWD_DEFAULT_VALUE));


        final DialogFragment fragment = this;

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SETTING_NAME, nameEditText.getText().toString());
                editor.putString(SETTING_SERVER, serverEditText.getText().toString());
                editor.putString(SETTING_TOKEN, tokenEditText.getText().toString());
                editor.putString(SETTING_PWD, pwdEditText.getText().toString());


                if(editor.commit()) {
                    Toast.makeText(getActivity(), "Settings saved!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Error occurred while saving settings.", Toast.LENGTH_LONG).show();
                }

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });


        notnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });


        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }
}
