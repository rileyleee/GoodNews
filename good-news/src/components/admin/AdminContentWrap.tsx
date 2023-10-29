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
      <div>
        <div>셀렉트박스1</div>
        <div>셀렉트박스2</div>
        <div>셀렉트박스3</div>
        <Button size="Large" color="Main" onClick={handleRegionClick} isActive>
          조회
        </Button>
      </div>
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
