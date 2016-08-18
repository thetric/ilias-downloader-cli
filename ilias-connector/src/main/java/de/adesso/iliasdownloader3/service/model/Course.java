package de.adesso.iliasdownloader3.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author broj
 * @since 31.05.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Course {
    private int id;
    private String name;
    private String url;
}
