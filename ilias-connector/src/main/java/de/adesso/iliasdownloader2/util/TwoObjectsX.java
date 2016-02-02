package de.adesso.iliasdownloader2.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class TwoObjectsX<A, B> {
	private A objectA;
	private B objectB;
}
