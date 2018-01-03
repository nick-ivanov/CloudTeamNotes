package nickivanov.pro.teamnotes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;



public class TeamNotesController {
    private static String TAG = "TeamNotesController";
    private final String NOTEIDLIST_ENDPOINT = "/stn_endpoint_noteidlist.php";
    private final String NOTESUBJECTLIST_ENDPOINT = "/stn_endpoint_notesubjectlist.php";
    private final String NOTEBODYLIST_ENDPOINT = "/stn_endpoint_notebodylist.php";
    private final String ADDNOTE_ENDPOINT = "/stn_endpoint_addnote.php";
    private final String EDITNOTE_ENDPOINT = "/stn_endpoint_editnote.php";
    private final String DELETENOTE_ENDPOINT = "/stn_endpoint_deletenote.php";
    private final String NOTESTATUS_ENDPOINT = "/stn_endpoint_notestatus.php";
    private final String LOCKNOTE_ENDPOINT = "/stn_endpoint_locknote.php";
    private final String UNLOCKNOTE_ENDPOINT = "/stn_endpoint_unlocknote.php";

    private String name;
    private String server;
    private String key;
    private String pwd;

    private ArrayList<Note> notes = new ArrayList<>();

    public TeamNotesController() {
    }

    public TeamNotesController(String name, String server, String key, String pwd) {
        this.name = name;
        this.server = server;
        this.key = key;
        this.pwd = pwd;
    }

    private class Note {
        private String noteId;
        private String noteTitle;
        private String noteBody;

        public Note(String noteId, String noteTitle, String noteBody) {
            this.noteId = noteId;
            this.noteTitle = noteTitle;
            this.noteBody = noteBody;
        }


        public Note() {}

        public String getNoteId() {
            return noteId;
        }

        public String getNoteTitle() {
            return noteTitle;
        }

        public String getNoteBody() {
            return noteBody;
        }

        public void setNoteId(String noteId) {
            this.noteId = noteId;
        }

        public void setNoteTitle(String noteTitle) {
            this.noteTitle = noteTitle;
        }

        public void setNoteBody(String noteBody) {
            this.noteBody = noteBody;
        }
    }

    private ArrayList<String> explodeArray(String src) {
        ArrayList<String> arrayList = new ArrayList<>();

        boolean inside1 = false;
        boolean inside2 = false;
        boolean escapeFlag = false;
        int j = 0;

        for(int i = 0; i < src.length()-1; i++) {
            if(src.charAt(i) == '\\' &&
                    (src.charAt(i+1) == '[' || src.charAt(i+1) == ']'
                            || src.charAt(i+1) == '{' || src.charAt(i+1) == '}')) {
                escapeFlag = true;
                continue;
            }

            if(inside1) {
                if(src.substring(i, i+2).equals("}}") && !escapeFlag) {
                    break;
                }

                if(inside2) {
                    if(src.charAt(i) == ']' && !escapeFlag) {
                        inside2 = false;
                        j++;
                    } else {
                        if(arrayList.size() == j) arrayList.add("");
                        arrayList.set(j, arrayList.get(j) + src.charAt(i));
                    }
                } else {
                    if(src.charAt(i) == '[' && !escapeFlag) {
                        inside2 = true;
                    }
                }

                if(escapeFlag) escapeFlag = false;
                continue;
            }

            if(src.substring(i, i+2).equals("{{") && !escapeFlag) {
                inside1 = true;
            }

            if(escapeFlag) escapeFlag = false;
        }


        return arrayList;
    }

    public ArrayList<String> decryptArray(ArrayList<String> arr) {
        CryptoHelper ch = new CryptoHelper(pwd);
        ArrayList<String> arr1 = new ArrayList<>();

        for(int i = 0; i < arr.size(); i++) {
            arr1.add(ch.decrypt(arr.get(i)));
        }

        return arr1;

    }

