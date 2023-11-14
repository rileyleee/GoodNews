package com.ssafy.goodnews.member.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyPlaceCanuseDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyPlaceRequestDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyPlaceUpdateRequestDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyRegistPlaceRequestDto;
import com.ssafy.goodnews.member.dto.request.member.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.member.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Family", description = "멤버(가족) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/family")
public class FamilyController {

    private final FamilyService familyService;

    @Operation(summary = "가족 신청 요청", description = "가족 신청 요청하기 familyId는 상대방 기기번호")
    @PostMapping("/registfamily")
    private BaseResponseDto registFamily(@RequestBody MemberRegistFamilyRequestDto memberRegistFamilyRequestDto) {

        return familyService.registFamily(memberRegistFamilyRequestDto);
    }


    @Operation(summary = "가족 신청 수락", description = "가족 신청 수락 approve 수정")
    @PutMapping("/acceptfamily")
    private BaseResponseDto updateRegistFamily(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        return familyService.updateFamilyMember(memberFirstLoginRequestDto);
    }

    @Operation(summary = "가족 구성원 조회", description = "가족 구성원 정보(전화번호,가족id,수락상태,상태,마지막 연결 시각), familyId 조회")
    @PostMapping("/familyinfo")
    private BaseResponseDto getFamilyMemberInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {
        return familyService.getFamilyMemberInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "가족 모임 장소 등록", description = "=가족 모임 장소(장소명,경도,위도) 등록")
    @PostMapping("/registplace")
    private BaseResponseDto registFamilyPlace(@RequestBody FamilyRegistPlaceRequestDto familyRegistPlaceRequestDto) {
        return familyService.registFamilyPlace(familyRegistPlaceRequestDto);
    }

    @Operation(summary = "가족 모임장소 조회", description = "가족 모임장소 정보(장소id,명칭,사용가능여부,순서(1,2,3)) 조회")
    @PostMapping("/allplaceinfo")
    private BaseResponseDto getFamilyPlaceInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {
        return familyService.getFamilyPlaceInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "가족 모임장소 상세 조회", description = "가족 모임장소 상세 정보(장소id,명칭,경도,위도,사용가능여부) 조회")
    @PostMapping("/placeinfo")
    private BaseResponseDto getFamilyPlaceInfo(@RequestBody FamilyPlaceRequestDto familyPlaceRequestDto) {
        return familyService.getFamilyPlaceInfoDetail(familyPlaceRequestDto.getPlaceId());
    }

    @Operation(summary = "가족 모임장소 수정", description = "가족 모임장소 수정(명칭,경도,위도) 수정")
    @PutMapping("/placeinfo/{placeId}")
    private BaseResponseDto getFamilyUpdatePlaceInfo(@PathVariable int placeId,@RequestBody FamilyPlaceUpdateRequestDto familyPlaceUpdateRequestDto) {
        return familyService.getFamilyPlaceInfoUpdate(placeId,familyPlaceUpdateRequestDto);
    }
    @Operation(summary = "가족 모임장소 사용가능여부 수정", description = "가족 모임장소 사용가능 여부 수정(사용가능여부) 수정")
    @PutMapping("/placeuse/{placeId}")
    private BaseResponseDto getFamilyUpdatePlaceCanUse(@PathVariable int placeId,@RequestBody FamilyPlaceCanuseDto familyPlaceCanuseDto) {
        return familyService.getFamilyPlaceInfoCanUseUpdate(placeId,familyPlaceCanuseDto);
    }
}
