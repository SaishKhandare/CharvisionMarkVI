package com.example.saish.charvisionmarkvi;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.Word;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class GoogleVision
{
    static String data = "", textword = "";
    static StringBuilder dataBuild = new StringBuilder();


    static int nametrap = 0; //used to append few words after "name" to String "namev"
    static int totaltrap = 0; //used to append few words after "total" to String "totalv"
    static int istrapedname = 0; //Ensure "name" is trapped only once
    static int istrapedtotal = 0; //Ensure "total" is trapped only once
    static int result = 0; // used in condition check function


    static String totalv = "";
    static String namev = "";
    static String numberv = "";


    static String[] namearr = new String[]{"name"};
    static String[] totalarr = new String[]{"total"};

    static int isTrapNumber = 0;
    static int TrapNumber = 0;

    static float confidence;
    static String valid;

    public static String Run_OCR(Context context, Bitmap picture)
    {


        data = "";
        //Declarations end.....................................................................................

        //creating vision object
        Vision.Builder visionbuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null
        );
        String key = context.getString(R.string.mykey);
        visionbuilder.setVisionRequestInitializer(new VisionRequestInitializer(key));
        final Vision vision = visionbuilder.build();

        //Encoding the Image.
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        //initially picture is to be compressed
        picture.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        // String base64Data = Base64.encodeToString(byteStream.toByteArray(), Base64.URL_SAFE);


        Image inputImage = new Image();
        inputImage.encodeContent(byteStream.toByteArray());

        Feature desiredFeatures = new Feature();
        desiredFeatures.setType("DOCUMENT_TEXT_DETECTION");


        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage);
        request.setFeatures(Arrays.asList(desiredFeatures));

        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Arrays.asList(request));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

            try
            {
                BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();

                //using Textannotation
                final TextAnnotation text = batchResponse.getResponses().get(0).getFullTextAnnotation();
                for (Page page : text.getPages())
                {
                    for (Block block : page.getBlocks())
                    {
                        data = data + "\n new_block_start";

                        for (Paragraph para : block.getParagraphs())
                        {
                            data = data + "\n new_para_start";
                            for (Word word : para.getWords())
                            {

                                for (Symbol symbol : word.getSymbols())
                                {
                                   // String test = String.valueOf(symbol.values().toArray()[ symbol.values().toArray().length-1 ]);
                                    Log.e("SYMBOL CONFIDENCE ", symbol.getText().toString() + ": " + symbol.values().toArray()[symbol.values().toArray().length - 1] );
                                    if(getConfidence(symbol)>=0.5)
                                    {
                                        textword = textword + symbol.getText();
                                    }
                                }


                                textword = textword + " ";
                                dataBuild.append(textword);

                                // data = data + "\n\n";
                                // data = data + textword ;
                                //data = data + "\n";
                                textword = "";
                            }
                        }
                    }
                }

                // data = data + "\n NAME:" + namev;
                // data = data + "\n TOTAL" + totalv;
                // data = data  + "\n NUMBER " + numberv;
                // data = (data + "\n NAME TRAP : " + istrapedname);
                // data = (data + "\n TOTAL TRAP : " + istrapedtotal);

                data = dataBuild.toString();
                // data = data + "\n\n NUMBER::" +Refiner.getField("CUSTOMER NO :",data,3);
                // data = data + "\n TOTAL::" + Refiner.getField("TOTAL",data,4);
                data = data + "\n\n CARD NUMBER::" + RefinerCard.getField("Bank", dataBuild.toString(), 0);


                valid = RefinerCard.getField("/", dataBuild.toString(), 8);
                if (valid.equals("NULL"))
                {
                    //data = data + "\n VALID THRU::" + valid;
                    valid =  RefinerCard.getField("THRU", dataBuild.toString(), 9);
                    if(valid.equals("NULL"))
                    {
                        //Call the validByDIGIT function here
                        valid = RefinerCard.validByDigit(dataBuild.toString());
                    }

                }


                data = data + "\n VALID THRU::" + valid;
                data = data + "\n CARD NAME::" + RefinerCard.cardName(dataBuild.toString());
                return data;

            } catch (IOException e)
            {
                e.printStackTrace();

                return "SOME ERROR OCCURED";
            }
        }


        return "ERROR SDK < 8";
    }



    public static float getConfidence(Symbol symbol)
    {

        if(symbol.getText().toString().equals("/"))
        {
            return 1;
        }
        String con = String.valueOf(symbol.values().toArray()[symbol.values().toArray().length - 1]);
        try
        {
            confidence = Float.valueOf(con);

        }catch (NumberFormatException e)
        {
            confidence = 1;
        }
        return confidence;
    }

}
