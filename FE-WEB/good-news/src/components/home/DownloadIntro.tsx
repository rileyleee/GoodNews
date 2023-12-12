import styled from "styled-components";
import Footer from "../@common/Footer";
import Button from "../@common/Button";
import Text from "../@common/Text";

// 메인페이지의 다운로드 부분(qr, 링크)
const DownloadIntro = () => {

  const handleDownloadClick = () => {
    window.location.href = "https://saveurlife.kr/images/goodnews.apk";
  }

  return (
    <>
      <StyledDownloadPageWrapper>
        <StyledDownloadIntro>
          <Text className="grid place-items-center pt-20" size="text4">언제 발생할지 모를 재난</Text>
          <Text className="grid place-items-center" size="text4">여러분도 미리 대비하시는게 어떨까요?</Text>
          <div className="flex justify-center align-center pt-10">
            <Text size="text2">이제 <strong>희소식</strong>과 함께 시작하세요.</Text>
          </div>
          {/* <Text className="grid place-items-center pb-6" size="text4">현재까지 2,368,493건의 다운로드</Text> */}
          <div className="grid place-items-center">
            <StyledQrCode src="/assets/goodnews_qrcode2.png" />
            {/* <Button
              className="px-8 drop-shadow-lg flex flex-row items-center"
              size="Large"
              color="Undefined"
              onClick={handleDownloadClick}
              isActive={true}
              ><StyledPlayStore src="/assets/googlePlay.png" alt="Download Icon"/>Google Play에서 다운로드</Button> */}
            <Button
              className="px-8 drop-shadow-lg w-80"
              size="Large"
              color="Undefined"
              onClick={handleDownloadClick}
              isActive={true}
              >희소식 다운로드</Button>
          </div>
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

const StyledQrCode = styled.img`
  width: 120px;
  height: 120px;
  margin-bottom: 20px;
  margin-top: 20px;
`

// const StyledPlayStore = styled.img`
//   width: 30px;
//   height: 30px;
//   margin-right: 0.5rem;
// `