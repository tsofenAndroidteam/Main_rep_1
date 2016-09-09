package com.example.misho.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {
    private Spinner spinner1, spinner2;
    private UserSignUpTask mAuthTask = null;
    private String mEmail;
    private String mPassword;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView2;
    private EditText mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        mFirstNameView = (EditText) findViewById(R.id.firstNameEditText);
        mLastNameView = (EditText) findViewById(R.id.lastNameEditText);
        mPasswordView2 = (EditText) findViewById(R.id.passwordSignupEditText);
        mEmailView = (EditText) findViewById(R.id.emailEditText);
        spinner1 = (Spinner) findViewById(R.id.gender_signUpSpinner);
        spinner2 = (Spinner) findViewById(R.id.userType_signUpSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.UserType_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);
        mPasswordView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        mLoginFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);

    }

    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView2.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView2.getText().toString();
        String firstname = mFirstNameView.getText().toString();
        String lastname = mLastNameView.getText().toString();
        String gender = spinner1.getSelectedItem().toString();
        String userType = spinner2.getSelectedItem().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid2(password)) {
            TextInputLayout til = (TextInputLayout) findViewById(R.id.tilPassword);
            til.setErrorEnabled(true);
            //todo: fix errors
            til.setError("Password too short");
            focusView = mPasswordView2;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserSignUpTask(email, password, firstname, lastname, gender, userType);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            signUpButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    signUpButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signUpButton.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid2(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public class UserSignUpTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstname;
        private final String mLastname;
        private final String mGender;
        private final String mUsertype;

        public UserSignUpTask(String mEmail, String mPassword, String mFirstname, String mLastname, String mGender, String mUsertype) {
            this.mEmail = mEmail;
            this.mPassword = mPassword;
            this.mFirstname = mFirstname;
            this.mLastname = mLastname;
            this.mGender = mGender;
            this.mUsertype = mUsertype;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            if (params.length == 0) {

                return null;
            }
            Log.v("connect", "CONNECTED");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String serverJsonStr = null;

            try {
                final String Server_BASE_URL =
                        "http://52.29.110.203:8080/LibArab/SignUp/dosignup?";

                final String USER_PARAM = "username";
                final String PASS_PARAM = "password";
                final String FIRSTNAME_PARAM = "firstname";
                final String LASTNAME_PARAM = "lastname";
                final String GENDER_PARAM = "gender";
                final String USERTYPE_PARAM = "usertype";
                Uri builtUri = Uri.parse(Server_BASE_URL).buildUpon()
                        .appendQueryParameter(USER_PARAM, mEmail)
                        .appendQueryParameter(PASS_PARAM, mPassword)
                        .build();
                if (!mFirstname.isEmpty()) {
                    builtUri = Uri.parse(builtUri.toString()).buildUpon()
                            .appendQueryParameter(FIRSTNAME_PARAM, mFirstname)
                            .build();
                }
                if (!mLastname.isEmpty()) {
                    builtUri = Uri.parse(builtUri.toString()).buildUpon()
                            .appendQueryParameter(LASTNAME_PARAM, mLastname)
                            .build();
                }
                if (mGender.equals("Gender")) {
                    builtUri = Uri.parse(builtUri.toString()).buildUpon()
                            .appendQueryParameter(GENDER_PARAM, mGender)
                            .build();
                }
                if (mUsertype.equals("Account type")) {
                    builtUri = Uri.parse(builtUri.toString()).buildUpon()
                            .appendQueryParameter(USERTYPE_PARAM, mUsertype)
                            .build();
                }
                URL url = new URL(builtUri.toString());
                Log.v("URL", builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                serverJsonStr = buffer.toString();
                Log.d("PROBLEM", serverJsonStr);

            } catch (IOException e) {
                Log.e("LOGE", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("LOGE", "Error closing stream", e);
                    }
                }
            }


            JSONObject serverJson = null;
            try {
                serverJson = new JSONObject(serverJsonStr);
                return serverJson;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
