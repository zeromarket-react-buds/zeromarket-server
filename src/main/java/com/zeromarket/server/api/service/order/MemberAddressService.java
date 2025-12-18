package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.MemberAddressDto;
import com.zeromarket.server.api.dto.order.MemberAddressRequest;
import com.zeromarket.server.api.mapper.order.MemberAddressMapper;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
public class MemberAddressService {

    private final MemberAddressMapper mapper;

    @Transactional
    public void create(Long memberId, MemberAddressRequest req) {
        if(memberId == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        System.out.println(req.toString());

        int addressCount = mapper.countActiveByMember(memberId);
        boolean isFirstAddress = addressCount == 0;
        boolean makeDefault = isFirstAddress || req.isDefault();

        if (makeDefault) {
            mapper.clearDefault(memberId);
        }

        mapper.insert(memberId, req, makeDefault);
    }

    public List<MemberAddressDto> findAll(Long memberId) {
        if(memberId == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        return mapper.findAll(memberId);
    }

//    @Transactional
//    public void update(Long addressId, Long memberId, MemberAddressRequest req) {
//        if (req.isDefault()) {
//            mapper.clearDefault(memberId);
//        }
//        mapper.update(addressId, memberId, req);
//    }
//
//    @Transactional
//    public void delete(Long addressId, Long memberId) {
//        mapper.softDelete(addressId, memberId);
//    }

    public MemberAddressDto findById(Long addressId, Long memberId) {
        return mapper.findById(addressId, memberId);
    }
}