    public String addNote(String subject, String body) {
        String encodedKey = "", encodedSubject = "", encodedBody = "";

        try {
            CryptoHelper ch = new CryptoHelper(pwd);
            encodedKey = java.net.URLEncoder.encode(key, "UTF-8");
            encodedSubject = java.net.URLEncoder.encode(ch.encrypt(subject), "UTF-8");
            encodedBody = java.net.URLEncoder.encode(ch.encrypt(body), "UTF-8");
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        String url = server + ADDNOTE_ENDPOINT + "?key=" + encodedKey + "&subject=" + encodedSubject + "&body=" + encodedBody;

        try {
            String res = getHtml(url);
            if(explodeArray(res).get(0).equals("success")) {
                return explodeArray(res).get(1);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        return null;
    }

    public String editNote(String id, String subject, String body) {
        String encodedKey = "", encodedSubject = "", encodedBody = "", encodedId = "";

        try {
            CryptoHelper ch = new CryptoHelper(pwd);

            encodedKey = java.net.URLEncoder.encode(key, "UTF-8");
            encodedSubject = java.net.URLEncoder.encode(ch.encrypt(subject), "UTF-8");
            encodedBody = java.net.URLEncoder.encode(ch.encrypt(body), "UTF-8");
            encodedId = java.net.URLEncoder.encode(id, "UTF-8");

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        String url = server + EDITNOTE_ENDPOINT + "?key=" + encodedKey + "&id=" + encodedId + "&subject=" + encodedSubject + "&body=" + encodedBody;

        try {
            String res = getHtml(url);
            if(explodeArray(res).get(0).equals("success")) {
                return explodeArray(res).get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        return null;
    }

    public String deleteNote(String id) {
        String encodedKey = "", encodedSubject = "", encodedBody = "", encodedId = "";

        try {
            encodedKey = java.net.URLEncoder.encode(key, "UTF-8");
            encodedId = java.net.URLEncoder.encode(id, "UTF-8");

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        String url = server + DELETENOTE_ENDPOINT + "?key=" + encodedKey + "&id=" + encodedId;

        try {
            String res = getHtml(url);
            if(explodeArray(res).get(0).equals("success")) {
                return explodeArray(res).get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        return null;
    }

    public String lockNote(String id) {
        String encodedKey = "", encodedSubject = "", encodedBody = "", encodedId = "";

        try {
            encodedKey = java.net.URLEncoder.encode(key, "UTF-8");
            encodedId = java.net.URLEncoder.encode(id, "UTF-8");
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        String url = server + LOCKNOTE_ENDPOINT + "?key=" + encodedKey + "&id=" + encodedId;

        try {
            String res = getHtml(url);
            if(explodeArray(res).get(0).equals("success")) {
                return explodeArray(res).get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        return null;
    }

    public String unlockNote(String id) {
        String encodedKey = "", encodedSubject = "", encodedBody = "", encodedId = "";

        try {
            encodedKey = java.net.URLEncoder.encode(key, "UTF-8");
            encodedId = java.net.URLEncoder.encode(id, "UTF-8");
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        String url = server + UNLOCKNOTE_ENDPOINT + "?key=" + encodedKey + "&id=" + encodedId;

        try {
            String res = getHtml(url);
            if(explodeArray(res).get(0).equals("success")) {
                return explodeArray(res).get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            System.exit(1);
        }

        return null;
    }

    public ArrayList<String> getNoteIdList() throws Exception {
        return explodeArray(getHtml(server + NOTEIDLIST_ENDPOINT + "?key=" + key));
    }

    public ArrayList<String> getNoteSubjectList() throws Exception {
        return decryptArray(explodeArray(getHtml(server + NOTESUBJECTLIST_ENDPOINT + "?key=" + key)));
    }

    public ArrayList<String> getNoteBodyList() throws Exception {
        return decryptArray(explodeArray(getHtml(server + NOTEBODYLIST_ENDPOINT + "?key=" + key)));
    }

    public String getNoteStatus(String id) throws Exception {
        String status = explodeArray(getHtml(server + NOTESTATUS_ENDPOINT + "?key=" + key + "&id=" + id)).get(0);
        return status;
    }

    public String getHtml(String url) throws Exception {
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }



}
