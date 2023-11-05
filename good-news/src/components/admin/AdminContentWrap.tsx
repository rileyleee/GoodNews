// 관리 페이지에서 content 전부
import { useState } from "react";
import styled from "styled-components";

import MapAdminInfoBoxList from "./MapAdminInfoBoxList";
import MapAdmin from "./MapAdmin";
import ShelterBoxList from "./ShelterBoxList";

import Button from "../@common/Button";
import SelectBox from "../@common/SelectBox";
import Text from "../@common/Text";

const AdminContentWrap = () => {
  const [fullRegion, setFullRegion] = useState("-");
  const [countRegion, setCountRegion] = useState(0);
  const handleRegionClick = () => {
    console.log("지역을 조회하는 버튼을 클릭했습니다.");
  };

  // 테스트 데이터
  const people = [
    { name: "Wade Cooper" },
    { name: "Arlene Mccoy" },
    { name: "Devon Webb" },
    { name: "Tom Cook" },
    { name: "Tanya Fox" },
    { name: "Hellen Schmidt" },
  ];

  return (
    <>
      <div className="h-1/6">
        <div className="grid grid-cols-3">
          <Text size="text3" isBold={true} className="col-span-1">
            대피소 현황
          </Text>
          {/* 지역 조회 셀렉트 + 버튼 박스 */}
          <div className="grid grid-cols-5 gap-2 col-span-2">
            <div className="col-span-4 grid grid-cols-3 gap-2">
              <SelectBox data={people} />
              <SelectBox data={people} />
              <SelectBox data={people} isInActive={true} />
            </div>
            <Button
              size="Large"
              color="Main"
              onClick={handleRegionClick}
              isActive={true}
              className="col-span-1"
            >
              조회
            </Button>
          </div>
        </div>

        <div className="flex justify-end items-center mt-2">
          <Text size="text5" color="Gray" isBold={true}>
            {fullRegion}
          </Text>
          <Text size="text6" color="Gray" className="ml-1">
            에 대한
          </Text>
          <Text size="text6" color="Point" isBold={true} className="ml-2">
            {countRegion} 건
          </Text>
          <Text size="text6" color="Gray" className="ml-1">
            의 대피소 조회 결과입니다.
          </Text>
        </div>
      </div>

      {/* 2분할 (지도+대피소정보) + (대피소 리스트) */}
      <div className="grid grid-cols-2 gap-6 h-5/6">
        {/* (지도+대피소정보) */}
        <div className="grid grid-rows-2 gap-4 h-full">
          <MapAdmin />
          <MapAdminInfoBoxList />
        </div>
        {/* (대피소 리스트) */}
        <StyledShelterList>
          <ShelterBoxList />
        </StyledShelterList>
      </div>
    </>
  );
};

export default AdminContentWrap;

const StyledShelterList = styled.div`
  width: 100%;
  height: 100%;
  overflow-y: auto;
`;
