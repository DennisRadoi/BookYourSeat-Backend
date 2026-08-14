package dto;


public record UpdateMyAccountRequest(String fullname,
                                     String email,
                                     String profilePhoto,
                                     String phoneNumber,
                                     String departmentName) {
    
}