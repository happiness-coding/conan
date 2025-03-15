package com.happiness.conan.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Custom implementation of {@link LocaleResolver} that resolves the locale based on the "lang" header.
 */
public class CustomLocaleResolver implements LocaleResolver {

    @Override
    public @NonNull Locale resolveLocale(@NonNull HttpServletRequest request) {
        String lang = request.getHeader("lang");
        if (lang == null || lang.isEmpty()) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(lang);
    }

    @Override
    public void setLocale(@NonNull HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // This implementation is read-only, so we do not need to set the locale.
    }
}