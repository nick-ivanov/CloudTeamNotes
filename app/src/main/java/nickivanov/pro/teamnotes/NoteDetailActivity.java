package nickivanov.pro.teamnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;


import static nickivanov.pro.teamnotes.NoteDetailFragment.ARG_ITEM_ID;

public class NoteDetailActivity extends AppCompatActivity {
    final String TAG = "NoteDetailActivity";

    private SecureTeamNotesModel.SecureTeamNote mItem;
    public static final String PREFS_NAME = "TeamNotesPrefs";
    public static final String SETTING_NAME = "setting_name";
    public static final String SETTING_SERVER = "setting_server";
    public static final String SETTING_TOKEN = "setting_token";
    public static final String SETTING_PWD = "setting_pwd";
    private boolean unblockReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unblockReady = false;
        setContentView(R.layout.activity_note_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mItem = SecureTeamNotesModel.ITEM_MAP.get(getIntent().getStringExtra(ARG_ITEM_ID));
        final EditText subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        subjectEditText.setText(mItem.subject);
        final SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);

        subjectEditText.setEnabled(false);

        FloatingActionButton saveFloatingButton = (FloatingActionButton) findViewById(R.id.fab);
        saveFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View tmpview = view;
                final EditText et = (EditText) findViewById(R.id.note_detail);

                Thread thread = new Thread(new Runnable(){
                    public void run() {
                        try {
                            try {
                                TeamNotesController controller = new TeamNotesController(
                                        settings.getString(SETTING_NAME, ""),
                                        settings.getString(SETTING_SERVER, ""),
                                        settings.getString(SETTING_TOKEN, ""),
                                        settings.getString(SETTING_PWD, "")
                                );

                                if(!controller.editNote(mItem.id, subjectEditText.getText().toString(), et.getText().toString()).equals("success")) {
                                    Snackbar.make(tmpview, "Problem occurred while saving the note", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {

                                    if(controller.unlockNote(mItem.id).equals("success")) {
                                        Snackbar.make(tmpview, "Note saved and unlocked!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                unblockReady = false;
                                                subjectEditText.setEnabled(true);
                                                et.setEnabled(true);
                                                //editFloatingButton.setEnabled(false);
                                            }
                                        });
                                    } else {
                                        Snackbar.make(tmpview, "An error occurred while unlocking the note. The note is saved but not unlocked. Please try to save it one more time.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }

                            } catch (Exception ex) {
                                showNoticeDialog();
                            }
                        } catch (Exception e) {
                            showNoticeDialog();
                        }
                    }
                });

                thread.start();
            }
        });

        FloatingActionButton deleteFloatingButton = (FloatingActionButton) findViewById(R.id.fad);
        final NoteDetailActivity parentThis = this;
        deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View parentView = view;

                Thread thread = new Thread(new Runnable(){
                    public void run() {
                        try {
                            try {
                                TeamNotesController controller = new TeamNotesController(
                                        settings.getString(SETTING_NAME, ""),
                                        settings.getString(SETTING_SERVER, ""),
                                        settings.getString(SETTING_TOKEN, ""),
                                        settings.getString(SETTING_PWD, "")
                                );

                                String status = controller.getNoteStatus(mItem.id);

                                if(status.equals("available")) {
                                    if (!controller.deleteNote(mItem.id).equals("success")) {
                                        Snackbar.make(parentView, "Problem occurred while deleting the note", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(parentView, "Note deleted!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        navigateUpTo(new Intent(parentThis, NoteListActivity.class));
                                    }
                                } else if(status.equals("blocked") && unblockReady == false) {
                                    Snackbar.make(parentView, "The note is blocked. Tap DELETE button one more time to forcibly unblock it.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    unblockReady = true;
                                } else if(status.equals("blocked") && unblockReady == true) {
                                    Snackbar.make(parentView, "Warning: forcibly unlocking the note. Deleting the note.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    if (!controller.deleteNote(mItem.id).equals("success")) {
                                        Snackbar.make(parentView, "Problem occurred while deleting the note", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(parentView, "Note deleted!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        navigateUpTo(new Intent(parentThis, NoteListActivity.class));
                                    }
                                } else {
                                    Snackbar.make(parentView, "An error occurred while retrieving the status of the note. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            } catch (Exception ex) {
                                showNoticeDialog();
                            }
                        } catch (Exception e) {
                            showNoticeDialog();
                        }
                    }
                });

                thread.start();
            }
        });

        final FloatingActionButton editFloatingButton = (FloatingActionButton) findViewById(R.id.fak);
        editFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View parentView = view;
                //final EditText et = (EditText) findViewById(R.id.note_detail);

                Thread thread = new Thread(new Runnable(){
                    public void run() {

                        try {
                            try {
                                final EditText et = (EditText) parentThis.findViewById(R.id.note_detail);

                                TeamNotesController controller = new TeamNotesController(
                                        settings.getString(SETTING_NAME, ""),
                                        settings.getString(SETTING_SERVER, ""),
                                        settings.getString(SETTING_TOKEN, ""),
                                        settings.getString(SETTING_PWD, "")
                                );

                                String status = controller.getNoteStatus(mItem.id);

                                if(status.equals("available")) {
                                    if(controller.lockNote(mItem.id).equals("success")) {
                                        Snackbar.make(parentView, "The note is available. Switching to editing mode. Locking the noted from editing by others.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                subjectEditText.setEnabled(true);
                                                et.setEnabled(true);
                                                //editFloatingButton.setEnabled(false);
                                            }
                                        });
                                        //editFloatingButton.setEnabled(false);
                                    } else {
                                        Snackbar.make(parentView, "Error occurred while attempting to lock the note. Try again.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                } else if(status.equals("blocked") && unblockReady == false) {
                                    Snackbar.make(parentView, "The note is blocked. Tap EDIT button one more time to forcibly unblock it.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    unblockReady = true;
                                } else if(status.equals("blocked") && unblockReady == true) {
                                    Snackbar.make(parentView, "Warning: forcibly unlocking the note. Switching to editing mode.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            subjectEditText.setEnabled(true);
                                            et.setEnabled(true);
                                            //editFloatingButton.setEnabled(false);
                                        }
                                    });

                                } else {
                                    Snackbar.make(parentView, "An error occurred while retrieving the status of the note. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            } catch (Exception ex) {
                                showNoticeDialog();
                            }
                        } catch (Exception e) {
                            showNoticeDialog();
                        }
                    }
                });

                thread.start();

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID,
                    getIntent().getStringExtra(ARG_ITEM_ID));
            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, NoteListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoticeDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()){
                    new AlertDialog.Builder(NoteDetailActivity.this)
                            .setTitle(R.string.oops)
                            .setMessage(R.string.dialog_no_connection)
                            .setCancelable(false)
                            .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...
                                }
                            }).show();
                }
            }
        });
    }
}
