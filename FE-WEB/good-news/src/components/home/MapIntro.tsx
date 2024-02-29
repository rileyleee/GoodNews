import styled from "styled-components";
import Text from "../@common/Text";
import { useEffect, useRef } from "react";

// 메인페이지의 지도 부분
const MapIntro = () => {
  const mapRef = useRef(null);

  useEffect(() => {
    const { naver } = window;
    if (mapRef.current && naver) {
      const map = new naver.maps.Map(mapRef.current, {
        zoom: 17, // 지도 확대 정도
      });
    }
  }, []);

  return (
    <StyledMapPageWrapper>
      <StyledMapIntro className="flex-row items-center justify-center lg:justify-around relative">
        <Text size="text3" className="md:hidden text-center">
          사용자 <strong>이용 현황</strong>
        </Text>
        <Text size="text2" className="hidden md:block lg:hidden">
          사용자 <strong>이용 현황</strong>
        </Text>
        <Text size="text1" className="hidden lg:block">
          사용자 <strong>이용 현황</strong>
        </Text>
        <StyledMap ref={mapRef}></StyledMap>
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

const StyledMap = styled.div`
  width: 100%;
  height: 80%;
  border-radius: 16px;
  border: 1px solid #c0d6df;
  background-color: #c0d6df;
`;
