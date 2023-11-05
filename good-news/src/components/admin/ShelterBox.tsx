// 대피소 박스 (위치, 이름, 상태) 1개 컴포넌트
import styled from "styled-components";

import SpareStatusBox from "../@common/SpareStatusBox";
import Text from "../@common/Text";
import SafeStatusBox from "../@common/SafeStatusBox";

interface ShelterInfo {
  region: string;
  shelterName: string;
  safeStatus: string;
  spareStatus: string;
}

interface ShelterBoxProps {
  data?: ShelterInfo[];
  onClick?: () => void;
}

const ShelterBox = ({ data, onClick }: ShelterBoxProps) => {
  return (
    <StyledShelterWrapper
      className="flex justify-between items-center"
      onClick={onClick}
    >
      {/* 대피소 기본 정보(위치, 이름) */}
      <div>
        <Text size="text6">대전광역시 유성구 덕명동</Text>
        <Text size="text5" isBold={true} className="mt-4">
          하우스토리 네오미아 아파트
        </Text>
      </div>
      {/* 상태 정보 들어감 */}
      <div className="text-center">
        <SafeStatusBox keyword="Dangerous" />
        <SpareStatusBox keyword="Confusion" className="mt-4" />
      </div>
    </StyledShelterWrapper>
  );
};

export default ShelterBox;

/** 임시 shelterbox wrapper */
const StyledShelterWrapper = styled.div`
  width: 100%;
  background-color: #e7ecef;
  border-radius: 16px;
  padding: 28px;

  &:hover {
    background-color: #c0d6df;
  }
`;
