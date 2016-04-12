package com.mapbox.services.android.geocoder;

import android.location.Address;

import com.mapbox.services.geocoding.v5.models.GeocodingFeature;

import java.util.Locale;

/**
 * Created by antonio on 1/30/16.
 */
public class GeocoderUtils {

    public static Address featureToAddress(GeocodingFeature geocodingFeature, Locale locale) {
        Address address = new Address(locale);
        address.setAddressLine(0, geocodingFeature.getPlaceName());
        address.setFeatureName(geocodingFeature.getText());
        address.setLongitude(geocodingFeature.getLongitude());
        address.setLatitude(geocodingFeature.getLatitude());
        return address;
    }

}