package services;

import dto.AddressResponseDTO;
import entities.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.AddressRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    private Address findAddressEntity(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found: " + id));
    }

    public AddressResponseDTO getAddressById(Integer id) {
        return AddressResponseDTO.fromEntity(
                findAddressEntity(id)
        );
    }

    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(AddressResponseDTO::fromEntity)
                .toList();
    }

    public AddressResponseDTO createAddress(Address address) {

        Address savedAddress = addressRepository.save(address);

        return AddressResponseDTO.fromEntity(savedAddress);
    }

    public AddressResponseDTO updateAddress(Integer id, Address address) {

        Address existing = findAddressEntity(id);

        existing.setStreet(address.getStreet());
        existing.setNumber(address.getNumber());
        existing.setFloor(address.getFloor());
        existing.setApartmentBlock(address.getApartmentBlock());
        existing.setPostalCode(address.getPostalCode());
        existing.setType(address.getType());
        existing.setLocality(address.getLocality());

        Address updatedAddress = addressRepository.save(existing);

        return AddressResponseDTO.fromEntity(updatedAddress);
    }

    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }
}
