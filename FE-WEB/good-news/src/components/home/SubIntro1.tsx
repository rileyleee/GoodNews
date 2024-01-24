// 메인페이지의 두 번째 부분 부터 (sub)

import styled from "styled-components";
import Text from "../@common/Text";

// mesh network 설명
const SubIntro1 = () => {
  return (
    <StyledSubIntro1Wrapper>
      <StyledSubIntro1ContentWrapper className="flex justify-center lg:justify-between items-center relative">
        <div className="absolute lg:relative bg-zinc-300/70 md:bg-inherit">
          {/* sm 사이즈일 때 */}
          {/* 주요 설명 (큰 사이즈) */}
          <div className="text-center md:hidden">
            <Text size="text4">
              <strong>통신이 불가능</strong> 할 때,
            </Text>
            <Text size="text4">
              <strong>Mesh Network</strong> 로 연결된
            </Text>
            <Text size="text4">사람들의 정보를 볼 수 있어요!</Text>
          </div>
          {/* 설명 (sm) */}
          <div className="mt-8 text-center md:hidden">
            <Text size="text6" color="Gray">
              100m 거리의 주변 사람들에게도, <br />
              멀리 있는 내 가족들에게도
            </Text>
            <Text size="text6" color="Gray">
              나의 상태를 알리고 연락하고 싶지 않으신가요?
            </Text>
            <div className="mt-8">
              <Text size="text6" color="Gray">
                <strong>거리, 통신 환경 모두 상관없이</strong> <br />
                대화하고 도움을 요청하세요!
              </Text>
            </div>
          </div>

          {/* 설명 (md 사이즈) */}
          <div className="text-center hidden md:block lg:hidden">
            <Text size="text3">
              <strong>통신이 불가능</strong> 할 때,
            </Text>
            <Text size="text3">
              <strong>Mesh Network</strong> 로 연결된
            </Text>
            <Text size="text3">사람들의 정보를 볼 수 있어요!</Text>
          </div>
          <div className="mt-8 text-center hidden md:block lg:hidden">
            <Text size="text5" color="Gray">
              100m 거리의 주변 사람들에게도, 멀리 있는 내 가족들에게도
            </Text>
            <Text size="text5" color="Gray">
              나의 상태를 알리고 연락하고 싶지 않으신가요?
            </Text>
            <div className="mt-8">
              <Text size="text5" color="Gray">
                <strong>거리, 통신 환경 모두 상관없이</strong> 대화하고 도움을
                요청하세요!
              </Text>
            </div>
          </div>

          {/* x-lg 사이즈일 때부터 */}
          {/* 주요 설명 (큰 사이즈) */}
          <div className="hidden xl:block">
            <Text size="text1">
              <strong>통신이 불가능</strong> 할 때,
            </Text>
            <Text size="text1">
              <strong>Mesh Network</strong> 로 연결된
            </Text>
            <Text size="text1">사람들의 정보를 볼 수 있어요!</Text>
          </div>
          {/* lg 사이즈일 때부터 */}
          {/* 주요 설명 (큰 사이즈) */}
          <div className="hidden lg:block xl:hidden">
            <Text size="text3">
              <strong>통신이 불가능</strong> 할 때,
            </Text>
            <Text size="text3">
              <strong>Mesh Network</strong> 로 연결된
            </Text>
            <Text size="text3">사람들의 정보를 볼 수 있어요!</Text>
          </div>
          {/* 설명 (작은 사이즈) */}
          <div className="mt-8 hidden lg:block">
            <Text size="text4" color="Gray">
              100m 거리의 주변 사람들에게도, 멀리 있는 내 가족들에게도
            </Text>
            <Text size="text4" color="Gray">
              나의 상태를 알리고 연락하고 싶지 않으신가요?
            </Text>
            <div className="mt-8">
              <Text size="text4" color="Gray">
                <strong>거리, 통신 환경 모두 상관없이</strong> 대화하고 도움을
                요청하세요!
              </Text>
            </div>
          </div>
        </div>
        <StyledImage
          src="/assets/subIntro1_img.png"
          alt="임시 사진"
          className="h-4/6 lg:h-5/6"
        />
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

// const StyledImage = styled.img`
//   height: 80%;
//   object-fit: cover;
// `;
const StyledImage = styled.img``;
