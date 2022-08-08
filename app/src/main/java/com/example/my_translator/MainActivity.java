package com.example.my_translator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.example.my_translator.Operations.BCPCode;
import com.example.my_translator.Operations.CodeArrayList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import static com.example.my_translator.R.color.purple_700;

public class MainActivity extends AppCompatActivity {
    SliderView sliderView;
    int images[]={R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,
            R.drawable.img8,R.drawable.img9};
    SliderAdp sliderAdp;
    Button capture,Tranfile,BarCode,about,Text,Share;
    SweetAlertDialog pDialog;
    TextRecognizer recognizer;
    InputImage inputImage;
    ArrayList<String> arrayList;
    int BarCodeValue=0;
    String path;
    String fileContent="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList=new CodeArrayList().addCodes();


        sliderView=(SliderView)findViewById(R.id.sl1);
        sliderAdp=new SliderAdp(images);
        sliderView.setSliderAdapter(sliderAdp);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();


        capture=(Button)findViewById(R.id.captureImage);
        Tranfile=(Button)findViewById(R.id.importImage);
        BarCode=(Button)findViewById(R.id.tranfile);
        Text=(Button)findViewById(R.id.trantext);
        about=(Button)findViewById(R.id.conver);
        Share=(Button)findViewById(R.id.shares);

        // Permission for internal Storage
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
   // Text To Text Converting
        Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Translate Text");
                View customLayout= LayoutInflater.from(MainActivity.this).inflate(R.layout.textconvert, null);
                builder.setView(customLayout).setCancelable(false);


                SearchableSpinner searchableSpinner2=(SearchableSpinner)customLayout.findViewById(R.id.to);

                final String[] to = {""};
                Button button=(Button)customLayout.findViewById(R.id.tranbtn);
                Button button2=(Button)customLayout.findViewById(R.id.analybtn);
                EditText editText=(EditText)customLayout.findViewById(R.id.body);
                TextView textView=(TextView)customLayout.findViewById(R.id.resulttext) ;

                searchableSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                      to[0] =searchableSpinner2.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

button2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (!editText.getText().toString().matches(""))
        {
            LanguageIdentifier languageIdentifier =
                    LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(editText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onSuccess(String languageCode) {
                            if (languageCode.equals("und")) {
                                textView.setTextColor(Color.RED);
                                textView.setText("Can't identify The Source language.");
                            } else {
                                Locale loc = new Locale(languageCode);
                                String source = loc.getDisplayLanguage();
                                textView.setTextColor(purple_700);
                                textView.setText("Language Type: "+source+"\n\nSource Language: "+ editText.getText().toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    textView.setTextColor(Color.RED);
                    textView.setText("Can't identify The Source language. Something Went Wrong");
                }
            });
        }
        else
        {
            textView.setTextColor(Color.RED);
            textView.setText("Please! Enter  Source Language To Analyse");
        }
    }
});

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                         if (!to[0].equals("Select Target Language")) {
                             // progress bar creation
                             myprogbar();
                             LanguageIdentifier languageIdentifier =
                                     LanguageIdentification.getClient();
                             languageIdentifier.identifyLanguage(editText.getText().toString())
                                     .addOnSuccessListener(
                                             new OnSuccessListener<String>() {
                                                 @Override
                                                 public void onSuccess(@Nullable String languageCode) {
                                                     if (languageCode.equals("und")) {
                                                         textView.setTextColor(Color.RED);
                                                         textView.setText("Can't identify language.");
                                                     }
                                                     else if(!arrayList.contains(languageCode))
                                                     {
                                                         textView.setTextColor(Color.RED);
                                                         textView.setText("Translation Not Support to the Source Language");
                                                     }
                                                     else {
                                                         Locale loc = new Locale(languageCode);
                                                         String source = loc.getDisplayLanguage();

                                                         // Create an  translator:

                        TranslatorOptions options =
                                new TranslatorOptions.Builder()
                                        .setSourceLanguage(TranslateLanguage.fromLanguageTag(languageCode))
                                        .setTargetLanguage(TranslateLanguage.fromLanguageTag(new BCPCode().getBcpCode(to[0])))
                                        .build();
                        final Translator myTranslator =
                                Translation.getClient(options);
                        // download model if required
                        pDialog.setTitleText("Translate Model Downloading....");
                        pDialog.show();
                        myTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            // now model ready to use
                                pDialog.dismissWithAnimation();
                                pDialog.setTitleText("Language Converting....");
                                pDialog.show();
                                myTranslator.translate(editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onSuccess(String s) {
                                        pDialog.dismissWithAnimation();
                                        textView.setTextColor(purple_700);
                                        textView.setText("Source Language: "+source+"\nTarget Language: "+to[0]+"\n\nResult :\n"+s);
                                        myTranslator. close();
                                        languageIdentifier.close();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pDialog.dismissWithAnimation();
                                        textView.setText("Went Wrong"+e.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                textView.setText("Went Wrong model not download");
                                pDialog.dismissWithAnimation();
                            }
                        });


                                                     }
                                                 }
                                             })
                                     .addOnFailureListener(
                                             new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     // Model couldn’t be loaded or other internal error.
                                                     // ...
                                                     pDialog.dismissWithAnimation();
                                                 }
                                             });

                         }
                         else
                         {
                             textView.setTextColor(Color.RED);
                             textView.setText("Please! Select Target Language to Translate");

                         }

                    }
                });


                builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //startActivity(new Intent(MainActivity.this,MainActivity.class));
                        //finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });



        capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BarCodeValue=0;
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, capture);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.camerapopupmenu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.LL:
                                recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                                break;
                            case R.id.IL:
                                recognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                                break;
                            case R.id.JL:
                                recognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                                break;
                            case R.id.CL:
                                recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                                break;
                            case R.id.KL:
                                recognizer =TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                                break;
                            default:
                                recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                        }
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(MainActivity.this);
                        return true;
                    }
                });

               popup.show();
            }
        });
        Tranfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;
                properties.show_hidden_files = true;

                FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
                dialog.setTitle("Select a File");
                final String[] re = {""};

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        for (String et:files)
                        {
                            re[0] = re[0] +et;
                        }
                        File file=new File(re[0]);
                        path=re[0];
                        String filename=file.getName();
                        int filelength=filename.length();
                        String fileexe=String.valueOf(filename.charAt(filelength-3))+String.valueOf(filename.charAt(filelength-2))+String.valueOf(filename.charAt(filelength-1));

                        if (fileexe.equals("txt"))
                        {
                            StringBuilder text = new StringBuilder();
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                String line;

                                while ((line = br.readLine()) != null) {
                                    text.append(line);
                                    text.append('\n');
                                }
                                br.close();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            openMyFilePopup(filename,text.toString());
                        }
                        else if (fileexe.equals("pdf"))
                        {
                            extractPDF();
                            openMyFilePopup(filename,fileContent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please Select pdf or text Format File", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                dialog.show();
            }
        });


        // Bar Code Button

        BarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarCodeValue=1;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
            }
        });

        //share Application

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,IntroductionActivity.class));
                finish();
            }
        });
    }

    private void myprogbar()
    {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
    }

    private void cameraPopUp(Bitmap bitmap)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View customLayout= LayoutInflater.from(MainActivity.this).inflate(R.layout.camera_popup, null);
        builder.setView(customLayout).setCancelable(false);
        ImageView imageView=(ImageView)customLayout.findViewById(R.id.imgReview1);
        TextView extractedText=(TextView)customLayout.findViewById(R.id.resulttext);
        TextView sourceLang=(TextView)customLayout.findViewById(R.id.txt4);

        Button button=(Button)customLayout.findViewById(R.id.tranbtn);
        SearchableSpinner searchableSpinner=(SearchableSpinner)customLayout.findViewById(R.id.to);
        TextView resultText=(TextView)customLayout.findViewById(R.id.convertedresulttext);
        final String[] targetLang = {""};

        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                targetLang[0] =searchableSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImage(bitmap);
            }
        });
       inputImage = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result =
              recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                extractedText.setTextColor(purple_700);
                                extractedText.setText(visionText.getText());

                                LanguageIdentifier languageIdentifier =
                                        LanguageIdentification.getClient();
                                languageIdentifier.identifyLanguage(extractedText.getText().toString())
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @SuppressLint("ResourceAsColor")
                                                    @Override
                                                    public void onSuccess(@Nullable String languageCode) {
                                                        if (languageCode.equals("und")) {
                                                           sourceLang.setTextColor(Color.RED);
                                                            sourceLang.setText("Source Language Type: Can't identify language");
                                                        }

                                                        else {
                                                            Locale loc = new Locale(languageCode);
                                                            String source = loc.getDisplayLanguage();
                                                            sourceLang.setTextColor(purple_700);
                                                            sourceLang.setText("Source Language Type: "+source);
                                                            button.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    if (!targetLang[0].equals("Select Target Language"))
                                                                    {
                                                                        // progress bar creation
                                                                        myprogbar();
                                                                        if(arrayList.contains(languageCode))
                                                                        {
                                                                            TranslatorOptions options =
                                                                                    new TranslatorOptions.Builder()
                                                                                            .setSourceLanguage(TranslateLanguage.fromLanguageTag(languageCode))
                                                                                            .setTargetLanguage(TranslateLanguage.fromLanguageTag(new BCPCode().getBcpCode(targetLang[0])))
                                                                                            .build();
                                                                            final Translator myTranslator =
                                                                                    Translation.getClient(options);
                                                                            pDialog.setTitleText("Translate Model Downloading....");
                                                                            pDialog.show();
                                                                            myTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // now model ready to use
                                                                                    pDialog.dismissWithAnimation();
                                                                                    pDialog.setTitleText("Language Converting....");
                                                                                    pDialog.show();
                                                                                    myTranslator.translate(extractedText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(String s) {
                                                                                            pDialog.dismissWithAnimation();
                                                                                            resultText.setTextColor(purple_700);
                                                                                            resultText.setText("Target Language: "+targetLang[0]+"\n\nResult :\n"+s);
                                                                                            myTranslator. close();
                                                                                            languageIdentifier.close();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            pDialog.dismissWithAnimation();
                                                                                            resultText.setText("Translation Went Wrong"+e.getMessage());
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    resultText.setText("Went Wrong model not download");
                                                                                    pDialog.dismissWithAnimation();
                                                                                }
                                                                            });


                                                                        }
                                                                        else
                                                                        {
                                                                            resultText.setTextColor(Color.RED);
                                                                            resultText.setText("Translation Not Supports to the Source Language");

                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        resultText.setTextColor(Color.RED);
                                                                        resultText.setText("Please! Select Target Language to Translate");
                                                                    }


                                                                }
                                                            });
                                                        }
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Model couldn’t be loaded or other internal error.
                                                        // ...
                                                        sourceLang.setTextColor(Color.RED);
                                                        sourceLang.setText("Source Language Type:Fail to identify language");
                                                    }
                                                });
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        extractedText.setTextColor(Color.RED);
                                        extractedText.setText("Unable to Extract Text Something Went Wrong");
                                        Log.d("myerror",String.valueOf(e));
                                    }
                                });
        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private Uri getUriImage(Bitmap bitmap)
{
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), bitmap, "Title", null);
    Uri uri= Uri.parse(path);
    return uri;
}
private Bitmap getBitmapImage(Uri uri)
    {
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

private void showImage(Bitmap bitmap)
{
    Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate2);
    runOnUiThread(new Runnable(){
        public void run() {
            try {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                View customLayout1= getLayoutInflater().inflate(R.layout.fullimagepopup, null);
                ImageView imageView2=(ImageView)customLayout1.findViewById(R.id.myimg1);
                imageView2.setImageBitmap(bitmap);
                builder1.setView(customLayout1);
                imageView2.setAnimation(animation);
                builder1.setCancelable(true);
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
            } catch (final Exception ex) {
                Log.i("---","Exception in thread"+String.valueOf(ex));
            }
        }
    });

}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && BarCodeValue==0) {
                Uri resultUri = result.getUri();
                cameraPopUp(getBitmapImage(resultUri));
            }
            else if (resultCode == RESULT_OK && BarCodeValue==1)
            {

                Uri resultUri = result.getUri();
                barcodePopUp(resultUri);
            }
            else  if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void extractPDF() {
        try {
            String extractedText = "";
            PdfReader reader = new PdfReader(path);
            int n = reader.getNumberOfPages();
            for (int i = 0; i < n; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
            }
            fileContent=extractedText;
            reader.close();
        } catch (Exception e) {
            fileContent="File Not Found";
        }
    }

    private void openMyFilePopup(String filename, String fileContent)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View customLayout= LayoutInflater.from(MainActivity.this).inflate(R.layout.myfilepopup, null);
        builder.setView(customLayout).setCancelable(false);
        TextView extractedText=(TextView)customLayout.findViewById(R.id.resulttext);
        TextView MyfileName=(TextView)customLayout.findViewById(R.id.txt1);
        TextView sourceLang=(TextView)customLayout.findViewById(R.id.txt4);
        Button button=(Button)customLayout.findViewById(R.id.tranbtn);
        LottieAnimationView lottieAnimationView=(LottieAnimationView)customLayout.findViewById(R.id.download);
        SearchableSpinner searchableSpinner=(SearchableSpinner)customLayout.findViewById(R.id.to);
        TextView resultText=(TextView)customLayout.findViewById(R.id.convertedresulttext);
        final String[] targetLang = {""};

        MyfileName.setText("File Name: "+filename);
        extractedText.setText(fileContent);
        //extractedText.setMovementMethod(new ScrollingMovementMethod());
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                targetLang[0] =searchableSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(extractedText.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(String languageCode) {
                        if (languageCode.equals("und")) {
                            sourceLang.setTextColor(Color.RED);
                            sourceLang.setText("Source Language Type: Can't identify language");
                        }

                        else {
                            Locale loc = new Locale(languageCode);
                            String source = loc.getDisplayLanguage();
                            sourceLang.setTextColor(purple_700);
                            sourceLang.setText("Source Language Type: " + source);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!targetLang[0].equals("Select Target Language"))
                                    {
                                        // progress bar creation
                                        myprogbar();
                                        if(arrayList.contains(languageCode))
                                        {
                                            lottieAnimationView.setVisibility(View.VISIBLE);
                                            TranslatorOptions options =
                                                    new TranslatorOptions.Builder()
                                                            .setSourceLanguage(TranslateLanguage.fromLanguageTag(languageCode))
                                                            .setTargetLanguage(TranslateLanguage.fromLanguageTag(new BCPCode().getBcpCode(targetLang[0])))
                                                            .build();
                                            final Translator myTranslator =
                                                    Translation.getClient(options);
                                            pDialog.setTitleText("Translate Model Downloading....");
                                            pDialog.show();
                                            myTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // now model ready to use
                                                    pDialog.dismissWithAnimation();
                                                    pDialog.setTitleText("Language Converting....");
                                                    pDialog.show();
                                                    myTranslator.translate(extractedText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(String s) {

                                                            resultText.setTextColor(purple_700);
                                                            resultText.setText("Target Language: "+targetLang[0]+"\n\nResult :\n"+s);
                                                            //resultText.setMovementMethod(new ScrollingMovementMethod());
                                                            pDialog.dismissWithAnimation();
                                                            myTranslator. close();
                                                            languageIdentifier.close();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pDialog.dismissWithAnimation();
                                                            resultText.setText("Translation Went Wrong"+e.getMessage());
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    resultText.setText("Went Wrong model not download");
                                                    pDialog.dismissWithAnimation();
                                                }
                                            });


                                        }
                                        else
                                        {
                                            resultText.setTextColor(Color.RED);
                                            resultText.setText("Translation Not Supports to the Source Language");

                                        }
                                    }
                                    else
                                    {
                                        resultText.setTextColor(Color.RED);
                                        resultText.setText("Please! Select Target Language to Translate");
                                    }

                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



lottieAnimationView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        Toast.makeText(MainActivity.this, "Under Progress", Toast.LENGTH_SHORT).show();

    }
});


        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void barcodePopUp(Uri uri)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View customLayout= LayoutInflater.from(MainActivity.this).inflate(R.layout.barcodepopup, null);
        builder.setView(customLayout).setCancelable(false);
        ImageView imageView=(ImageView)customLayout.findViewById(R.id.imgReview1);
        TextView extractedText=(TextView)customLayout.findViewById(R.id.resulttext);
        imageView.setImageURI(uri);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(getBitmapImage(uri));
            }
        });
        extractedText.setText("^.^");

        InputImage image = null;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
       // InputImage image = InputImage.fromBitmap(mybitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully


                        for (Barcode barcode: barcodes)
                        {
                            String rawValue = barcode.getRawValue();
                            int valueType = barcode.getValueType();

                            switch (valueType)
                            {
                                case Barcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    extractedText.setTextColor(purple_700);
                                    extractedText.setText("WIFI Information"+"\n\nSSID: "+ssid+"\nPassword: "+password+"\n WIFI Type: "+ type);
                                    break;
                                case Barcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    extractedText.setTextColor(Color.GREEN);
                                    extractedText.setText("URL Information"+"\n\nTitle: "+title+"\nUrl:  "+url);
                                    break;
                                default:
                                    String info=barcode.getDisplayValue();
                                    extractedText.setTextColor(purple_700);
                                    extractedText.setText("Bar-Code Info: \n"+info);
                                    break;
                            }

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        extractedText.setTextColor(Color.RED);
                        extractedText.setText("Can Not Identify The Bar-Code");
                    }
                });

        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void shareApp()
    {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String sharebody="This App Not Published yet. Created By MOHD ABBAS"; //https://play.google.com/store/apps/details?id=com.jgianveshana
        String sharesubject="Regional Translator";
        intent.putExtra(Intent.EXTRA_TEXT,sharebody);
        intent.putExtra(Intent.EXTRA_SUBJECT,sharesubject);
        startActivity(Intent.createChooser(intent,"Share By"));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("EXIT").setMessage("Do You Want To Exit?").setIcon(R.drawable.tlogout1).setCancelable(false);
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}


