// 가족 관련 설명

import styled from "styled-components";
import Text from "../@common/Text";

const SubIntro2 = () => {
  return (
    <StyledSubIntro2Wrapper className="relative">
      <StyledSubIntro2ContentWrapper className="flex items-center justify-center lg:justify-around relative">
        <div className="absolute md:relative bg-zinc-300/70 md:bg-inherit">
          {/* sm 사이즈일 때 */}
          <div className="text-center md:hidden">
            <Text size="text4">
              재난 시 <strong>가족의 상태</strong>가
            </Text>
            <Text size="text4" className="mb-20">
              궁금하지 않으신가요?
            </Text>
          </div>
          <div className="text-center md:hidden">
            <Text size="text6" className="mb-2">
              재난 시, 가족의 상태와 위치를 확인하고
            </Text>
            <Text size="text6">가족과 만날 장소도 정할 수 있어요.</Text>
          </div>

          {/* md 사이즈일 때 */}
          <div className="hidden md:block lg:hidden">
            <Text size="text4">
              재난 시 <strong>가족의 상태</strong>가
            </Text>
            <Text size="text4" className="mb-8">
              궁금하지 않으신가요?
            </Text>
          </div>
          <div className="hidden md:block lg:hidden">
            <Text size="text6" className="mb-4">
              재난 시, 가족의 상태와 위치를 확인하고
            </Text>
            <Text size="text6">가족과 만날 장소도 정할 수 있어요.</Text>
          </div>

          {/* lg 사이즈일 때 */}
          <div className="hidden lg:block">
            <Text size="text2">
              재난 시 <strong>가족의 상태</strong>가
            </Text>
            <Text size="text2" className="mb-12">
              궁금하지 않으신가요?
            </Text>
          </div>
          <div className="hidden lg:block">
            <Text size="text4" className="mb-2">
              재난 시, 가족의 상태와 위치를 확인하고
            </Text>
            <Text size="text4">가족과 만날 장소도 정할 수 있어요.</Text>
          </div>
        </div>

        <StyledImage
          src="/assets/subintro2_1.png"
          alt="가족 장소 및 추가 페이지"
          className="h-4/6 lg:h-5/6"
        />
      </StyledSubIntro2ContentWrapper>
      <StyledBackgroundImage1
        src="/assets/subintro2_2.png"
        className="w-4/5 md:w-2/5"
      />
      <StyledBackgroundImage2
        src="/assets/subintro2_3.png"
        className="w-4/5 md:w-2/5"
      />
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
const StyledBackgroundImage1 = styled.img`
  position: absolute;
  top: 40px;
  left: 20px;
  z-index: -1;
`;
const StyledBackgroundImage2 = styled.img`
  position: absolute;
  bottom: 40px;
  right: 20px;
  z-index: -1;
`;

const StyledImage = styled.img`
  height: 600px;
`;
