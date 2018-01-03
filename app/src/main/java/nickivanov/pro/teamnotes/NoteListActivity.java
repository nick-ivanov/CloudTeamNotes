package nickivanov.pro.teamnotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class NoteListActivity extends AppCompatActivity {
    private final String TAG = "NoteListActivity";


    public static final String PREFS_NAME = "TeamNotesPrefs";
    public static final String SETTING_NAME = "setting_name";
    public static final String SETTING_SERVER = "setting_server";
    public static final String SETTING_TOKEN = "setting_token";
    public static final String SETTING_PWD = "setting_pwd";

    private SharedPreferences settings;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = this.getSharedPreferences(PREFS_NAME, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final Activity activity = this;

        FloatingActionButton addNoteButton = (FloatingActionButton) findViewById(R.id.fam2);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Adding new note...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//                SecureTeamNotesModel.SecureTeamNote item = new SecureTeamNotesModel.SecureTeamNote("s11", "kaka", "sraka");
//                SecureTeamNotesModel.ITEMS.add(item);
//                SecureTeamNotesModel.ITEM_MAP.put("s11", item);
//
//
//                View recyclerView = findViewById(R.id.note_list);
//                assert recyclerView != null;
//                setupRecyclerView((RecyclerView) recyclerView);
//
//                try {
//                    Log.d(TAG, controller.getNoteList().toString());
//                } catch (Exception ex) {
//                    Log.d(TAG, ex.getMessage());
//                    System.exit(1);
//                }

                final View tmpview = view;


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

                                final String newid = controller.addNote("New Note", "New note body...");

                                if(newid == null) {
                                    Snackbar.make(tmpview, "Error occurred while adding a note", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                                final ArrayList<String> ids = controller.getNoteIdList();
                                final ArrayList<String> subjects = controller.getNoteSubjectList();
                                final ArrayList<String> bodies = controller.getNoteBodyList();


//                                CryptoHelper ch = new CryptoHelper("pass");
//                                String encStr = ch.encrypt("hello world");
//                                String decStr = ch.decrypt(encStr);
//
//                                Log.d(TAG, encStr);
//                                Log.d(TAG, decStr);
//
//                                String url = "http://blahblahblah.com/blah.php?key=BlahBlahKey&msg="+encStr;
//                                Log.d(TAG, java.net.URLEncoder.encode(url, "UTF-8"));

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        View recyclerView = findViewById(R.id.note_list);
                                        assert recyclerView != null;

                                        SecureTeamNotesModel.ITEMS.clear();
                                        SecureTeamNotesModel.ITEM_MAP.clear();

                                        for(int i = 0; i < ids.size(); i++) {
                                            SecureTeamNotesModel.SecureTeamNote item = new SecureTeamNotesModel.SecureTeamNote(ids.get(i), subjects.get(i), bodies.get(i));
                                            SecureTeamNotesModel.ITEMS.add(item);
                                            SecureTeamNotesModel.ITEM_MAP.put(ids.get(i), item);
                                        }

                                        setupRecyclerView((RecyclerView) recyclerView);
                                    }
                                });

                            } catch (Exception ex) {
                                showNoticeDialog();
                                ex.printStackTrace();
                            }
                        } catch (Exception e) {
                            showNoticeDialog();
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });


        FloatingActionButton reloadNotesButton = (FloatingActionButton) findViewById(R.id.fam1);
        reloadNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });

        refreshList();

        if (findViewById(R.id.note_detail_container) != null) {
            mTwoPane = true;
        }
    }

    private void refreshList() {
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

                        final ArrayList<String> ids = controller.getNoteIdList();
                        final ArrayList<String> subjects = controller.getNoteSubjectList();
                        final ArrayList<String> bodies = controller.getNoteBodyList();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                View recyclerView = findViewById(R.id.note_list);
                                assert recyclerView != null;

                                SecureTeamNotesModel.ITEMS.clear();
                                SecureTeamNotesModel.ITEM_MAP.clear();

                                for(int i = 0; i < ids.size(); i++) {
                                    SecureTeamNotesModel.SecureTeamNote item = new SecureTeamNotesModel.SecureTeamNote(ids.get(i), subjects.get(i), bodies.get(i));
                                    SecureTeamNotesModel.ITEMS.add(item);
                                    SecureTeamNotesModel.ITEM_MAP.put(ids.get(i), item);
                                }

                                setupRecyclerView((RecyclerView) recyclerView);
                            }
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showNoticeDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoticeDialog();
                }
            }
        });

        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.securenotes_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void showNoticeDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()){
                    new AlertDialog.Builder(NoteListActivity.this)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getTitle().toString()) {
            case "Settings":
                SettingsDialogFragment fragment = new SettingsDialogFragment();
                fragment.show(getFragmentManager(), "hello");
                break;

//            case "Easter Egg":
//                Toast.makeText(this, "Have a good day!", Toast.LENGTH_LONG).show();
//                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(SecureTeamNotesModel.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<SecureTeamNotesModel.SecureTeamNote> mValues;

        public SimpleItemRecyclerViewAdapter(List<SecureTeamNotesModel.SecureTeamNote> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mSubjectView.setText(mValues.get(position).subject);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(NoteDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        NoteDetailFragment fragment = new NoteDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.note_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, NoteDetailActivity.class);
                        intent.putExtra(NoteDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mSubjectView;
            public SecureTeamNotesModel.SecureTeamNote mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
                mSubjectView = (TextView) view.findViewById(R.id.subject);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSubjectView.getText() + "'";
            }
        }
    }
}
