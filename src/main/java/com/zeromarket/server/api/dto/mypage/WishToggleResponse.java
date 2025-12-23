package com.zeromarket.server.api.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class WishToggleResponse {

    private boolean liked;
}
