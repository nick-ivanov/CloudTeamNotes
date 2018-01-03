package nickivanov.pro.teamnotes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecureTeamNotesModel {

    public static final List<SecureTeamNote> ITEMS = new ArrayList<SecureTeamNote>();

    public SecureTeamNotesModel() {
    }

    public static final Map<String, SecureTeamNote> ITEM_MAP = new HashMap<String, SecureTeamNote>();

//    private static final int COUNT = 10;
//
//    static {
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    public static void addItem(SecureTeamNote item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

//    private static SecureTeamNote createDummyItem(int position) {
//        return new SecureTeamNote(String.valueOf(position), "Note " + position, makeDetails(position));
//    }

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Secure Note #: ").append(position);
//        return builder.toString();
//    }

    public static class SecureTeamNote {
        public final String id;
        public final String subject;
        public final String body;

        public SecureTeamNote(String id, String subject, String body) {
            this.id = id;
            this.subject = subject;
            this.body = body;
        }

        @Override
        public String toString() {
            return subject;
        }
    }
}
