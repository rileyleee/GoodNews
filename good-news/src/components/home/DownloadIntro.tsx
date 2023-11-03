import styled from "styled-components";
import Footer from "../@common/Footer";
import Button from "../@common/Button";

// 메인페이지의 다운로드 부분(qr, 링크)
const DownloadIntro = () => {

  const handleDownloadClick = () => {
    console.log("안드로이드 앱 설치 페이지로 넘어가야함 !")
  }

  return (
    <>
      <StyledDownloadPageWrapper>
        <StyledDownloadIntro>
          <StyledText>언제 발생할지 모르는 재난</StyledText>
          <StyledText>여러분들도 미리 대비하시는게 어떨까요?</StyledText>
          <StyledText2>이제 희소식과 함께 시작하세요.</StyledText2>
          <StyledText2>현재까지 2,368,493건의 다운로드</StyledText2>
          <StyledText>QR 코드 위치</StyledText>
          <Button
            size="Large"
            color="Undefined"
            onClick={handleDownloadClick}
            isActive={true}
          >Google Play에서 다운로드</Button>
        </StyledDownloadIntro>
      <Footer />
      </StyledDownloadPageWrapper>
    </>
  );
  
};

export default DownloadIntro;

const StyledDownloadPageWrapper = styled.div`
  width: 100%;
  height: 100%;
  background-color: #E7ECEF;
`

const StyledDownloadIntro = styled.div`
  width: 80%;
  height: 70%;
  // background-color: lavender;
  margin: 0 auto;
`;

const StyledText = styled.p`
  font-size: 35px;
  color: #000000;
  text-align: center;
`;

const StyledText2 = styled.p`
  font-size: 45px;
  color: #000000;
  text-align: center;
`;