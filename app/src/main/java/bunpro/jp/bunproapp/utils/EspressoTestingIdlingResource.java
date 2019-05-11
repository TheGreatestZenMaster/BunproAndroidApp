package bunpro.jp.bunproapp.utils;

import java.util.HashMap;
import java.util.Map;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoTestingIdlingResource {
    private static Map<String, CountingIdlingResource> countingIdlingResources =
            new HashMap<String, CountingIdlingResource>();

    public static void increment(String key) {
        if (!countingIdlingResources.containsKey(key)) {
            countingIdlingResources.put(key, new CountingIdlingResource(key));
        }
        countingIdlingResources.get(key).increment();
    }

    public static void decrement(String key) {
        if (countingIdlingResources.containsKey(key) && !countingIdlingResources.get(key).isIdleNow()) {
            countingIdlingResources.get(key).decrement();
        }
    }

    public static IdlingResource getIdlingResource(String key) {
        if (!countingIdlingResources.containsKey(key)) {
            countingIdlingResources.put(key, new CountingIdlingResource(key));
        }
        return countingIdlingResources.get(key);
    }
}
