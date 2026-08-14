package dto;

import entities.Address;

public record AddressResponseDTO(
    Integer id,
    String street,
    String number,
    Integer floor,
    String apartmentBlock,
    String postalCode,
    String localityName,
    String countyName
) {

    public static AddressResponseDTO fromEntity(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getFloor(),
                address.getApartmentBlock(),
                address.getPostalCode(),
                address.getLocality() == null ? null : address.getLocality().getName(),
                address.getLocality() == null || address.getLocality().getCounty() == null
                        ? null : address.getLocality().getCounty().getName()
        );
    }
}
