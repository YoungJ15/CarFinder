package com.josermando.apps.carfinder;

import android.net.Uri;
import android.util.Log;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void uri_isCorrect() throws  Exception{
        final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/";
        final String API_KEY = "api_key";
        String apiID = "y6hazeyr3t7tdhnpngjzy4rk";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(API_KEY,apiID).build();
        URL url = new URL(builtUri.toString());
        assertEquals("http://api.edmunds.com/api/vehicle/v2/",url);
    }

}