package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//класс для авторизации
public class SignInActivity extends AppCompatActivity {

    //переменная для вывода резульата в лог
    private static final String TAG = "SignInActivity";

    private FirebaseAuth auth;

    //переменные для авторизации
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText nameEditText;
    private TextView toggleLoginSingUpTextView;
    private Button loginSingUpButton;

    //переменная для переключения логина и регистрации
    private Boolean loginModeActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        //связываем
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        toggleLoginSingUpTextView = findViewById(R.id.toggleLoginSingUpTextView);
        loginSingUpButton = findViewById(R.id.loginSingUpButton);

        //делаем слушатель для кнопки
        loginSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //передаем значения строки емайла и пароля
                loginSingUpUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        });
    }

    //метод для создания новой учетной записи
    private void loginSingUpUser(String email, String password) {

        //делаем проверку на то в каком моде регистрации или лога пользщователь
        if (loginModeActive) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                //если ок делаем переход в нвоое активити
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
//                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    });
        } else {

            //если не ок - ввыводим лог
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                //если ок делаем переход в нвоое активити
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
//                            updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                        }
                    });

        }

    }

    //метод для переключения логина и регистрации по клику на текст
    public void toggleLoginMode(View view) {

        if (loginModeActive) {
            loginModeActive = false;
            loginSingUpButton.setText("Зарегистрироваться");
            toggleLoginSingUpTextView.setText("Войти");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            loginSingUpButton.setText("Войти");
            toggleLoginSingUpTextView.setText("Зарегистрироваться");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }
}