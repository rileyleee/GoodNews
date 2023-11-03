import styled from "styled-components";
import Footer from "../@common/Footer";

// 메인페이지의 다운로드 부분(qr, 링크)
const DownloadIntro = () => {
  return (
    <>
      <StyledDownloadIntro>다운로드</StyledDownloadIntro>
      <Footer />
    </>
  );
};

export default DownloadIntro;

const StyledDownloadIntro = styled.div`
  height: 70%;
  background-color: lavender;
`;
