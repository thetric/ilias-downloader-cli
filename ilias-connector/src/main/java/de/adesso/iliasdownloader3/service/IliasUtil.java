package de.adesso.iliasdownloader3.service;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class IliasUtil {
    public static String findClientByLoginPageOrWebserviceURL(String loginURL) {
      return "ilias-fhdo";
    }
}
