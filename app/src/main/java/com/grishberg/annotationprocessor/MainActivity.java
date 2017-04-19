package com.grishberg.annotationprocessor;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.grishberg.annotationprocessor.processor.CustomAnnotation;
import com.grishberg.annotationprocessor.processor.SubscribeTest;

@CustomAnnotation
@SubscribeTest
public class MainActivity extends AppCompatActivity {

    @CustomAnnotation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showAnnotationMessage();
    }

    private void showAnnotationMessage() {
        //GeneratedClassSubscribe
        GeneratedClass generatedClass = new GeneratedClass();
        String message = generatedClass.getMessage();
        // android.support.v7.app.AlertDialog
        new AlertDialog.Builder(this)
                .setPositiveButton("Ok", null)
                .setTitle("Annotation Processor Messages")
                .setMessage(message)
                .show();
    }

    @SubscribeTest
    public void onSomeState(SomeState state){

    }
}
