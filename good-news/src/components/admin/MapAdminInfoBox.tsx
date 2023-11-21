// 대피소 상세 정보 1개 컴포넌트 (아이콘 + 라벨 + 내용)
import styled from "styled-components";
import Text from "../@common/Text";

interface MapAdminInfoBoxProps {}

const MapAdminInfoBox = () => {
  return (
    <div className="flex items-center">
      <div className="flex items-center">
        <StyledImage
          src="https://pbs.twimg.com/media/FTxJstIaQAAU9Lc.jpg"
          alt="아이콘"
        />
        <Text size="text5" isBold={true} className="ml-4">
          라벨
        </Text>
      </div>
      <Text size="text5" className="ml-6">
        들어갈 내용
      </Text>
    </div>
  );
};

export default MapAdminInfoBox;

const StyledImage = styled.img`
  width: 28px;
  height: 28px;
`;
