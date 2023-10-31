import styled from "styled-components";
import Button from "../@common/Button";
import MapAdmin from "./MapAdmin";
import MapAdminInfoBoxList from "./MapAdminInfoBoxList";
import ShelterBoxList from "./ShelterBoxList";

// 관리 페이지에서 content 전부
const AdminContentWrap = () => {
  const handleRegionClick = () => {
    console.log("지역을 조회하는 버튼을 클릭했습니다.");
  };

  return (
    <>
      <div>대피소 현황</div>
      {/* 지역 조회 셀렉트 + 버튼 박스 */}
      <StyledRegionCheckWrapper>
        <StyledTempSelect>셀렉트박스1</StyledTempSelect>
        <StyledTempSelect>셀렉트박스2</StyledTempSelect>
        <StyledTempSelect>셀렉트박스3</StyledTempSelect>
        <Button
          size="Large"
          color="Main"
          onClick={handleRegionClick}
          isActive={true}
        >
          조회
        </Button>
      </StyledRegionCheckWrapper>
      <div>조회한 지역의 이름 에 대한 몇 건의 대피소 조회 결과입니다.</div>
      {/* 2분할 (지도+대피소정보) + (대피소 리스트) */}
      <div>
        {/* (지도+대피소정보) */}
        <div>
          <MapAdmin />
          <MapAdminInfoBoxList />
        </div>
        {/* (대피소 리스트) */}
        <div>
          <ShelterBoxList />
        </div>
      </div>
    </>
  );
};

export default AdminContentWrap;

// 임시 셀렉트박스 모양
const StyledTempSelect = styled.div`
  height: 48px;
  background-color: red;
`;

// 지역 조회 셀렉트박스 + 버튼 wrapper
const StyledRegionCheckWrapper = styled.div`
  display: flex;
  justify-content: center;
`;
