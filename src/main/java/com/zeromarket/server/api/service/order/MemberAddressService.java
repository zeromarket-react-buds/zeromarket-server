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

    private static final int MAX_ADDRESSES = 5;

    private final MemberAddressMapper mapper;

    @Transactional
    public void create(Long memberId, MemberAddressRequest req) {
        if(memberId == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        int addressCount = mapper.countActiveByMember(memberId);

        if(addressCount >= MAX_ADDRESSES) {
            throw new ApiException(ErrorCode.ADDRESS_LIMIT_REACHED);
        }

        boolean isFirstAddress = addressCount == 0;
        boolean makeDefault = isFirstAddress || req.isDefault();
//        첫 배송지 -> 자동 default 설정
        if (makeDefault) {
            mapper.clearDefault(memberId);
        }

        mapper.insert(memberId, req, makeDefault);
    }

    public List<MemberAddressDto> findAll(Long memberId) {
        if(memberId == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        return mapper.findAll(memberId);
    }

    public MemberAddressDto findById(Long addressId, Long memberId) {
        return mapper.findById(addressId, memberId);
    }

    public MemberAddressDto findDefaultByMember(Long memberId) {
        return mapper.findDefaultByMember(memberId);
    }

    @Transactional
    public void update(Long addressId, Long memberId, MemberAddressRequest req) {
        // 현재 주소가 대표인데 false로 바꾸는 건 허용하되,
        // 그 결과 대표가 0개가 되면 안 되므로 안전하게 처리
        boolean makeDefault = req.isDefault();

        if (makeDefault) {
            mapper.clearDefault(memberId);
        } else {
            // 대표를 해제하려는 경우
            // 이 주소가 대표였다면, 다른 대표를 자동 지정하거나(선호), 혹은 막아도 됨.
            boolean wasDefault = mapper.isDefaultAddress(addressId, memberId);
            if (wasDefault) {
                // 여기서는 "대표 해제 금지"가 UX에 더 좋음
                throw new ApiException(ErrorCode.ADDRESS_UNSET_NOT_ALLOWED);
            }
        }

        mapper.update(addressId, memberId, req);
//        mapper.update(addressId, memberId, req, makeDefault);
    }

    @Transactional
    public void delete(Long addressId, Long memberId) {
        boolean wasDefault = mapper.isDefaultAddress(addressId, memberId);

        mapper.softDelete(addressId, memberId);

        if (wasDefault) {
            int remain = mapper.countActiveByMember(memberId);
            if (remain > 0) {
                Long nextId = mapper.pickNextDefaultAddressId(memberId);
                mapper.clearDefault(memberId);
                mapper.setDefaultById(nextId, memberId);
            }
        }
    }
}

