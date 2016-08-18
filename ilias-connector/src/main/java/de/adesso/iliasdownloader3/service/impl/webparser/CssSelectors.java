package de.adesso.iliasdownloader3.service.impl.webparser;

import lombok.Getter;

/**
 * @author broj
 * @since 18.08.2016
 */
enum CssSelectors {
    COURSE_SELECTOR("a[href*='_crs_'].il_ContainerItemTitle"),
    GENERIC_ITEM_SELECTOR("a.il_ContainerItemTitle");

    @Getter
    private final String cssSelector;

    CssSelectors(String cssSelector) {
        this.cssSelector = cssSelector;
    }
}
