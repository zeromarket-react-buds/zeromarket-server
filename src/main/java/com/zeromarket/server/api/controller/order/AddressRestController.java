package com.zeromarket.server.api.controller.order;

import com.zeromarket.server.api.dto.order.MemberAddressDto;
import com.zeromarket.server.api.dto.order.MemberAddressRequest;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.order.MemberAddressService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressRestController {

    private final MemberAddressService addressService;

    @PostMapping
    public void create(
        @RequestBody MemberAddressRequest req,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        addressService.create(userDetails.getMemberId(), req);
    }

    @GetMapping
    public List<MemberAddressDto> list(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return addressService.findAll(userDetails.getMemberId());
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<MemberAddressDto> detail(
        @PathVariable Long addressId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if(user == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        MemberAddressDto addressDetail = addressService.findById(addressId, user.getMemberId());

        return ResponseEntity.ok(addressDetail);
    }

    @GetMapping("/default")
    public ResponseEntity<MemberAddressDto> defaultAddress (
        @PathVariable Long addressId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if(user == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        MemberAddressDto addressDetail = addressService.findDefaultByMember(user.getMemberId());

        return ResponseEntity.ok(addressDetail);
    }

    @PutMapping("/{addressId}")
    public void update(
        @PathVariable Long addressId,
        @RequestBody MemberAddressRequest req,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        addressService.update(
            addressId,
            user.getMemberId(),
            req
        );
    }

    @DeleteMapping("/{addressId}")
    public void delete(
        @PathVariable Long addressId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        addressService.delete(addressId, user.getMemberId());
    }
}

