// 가족 관련 설명

import styled from "styled-components";
import Text from "../@common/Text";

const SubIntro2 = () => {
  return (
    <StyledSubIntro2Wrapper>
      <StyledSubIntro2ContentWrapper>
        {/* 주요 설명 (큰 사이즈) */}
        <div>
          <div className="flex">
            <Text size="text1">재난 시</Text>
            <Text size="text1" isBold={true} className="ml-2">
              가족
            </Text>
            <Text size="text1">의 상태가</Text>
          </div>
          <Text size="text1">궁금하지 않으신가요?</Text>
        </div>

        {/* 설명 (작은 사이즈) */}
        <div className="flex justify-between w-full">
          <Text size="text4">재난 시, 가족의 상태와 위치를 확인하고</Text>
          <Text size="text4">가족과 만날 장소도 정할 수 있어요.</Text>
        </div>
      </StyledSubIntro2ContentWrapper>
    </StyledSubIntro2Wrapper>
  );
};

export default SubIntro2;

const StyledSubIntro2Wrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
`;

const StyledSubIntro2ContentWrapper = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;
