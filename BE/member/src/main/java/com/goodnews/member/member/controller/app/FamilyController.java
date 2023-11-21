package com.goodnews.member.member.controller.app;

import com.goodnews.member.member.dto.request.family.*;
import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.member.dto.request.member.MemberFirstLoginRequestDto;
import com.goodnews.member.member.dto.request.member.MemberRegistFamilyRequestDto;
import com.goodnews.member.member.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Family", description = "멤버(가족) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/family/app")
public class FamilyController {

    private final FamilyService familyService;

    @Operation(summary = "가족 신청 요청", description = "가족 신청 요청하기 familyId는 상대방 전화번호")
    @PostMapping("/registfamily")
    private BaseResponseDto registFamily(@RequestBody MemberRegistFamilyRequestDto memberRegistFamilyRequestDto) {

        return familyService.registFamily(memberRegistFamilyRequestDto);
    }


    @Operation(summary = "가족 신청 수락 및 거절", description = "가족 신청 수락 approve 수정 거절 시 삭제 refuse: false or true")
    @PutMapping("/acceptfamily")
    private BaseResponseDto updateRegistFamily(@RequestBody FamilyRegistRequestDto familyRegistRequestDto) {

        return familyService.updateFamilyMember(familyRegistRequestDto);
    }

    @Operation(summary = "가족 구성원 조회", description = "가족 구성원 정보(전화번호,가족id,수락상태,상태,마지막 연결 시각), familyId 조회")
    @PostMapping("/familyinfo")
    private BaseResponseDto getFamilyMemberInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {
        return familyService.getFamilyMemberInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "가족 모임 장소 등록", description = "가족 모임 장소(장소명,경도,위도) 등록")
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

    @Operation(summary = "가족 신청 요청 조회", description = "가족 신청 요청 조회하기(상대방 이름, 전화번호)")
    @PostMapping("/getregistfamily")
    private BaseResponseDto getRegistFamily(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        return familyService.getRegistFamily(memberFirstLoginRequestDto);
    }
}
