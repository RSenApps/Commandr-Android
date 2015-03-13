package com.RSen.Commandr.util;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adam on 6. 3. 2015.
 */
public class ActivationCheck {
    public static String phraseActivation(String prompt, String activationPhrase) {
        String[] splitPhrases = activationPhrase.toLowerCase().split(",");
        boolean activate = false;
        for (String splitPhrase : splitPhrases) {
            activate = true;
            for (String activationPhrasePart : splitPhrase.split("&")) {
                if (!prompt.toLowerCase().trim().contains(activationPhrasePart.toLowerCase().trim())) {
                    activate = false;
                    break;
                }
            }

        }
        return activate ? prompt:null;
    }
    public static MatchResult regexActivation(String prompt, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(prompt);
        return m.find()? m.toMatchResult() : null;
    }
}