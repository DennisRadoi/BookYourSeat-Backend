package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateMyAccountRequest(
                                     @Pattern(regexp = "\\S+\\s+\\S+", message = "Numele complet trebuie să conțină prenumele și numele.")
                                     String fullname,
                                     @Email(message = "Emailul nu are un format valid.")
                                     String email,
                                     String profilePhoto,
                                     @Pattern(regexp = "\\d{10}", message = "Numărul de telefon trebuie să conțină exact 10 cifre.")
                                     String phoneNumber,
                                     @Size(max = 100, message = "Numele departamentului este prea lung.")
                                     String departmentName,
                                     @Size(max = 100, message = "Rolul este prea lung.")
                                     String role,
                                     @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data angajării trebuie transmisă în formatul YYYY-MM-DD.")
                                     String employmentDate,
                                     @Valid
                                     UpdateMyAdressRequest updateMyAdressRequest) {
}
