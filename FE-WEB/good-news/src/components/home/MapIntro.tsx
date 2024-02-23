import styled from "styled-components";
import Text from "../@common/Text";

// 메인페이지의 지도 부분
const MapIntro = () => {
  return (
    <StyledMapPageWrapper>
      <StyledMapIntro className="flex items-center justify-center lg:justify-around relative">
        <Text size="text2" className="text-center">
          사용자 이용 현황
        </Text>
      </StyledMapIntro>
    </StyledMapPageWrapper>
  );
};

export default MapIntro;

const StyledMapPageWrapper = styled.div`
  width: 100%;
  height: 100%;
`;

const StyledMapIntro = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;
