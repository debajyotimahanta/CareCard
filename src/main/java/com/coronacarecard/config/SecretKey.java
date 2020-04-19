package com.coronacarecard.config;

public enum SecretKey {
    STRIPE_KEY ("STRIPE_KEY"),
    GEO_API_KEY("GEO_API_KEY"),
    STRIPE_CLIENT_ID ("STRIPE_CLIENT_ID"),
    STRIPE_WEB_HOOK_SECRET("STRIPE_WEB_HOOK_SECRET");

    private final String name;

    SecretKey(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
