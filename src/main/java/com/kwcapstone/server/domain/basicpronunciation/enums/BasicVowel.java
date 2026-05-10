package com.kwcapstone.server.domain.basicpronunciation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BasicVowel {
    A("아"),
    E("에"),
    I("이"),
    O("오"),
    U("우");

    private final String korean;
}
