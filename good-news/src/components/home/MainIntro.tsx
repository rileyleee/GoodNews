import styled from "styled-components";
import Text from "../@common/Text";
import Button from "../@common/Button";

// 메인페이지의 제일 처음 부분
const MainIntro = () => {
  const handleMoveGoogleClick = () => {
    // 구글 스토어로 이동하기!!!
    console.log("구글 스토어로 이동합니다.");
  };

  const handleMoveMapIntroClick = () => {
    console.log("앱 이용 현황으로 이동합니다.");
    window.location.hash = "#mapIntro";
  };

  return (
    <StyledMainIntroWrapper>
      {/* 여기에서 이미지 넣어주기 */}
      <video muted autoPlay loop>
        <source src="/assets/mainBackground.mp4" type="video/mp4" />
      </video>
      <StyledMainContentWrapper>
        {/* 텍스트 wrap */}
        <div className="ml-24">
          <Text size="text1">위급 상황의 손길과 소통</Text>
          <Text size="text1" isBold={true}>
            희소식과 함께, 안전하게!
          </Text>
        </div>

        {/* 버튼 wrap */}
        <StyledMainButtonWrapper className="grid gap-4 grid-cols-2">
          <Button
            size="Large"
            color="Sub"
            isActive={true}
            className="drop-shadow-lg"
            onClick={handleMoveGoogleClick}
          >
            Google Play
          </Button>
          <Button
            size="Large"
            color="Sub"
            isActive={true}
            className="drop-shadow-lg"
            onClick={handleMoveMapIntroClick}
          >
            앱 이용 현황
          </Button>
        </StyledMainButtonWrapper>
      </StyledMainContentWrapper>
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

const StyledMainButtonWrapper = styled.div`
  width: 50%;
  margin: 0 auto;
  margin-top: 52px;
`;
