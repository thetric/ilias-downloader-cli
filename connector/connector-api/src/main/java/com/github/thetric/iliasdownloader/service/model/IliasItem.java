package com.github.thetric.iliasdownloader.service.model;

/**
 * @author broj
 * @since 18.08.2016
 */
public interface IliasItem {
    int getId();

    String getName();

    String getUrl();

    void setId(int id);

    void setName(String name);

    void setUrl(String url);
}
