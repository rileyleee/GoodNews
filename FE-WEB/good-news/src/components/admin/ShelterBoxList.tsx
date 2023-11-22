// 대피소 박스를 묶는 리스트 컴포넌트
import ShelterBox from "./ShelterBox";

const ShelterBoxList = () => {
  return (
    <div className="grid gap-2 pr-2">
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
      <ShelterBox />
    </div>
  );
};

export default ShelterBoxList;
