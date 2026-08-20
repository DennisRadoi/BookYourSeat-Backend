package dto;

public record UpdateMyAdressRequest(String county, String locality,
                                    String number, String street,
                                    String apartmentBlock,
                                    Integer floor,
                                    String postalCode,
                                    boolean clearApartmentBlock,
                                    boolean clearFloor) {
}
