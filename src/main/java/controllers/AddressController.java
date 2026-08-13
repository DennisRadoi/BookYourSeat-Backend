package controllers;

import dto.AddressResponseDTO;
import entities.Address;
import services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{id}")
    public AddressResponseDTO getAddressById(@PathVariable Integer id) {

        return addressService.getAddressById(id);
    }

    @GetMapping
    public List<AddressResponseDTO> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @PostMapping
    public AddressResponseDTO createAddress(@RequestBody Address address) {
        return addressService.createAddress((address));
    }

    @PutMapping("/{id}")
    public AddressResponseDTO updateAddress(@PathVariable Integer id, @RequestBody Address address) {

        return addressService.updateAddress(id, address);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Integer id) {

        addressService.deleteAddress(id);
    }

}
