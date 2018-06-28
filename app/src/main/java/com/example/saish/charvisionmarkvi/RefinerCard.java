package com.example.saish.charvisionmarkvi;

import android.util.Log;

import java.security.PublicKey;

public class RefinerCard
{

    static int valid = 0;
    static int h;
    static int k;
    static int match=0;//used in isUpperCase


    public static String getField(String field, String data, int draw)
    {

        String[] dataArray = data.split(" ");
        String[] fieldArray = field.split(" ");
        for (h = 0; h < dataArray.length; h++)
        {
            Log.e("OUTPUT", "getField: " + dataArray[h]);

            for (k = 0; k < fieldArray.length; k++)
            {
                if (dataArray[h + k].equals(fieldArray[k]))
                {
                    valid = 1;
                }
                else
                {
                    valid = 0;
                    break;
                }
            }
            if (valid == 1)
            {
                //if Draw is specified call the draw method if not specified will return NULL
                if (draw == 0) //CARD NUMBER
                {
                    // return Draw(h+k,draw,dataArray);
                    StringBuilder subData = new StringBuilder();
                    for (int i = h + k; i < dataArray.length; i++)
                    {
                        subData.append(dataArray[i]);
                    }
                    return CardNumber(subData.toString());
                }
                if(draw ==8)//VALID THRU BY /
                {
                    StringBuilder subData = new StringBuilder();
                    for(int i = h-2;i<dataArray.length ;i++)
                    {
                        subData.append(dataArray[i]);

                    }
                    String SUBDATA = subData.toString();
                    return Valid(SUBDATA);
                }
                if(draw ==9)//VALID THRU BY THRU
                {
                    StringBuilder subdata = new StringBuilder();
                    for(int k =h;k<dataArray.length;k++)
                    {
                        subdata.append(dataArray[k]);
                    }
                    return  Valid(subdata.toString());
                }

            }
        }
        return "NULL";

    }


    //FUNCTION TO EXTRACT CARD NAME
    public static String cardName(String data)
    {
        StringBuilder cardname = new StringBuilder();
        String[] dataArray = data.split(" ");
        int h = dataArray.length;
        for (int k=h-6;k<h;k++)
        {
            if(isUpperCase(dataArray[k]))
            {
                if(isValidName(dataArray[k]))
                {
                    cardname.append(dataArray[k] + " ");
                }
            }

        }
        if(cardname.length()==0)
        {
            return "NULL";
        }
        else
        {
            return cardname.toString();
        }
    }
    //Function to check ifValidName in cardName
    public static Boolean isValidName(String data)
    {
        String[] invalidNames = {"VISA","VALID","THRU","YARU","SIGNATURE","PLATINUM","FROM","GOLD","INFINITE","WORLD","AMERICAN","EXPRESS","YAU","YAH","YA","VRU","YARO"};
       // String[] invalidSubNames= {"VISA","VALID","THRU","SIGNATURE","PLATINUM","FROM","GOLD","INFINITE","WORLD","AMERICAN","EXPRESS"};
        for (String word:invalidNames)
        {
            if (word.equals(data))
            {
                return false;
            }

        }

        return true;
    }





    //Function to check if uppercase in cardName
    public static Boolean isUpperCase(String data)
    {
        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int h = 0;h<data.length();h++)
        {
            Character c = ((Character) data.charAt(h));
            match=0;
            for(int k =0;k<alphabets.length();k++)
            {
                if(c.equals(alphabets.charAt(k)))
                {
                    match = 1;
                }
            }
            if(match==0)
            {
                return false;
            }
        }
        return true;
    }

    //FUNCTION TO EXTRACT VALID THRU
    public static String Valid(String data)
    {
        int h;
        StringBuilder month = new StringBuilder();
        StringBuilder year = new StringBuilder();

        for(h=0;h<data.length();h++)
        {
            if(isDigit(data.charAt(h)))
            {
                month.append(data.charAt(h));
                month.append(data.charAt(h+1));
                year.append(data.charAt(h+3));
                year.append(data.charAt(h+4));
                break;
            }
        }
        for(int k = h+4;k<data.length();k++ )
        {
            if(((Character) data.charAt(k)).equals('/'))
            {
                for (int i = k-4;i<data.length();i++)
                {
                    if(isDigit(data.charAt(i)))
                    {
                        month.append(data.charAt(i));
                        month.append(data.charAt(i+1));
                        year.append(data.charAt(i+3));
                        year.append(data.charAt(i+4));
                        break;
                    }
                }
            }
        }
        if(month.length()!=0 && year.length() !=0)
        {
            return month.toString() + " " + year.toString();

        }
        else
        {
            return "null";
        }

    }

    //FUNCTION TO EXTRACT CARD NUMBER
    public static String CardNumber(String data)
    {
        int size = 0;

        StringBuilder CardNum = new StringBuilder();
        for (h = 0; h < data.length(); h++)
        {
            if (isDigit((data.charAt(h))))
            {
                if (size < 16)
                {
                    CardNum.append(data.charAt(h));
                    size = size + 1;
                }
                else
                {
                    break;
                }
            }
        }
        return CardNum.toString();
    }


    //FUNCTION TO FIND CHARACTER IS DIGIT
    public static Boolean isDigit(Character c) {
        if (c.equals('0') || c.equals('0') || c.equals('1') || c.equals('2') || c.equals('3') || c.equals('4') || c.equals('5') || c.equals('6') || c.equals('7') || c.equals('8') || c.equals('9')) {
            return true;
        } else {
            return false;
        }
    }

    //FUNCTION TO EXTRACT VALID BY VALID BY DIGIT
    public static String validByDigit(String data)
    {
        StringBuilder month = new StringBuilder();
        StringBuilder year = new StringBuilder();
        int doneMonth = 0;

        String dataArray[] = data.split(" ");
        for (int h=0;h<dataArray.length;h++)
        {
            if(dataArray[h].length()==2)
            {

                if(isDigit(dataArray[h].charAt(0))&&isDigit(dataArray[h].charAt(1)))
                {
                    if(doneMonth==0)
                    {
                        month.append(dataArray[h].toString().charAt(0));
                        month.append(dataArray[h].toString().charAt(1));
                        doneMonth=1;
                    }
                    else
                    {
                        year.append(dataArray[h].toString().charAt(0));
                        year.append(dataArray[h].toString().charAt(1));
                    }


                }
            }
        }
        if(month.length()!=0&&year.length()!=0)
        {
            return month.toString() + " " + year.toString();
        }
        else
        {
            for (int h=0;h<dataArray.length;h++)
            {
                if(dataArray[h].length()==5)
                {
                    String word = dataArray[h];
                    int k;
                    for(k = 0;k<word.length();k++)
                    {
                        if(!isDigit(word.charAt(k)))
                        {
                            break;
                        }
                    }
                    if(k == 5)
                    {
                        month.append(word.charAt(0));
                        month.append(word.charAt(1));
                        year.append(word.charAt(3));
                        year.append(word.charAt(4));
                        break;
                    }

                }
            }
            if(month.length()!=0&&year.length()!=0)
            {
                return month.toString() + " " + year.toString();
            }
            else
            {
                return "NULL";
            }

        }
    }


}