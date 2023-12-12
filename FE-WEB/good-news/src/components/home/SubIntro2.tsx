// 가족 관련 설명

import styled from "styled-components";
import Text from "../@common/Text";

const SubIntro2 = () => {
  return (
    <StyledSubIntro2Wrapper>
      <StyledSubIntro2ContentWrapper className="flex flex-col items-center justify-center">
        <div className="w-full">
          {/* 주요 설명 (큰 사이즈) */}

          {/* 설명 (작은 사이즈) */}
          <div className="flex justify-between w-full">
            <div>
              <div className="flex self-start">
                <Text size="text1">재난 시</Text>
                <Text size="text1" isBold={true} className="ml-2">
                  가족
                </Text>
                <Text size="text1">의 상태가</Text>
              </div>
              <Text size="text1">궁금하지 않으신가요?</Text>
              <Text size="text4" className="mt-40">
                재난 시, 가족의 상태와 위치를 확인하고
              </Text>
            </div>
            <StyledImage src="/assets/subintro2_1.png" alt="임시 사진" />
            <Text size="text4" className="self-center">
              가족과 만날 장소도 정할 수 있어요.
            </Text>
          </div>
        </div>

      </StyledSubIntro2ContentWrapper>
    </StyledSubIntro2Wrapper>
  );
};

export default SubIntro2;

const StyledSubIntro2Wrapper = styled.div`
  width: 100%;
  height: 100%;
`;

const StyledSubIntro2ContentWrapper = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledImage = styled.img`
  height: 600px;
`;

