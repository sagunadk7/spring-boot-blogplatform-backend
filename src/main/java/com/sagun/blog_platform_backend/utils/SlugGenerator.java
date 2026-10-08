package com.sagun.blog_platform_backend.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SlugGenerator {

    public static String getSlug(String title){
        if(title == null || title.length() < 5 ) throw new IllegalArgumentException();
        return Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{M}","")
                .toLowerCase(Locale.ROOT).
                replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s","-")
                .replaceAll("-+","-");

    }

}
