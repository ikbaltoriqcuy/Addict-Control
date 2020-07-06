package org.d3ifcool.addictcontrol.Database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cool on 4/27/2018.
 */

public class TimeWorkContract {
    public static final String CONTENT_AUTHORITY = "org.d3ifcool.timework" ;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNT = "ACCOUNT";
    public static final String PATH_QUOTES = "quotes";
    public static final String PATH_SCHEDULE = "schedules";

    public static final class AccountEntry implements BaseColumns {
        public static final Uri CONTENT_URI= Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_ACCOUNT );

        public static final String TABLE_ACCOUNT = "account";

        public static final String KEY_USERNAME = "username";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_TYPE_ACCOUNT = "type_account";
        public static final String KEY_MY_PASSWORD = "my_password";

    }

    public static final class QuotesEntry implements BaseColumns {
        public static final Uri CONTENT_URI= Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_QUOTES );

        public static final String TABLE_QUOTES = "quotes";

        public static final String KEY_NAME_QUOTE = "name_quote";

    }

    public static final class ScheduleEntry implements BaseColumns {
        public static final Uri CONTENT_URI= Uri.withAppendedPath(
                BASE_CONTENT_URI, PATH_SCHEDULE );

        public static final String TABLE_SCHEDULE = "schedule";

        public static final String _ID = BaseColumns._ID;

        public static final String KEY_NAME = "name_schedule";
        public static final String KEY_DAY = "day";
        public static final String KEY_START_TIME = "start_time";
        public static final String KEY_END_TIME = "end_time";
        public static final String KEY_ACTIVE = "active";

    }
}
