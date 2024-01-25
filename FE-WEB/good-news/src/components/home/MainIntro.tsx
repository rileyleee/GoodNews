// 메인페이지의 제일 처음 부분
import styled, { keyframes } from "styled-components";
import { LuMouse } from "react-icons/lu";

import Text from "../@common/Text";
import Button from "../@common/Button";

// 1. 깜빡거리는 애니메이션 정의
const blink = keyframes`
0%, 100% {
  opacity: 1;
}
50% {
  opacity: 0;
}
`;

const MainIntro = () => {
  const handleMoveDownloadClick = () => {
    window.location.href = "https://saveurlife.kr/images/goodnews.apk";
  };

  // const handleMoveMapIntroClick = () => {
  //   console.log("앱 이용 현황으로 이동합니다.");
  //   window.location.hash = "#mapIntro";
  // };

  return (
    <StyledMainIntroWrapper>
      {/* 여기에서 이미지 넣어주기 */}
      <StyledVideo muted autoPlay loop>
        <source src="/assets/mainBackground.mp4" type="video/mp4" />
      </StyledVideo>
      <StyledMainContentWrapper>
        {/* 텍스트 wrap md 사이즈부터 보이게 */}
        <div className="ml-24 hidden md:block">
          <Text size="text1">위급 상황의 손길과 소통</Text>
          <Text size="text1" isBold={true}>
            희소식과 함께, 안전하게!
          </Text>
        </div>
        {/* 텍스트 wrap sm 사이즈만 보이게 (모바일) */}
        <div className="text-center md:hidden">
          <Text size="text2">위급 상황의 손길과 소통</Text>
          <Text size="text2" isBold={true}>
            희소식과 함께, 안전하게!
          </Text>
        </div>

        {/* 버튼 wrap */}
        {/* <StyledMainButtonWrapper className="grid gap-4 grid-cols-2"> */}
        {/* <StyledMainButtonWrapper className="grid gap-4 grid-cols-1"> */}
        <div className="flex justify-center">
          <Button
            size="Large"
            color="Sub"
            isActive={true}
            className="drop-shadow-lg w-full md:w-1/2 mt-8"
            onClick={handleMoveDownloadClick}
          >
            희소식 다운로드
          </Button>
        </div>

        {/* <Button
            size="Large"
            color="Sub"
            isActive={true}
            className="drop-shadow-lg"
            onClick={handleMoveMapIntroClick}
          >
            앱 이용 현황
          </Button> */}
        {/* </StyledMainButtonWrapper> */}
      </StyledMainContentWrapper>
      <StyledLuMouse />
    </StyledMainIntroWrapper>
  );
};

export default MainIntro;

const StyledMainIntroWrapper = styled.div`
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
`;
const StyledMainContentWrapper = styled.div`
  width: 80%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`;

// 비디오 반응형 시 설정 변경
const StyledVideo = styled.video`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

// const StyledMainButtonWrapper = styled.div`
//   width: 50%;
//   margin: 0 auto;
//   margin-top: 52px;
// `;

const StyledLuMouse = styled(LuMouse)`
  color: white;
  width: 32px;
  height: 32px;

  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translate(-50%, 0);

  animation: ${blink} 2s infinite;
`;
