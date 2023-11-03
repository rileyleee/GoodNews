import SpareStatusBox from "../@common/SpareStatusBox";

// 대피소 박스 (위치, 이름, 상태) 1개 컴포넌트
const ShelterBox = () => {
  return (
    <div>
      {/* 대피소 기본 정보(위치, 이름) */}
      <div>
        <div>대전광역시 유성구 덕명동</div>
        <div>하우스토리 네오미아 아파트</div>
      </div>
      {/* 상태 정보 들어감 */}
      <div>
        <div>위험</div>
        <SpareStatusBox keyword="Confusion" />
      </div>
    </div>
  );
};

export default ShelterBox;
