package dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateMyAdressRequest(@Size(max = 100, message = "Județul este prea lung.") String county,
                                    @Size(max = 100, message = "Localitatea este prea lungă.") String locality,
                                    String number, String street,
                                    String apartmentBlock,
                                    @Min(value = 0, message = "Etajul nu poate fi negativ.")
                                    Integer floor,
                                    @Pattern(regexp = "\\d{6}", message = "Codul poștal trebuie să conțină exact 6 cifre.")
                                    String postalCode,
                                    boolean clearApartmentBlock,
                                    boolean clearFloor) {
}
