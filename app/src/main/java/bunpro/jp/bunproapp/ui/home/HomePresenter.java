package bunpro.jp.bunproapp.ui.home;

import android.util.Log;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bunpro.jp.bunproapp.interactors.ExampleSentenceInteractor;
import bunpro.jp.bunproapp.interactors.ReviewInteractor;
import bunpro.jp.bunproapp.interactors.SupplementalLinkInteractor;
import bunpro.jp.bunproapp.models.ExampleSentence;
import bunpro.jp.bunproapp.models.GrammarPoint;
import bunpro.jp.bunproapp.models.Review;
import bunpro.jp.bunproapp.models.SupplementalLink;
import bunpro.jp.bunproapp.service.ApiService;
import bunpro.jp.bunproapp.service.JsonParser;
import bunpro.jp.bunproapp.utils.SimpleCallbackListener;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View homeView;
    private SupplementalLinkInteractor supplementalLinkInteractor;
    private ExampleSentenceInteractor exampleSentenceInteractor;
    private ReviewInteractor reviewInteractor;

    public HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
        this.supplementalLinkInteractor = new SupplementalLinkInteractor(this.homeView.getContext());
        this.exampleSentenceInteractor = new ExampleSentenceInteractor(this.homeView.getContext());
        this.reviewInteractor = new ReviewInteractor(this.homeView.getContext());
    }

    public void stop() {
        this.supplementalLinkInteractor.close();
        this.exampleSentenceInteractor.close();
        this.reviewInteractor.close();
    }

    public void fetchData() {
        // Attempt to fetch reviews
        reviewInteractor.fetchReviews(new SimpleCallbackListener() {
            @Override
            public void success() {
                // Attempt to fetch grammar points
                fetchGrammarPoints(new SimpleCallbackListener() {
                    @Override
                    public void success() {
                        // Attempt to fetch example sentences
                        exampleSentenceInteractor.fetchExampleSentences(new SimpleCallbackListener() {
                            @Override
                            public void success() {
                                // Attempt to fetch supplemental links
                                supplementalLinkInteractor.fetchSupplementalLinks(new SimpleCallbackListener() {
                                    @Override
                                    public void success() {
                                    }
                                    @Override
                                    public void error(String errorMessage) {
                                        Log.e("Data retrieval error", errorMessage);
                                    }
                                });
                            }
                            @Override
                            public void error(String errorMessage) {
                                Log.e("Data retrieval error", errorMessage);
                            }
                        });
                        // Workaround for /user/progress v3 endpoint not working
                        if (GrammarPoint.getN2GrammarPointsTotal().size() == 0) {
                            countProgress(reviewInteractor.loadReviews().findAll());
//                            Fragment currentFragment = fragNavController.getCurrentFrag();
//                            if (currentFragment instanceof StatusFragment) {
////                                ((StatusFragmentDeprecated)currentFragment).refreshStatus();
//                            }
                        }
                    }
                    @Override
                    public void error(String errorMessage) {
                        Log.e("Data retrieval error", errorMessage);
                    }
                });
                // Try to update status fragment with review count
                //Fragment currentFragment = fragNavController.getCurrentFrag();
//                if (currentFragment instanceof StatusFragmentDeprecated) {
//                    ((StatusFragmentDeprecated)currentFragment).calculateReviewsNumber();
//                }
            }
            @Override
            public void error(String errorMessage) {
                Log.e("Data retrieval error", errorMessage);
            }
        });
    }

    private void fetchGrammarPoints(SimpleCallbackListener callback) {

        ApiService apiService = new ApiService(homeView.getContext());
        apiService.getGrammarPoints(new ApiService.ApiCallbackListener() {
            @Override
            public void success(JSONObject jsonObject) {
                Log.w("API Format changed", "JSONObject obtained instead of an JSONArray ! (Grammar points)");
                callback.error("Grammar points API response format changed !");
            }

            @Override
            public void successAsJSONArray(JSONArray jsonArray) {

                List<GrammarPoint> grammarPoints = JsonParser.getInstance(homeView.getContext()).parseGrammarPoints(jsonArray);
                GrammarPoint.setGrammarPointList(grammarPoints);
                callback.success();
            }

            @Override
            public void error(ANError anError) {
                Log.e("Error", anError.getErrorDetail());
                callback.error(anError.getErrorDetail());
            }
        });
    }

    /**
     * Temporary workaround for the non working /user/progress v3 endpoint
     */
    public void countProgress(List<Review> reviews) {
        List<Integer> n2GrammarPointsLearned = new ArrayList<>();
        List<Integer> n1GrammarPointsLearned = new ArrayList<>();
        List<Integer> n2GrammarPointsTotal = new ArrayList<>();
        List<Integer> n1GrammarPointsTotal = new ArrayList<>();
        List<GrammarPoint> n2GrammarPoints = new ArrayList<>();
        List<GrammarPoint> n1GrammarPoints = new ArrayList<>();

        for (GrammarPoint grammarPointExample : GrammarPoint.getGrammarPointList()) {
            if (grammarPointExample.level.equals("JLPT2")) {
                if (!n2GrammarPointsTotal.contains(grammarPointExample.id)) {
                    n2GrammarPointsTotal.add(grammarPointExample.id);
                }
                n2GrammarPoints.add(grammarPointExample);
            } else if (grammarPointExample.level.equals("JLPT1")) {
                if (!n1GrammarPointsTotal.contains(grammarPointExample.id)) {
                    n1GrammarPointsTotal.add(grammarPointExample.id);
                }
                n1GrammarPoints.add(grammarPointExample);
            }
        }
        for (Review review : reviews) {
            for (GrammarPoint grammarConcernedByReview : n2GrammarPoints) {
                if (review.grammar_point_id == grammarConcernedByReview.id) {
                    if (review.times_correct > 0 && n2GrammarPointsLearned.contains(review.id)) {
                        n2GrammarPointsLearned.add(review.id);
                    }
                }
            }
            for (GrammarPoint grammarConcernedByReview : n1GrammarPoints) {
                if (review.grammar_point_id == grammarConcernedByReview.id) {
                    if (review.times_correct > 0 && n1GrammarPointsLearned.contains(review.id)) {
                        n1GrammarPointsLearned.add(review.id);
                    }
                }
            }
        }
        GrammarPoint.setN1GrammarPointsLearned(n1GrammarPointsLearned);
        GrammarPoint.setN2GrammarPointsLearned(n2GrammarPointsLearned);
        GrammarPoint.setN1GrammarPointsTotal(n1GrammarPointsTotal);
        GrammarPoint.setN2GrammarPointsTotal(n2GrammarPointsTotal);
    }
}
