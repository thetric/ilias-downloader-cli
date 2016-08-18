package de.adesso.iliasdownloader3.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

/**
 * @author broj
 * @since 31.05.2016
 */
@AllArgsConstructor
@Data
public final class Course extends AbstractIliasItem {
    private final Collection<? extends AbstractIliasItem> items;
}
