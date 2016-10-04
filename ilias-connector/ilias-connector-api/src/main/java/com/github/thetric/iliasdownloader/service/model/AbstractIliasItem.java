package com.github.thetric.iliasdownloader.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author broj
 * @since 18.08.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractIliasItem implements IliasItem {
    private int id;
    private String name;
    private String url;
}
