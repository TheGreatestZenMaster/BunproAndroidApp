package bunpro.jp.bunproapp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Review extends RealmObject {
    @PrimaryKey
    public int id;
    public int user_id;
    public int study_question_id;
    public int grammar_point_id;
    public int times_correct;
    public int times_incorrect;
    public int streak;
    public String next_review;
    public String created_at, updated_at;
    public boolean complete;
    public String last_studied_at;
    public boolean was_correct;
    public boolean self_study;
    public int review_misses;
    public RealmList<History> history;
    public RealmList<Integer> missed_question_ids;
    public RealmList<Integer> studied_question_ids;
    public String review_type;

    public Review() {

    }

    public long getRemainingHoursBeforeReview() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date reviewDate = dateFormat.parse(next_review);
            long dateDiff = reviewDate.getTime() - currentDate.getTime();
            return dateDiff / (60 * 60 * 1000);
        } catch (ParseException e) {
            return 1000; // Returning a long we won't care about because it is too big
        }
    }

    public static Comparator<Review> IdComparator = new Comparator<Review>() {
        @Override
        public int compare(Review point1, Review point2) {
            if (point1.id > point2.id) {
                return 1;
            } else {
                return -1;
            }
        }
    };
}
