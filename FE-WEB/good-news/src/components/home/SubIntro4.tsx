import styled from "styled-components";
import Text from "../@common/Text";

//손전등 관련 설명
const SubIntro4 = () => {
  return (
    <StyledFlashLightPageWrapper>
      <StyledFlashLightIntro className="flex justify-center lg:justify-between items-center relative">
        <StyledTextWrapper className="absolute md:relative">
          <Text size="text3" className="md:hidden text-center">
            나만의 <strong>긴급 손전등</strong>
          </Text>
          <Text size="text2" className="hidden md:block lg:hidden">
            나만의 <strong>긴급 손전등</strong>
          </Text>
          <Text size="text1" className="hidden lg:block">
            나만의 <strong>긴급 손전등</strong>
          </Text>

          {/* 모스부호 이미지 */}
          <div className="w-4/5 mx-auto md:w-full grid">
            <StyledMorseImage
              src="/assets/morse_S1.png"
              alt="임시 사진"
              className="justify-self-start"
            />
            <StyledMorseImage
              src="/assets/morse_O.png"
              alt="임시 사진"
              className="justify-self-center my-4"
            />
            <StyledMorseImage
              src="/assets/morse_S2.png"
              alt="임시 사진"
              className="justify-self-end"
            />
          </div>

          <div className="md:hidden text-center bg-zinc-300/70 md:bg-inherit">
            <Text size="text6">근거리에 위치한</Text>
            <Text size="text6">상대방과 소통하기 어려울 때</Text>
            <Text size="text6">
              <strong>모스부호</strong>를 만들어 소통할 수 있어요
            </Text>
          </div>
          <div className="hidden md:block lg:hidden">
            <Text size="text5">근거리에 위치한</Text>
            <Text size="text5">상대방과 소통하기 어려울 때</Text>
            <Text size="text5">
              <strong>모스부호</strong>를 만들어 소통할 수 있어요
            </Text>
          </div>
          <div className="hidden lg:block">
            <Text size="text4">근거리에 위치한</Text>
            <Text size="text4">상대방과 소통하기 어려울 때</Text>
            <Text size="text4">
              <strong>모스부호</strong>를 만들어 소통할 수 있어요
            </Text>
          </div>
        </StyledTextWrapper>
        <StyledFlashLightImage
          className="h-4/6 lg:h-5/6"
          src="/assets/mainFlashLight.png"
        />
      </StyledFlashLightIntro>
    </StyledFlashLightPageWrapper>
  );
};

export default SubIntro4;

const StyledFlashLightPageWrapper = styled.div`
  width: 100%;
  height: 100%;
`;

const StyledFlashLightIntro = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledFlashLightImage = styled.img``;

const StyledTextWrapper = styled.div`
  height: 80%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

const StyledMorseImage = styled.img`
  width: 65%;
`;
