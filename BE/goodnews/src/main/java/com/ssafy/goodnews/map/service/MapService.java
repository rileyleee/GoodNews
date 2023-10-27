package com.ssafy.goodnews.map.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.map.domain.OffMapInfo;
import com.ssafy.goodnews.map.repository.MapMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

    private final MapMongoRepository mongoRepository;

    @Transactional(readOnly = true)
    public BaseResponseDto test() {

        Optional<OffMapInfo> findId = mongoRepository.findFirstByNameRegex("뉴코아아울렛");
        return BaseResponseDto.builder()
                .data(findId.get())
                .build();
    }
}
