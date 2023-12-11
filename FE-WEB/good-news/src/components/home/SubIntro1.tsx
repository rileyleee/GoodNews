// 메인페이지의 두 번째 부분 부터 (sub)

import styled from "styled-components";
import Text from "../@common/Text";

// mesh network 설명
const SubIntro1 = () => {
  return (
    <StyledSubIntro1Wrapper>
      <StyledSubIntro1ContentWrapper className="flex justify-between items-center">
        <div>
          {/* 주요 설명 (큰 사이즈) */}
          <div>
            <div className="flex">
              <Text size="text1" isBold={true}>
                통신이 불가능
              </Text>
              <Text size="text1">할 때,</Text>
            </div>
            <div className="flex">
              <Text size="text1" isBold={true}>
                Mesh Network
              </Text>
              <Text size="text1">로 연결된</Text>
            </div>
            <Text size="text1">사람들의 정보를 볼 수 있어요!</Text>
          </div>
          {/* 설명 (작은 사이즈) */}
          <div className="mt-8">
            <Text size="text4" color="Gray">
              100m 거리의 주변 사람들에게도, 멀리 있는 내 가족들에게도
            </Text>
            <Text size="text4" color="Gray">
              나의 상태를 알리고 연락하고 싶지 않으신가요?
            </Text>
            <div className="flex mt-8">
              <Text size="text4" color="Gray" isBold={true}>
                거리, 통신 환경 모두 상관없이
              </Text>
              <Text size="text4" color="Gray" className="ml-2">
                대화하고 도움을 요청하세요!
              </Text>
            </div>
          </div>
        </div>
        <StyledImage src="/assets/subIntro1_img.png" alt="임시 사진" />
      </StyledSubIntro1ContentWrapper>
    </StyledSubIntro1Wrapper>
  );
};

export default SubIntro1;

const StyledSubIntro1Wrapper = styled.div`
  width: 100%;
  height: 100%;
`;

const StyledSubIntro1ContentWrapper = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledImage = styled.img`
  height: 80%;
`;
