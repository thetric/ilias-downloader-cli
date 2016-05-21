package de.adesso.iliasdownloader2.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Deprecated
final class SoapResult {
    private String text;
    private String error;
    private boolean faultCode;
}
