// 메인 페이지
import { SectionsContainer, Section } from "react-fullpage";

import MainIntro from "../components/home/MainIntro";
import SubIntro1 from "../components/home/SubIntro1";
import SubIntro2 from "../components/home/SubIntro2";
import SubIntro3 from "../components/home/SubIntro3";
import SubIntro4 from "../components/home/SubIntro4";
import MapIntro from "../components/home/MapIntro";
import DownloadIntro from "../components/home/DownloadIntro";
import styled from "styled-components";

const HomePage: React.FC = () => {
  let options: any = {
    //anchors : 각 페이지 섹션에 고유한 식별자를 제공
    //이 식별자들은 URL의 해시(#) 부분에 해당되어, 사용자가 URL을 통해 직접 특정 섹션으로 이동할 수 있게 해주는 역할
    anchors: [
      "mainIntro",
      "subIntro1",
      "subIntro2",
      "subIntro3",
      "subIntro4",
      "mapIntro",
      "downloadIntro",
    ],
    arrowNavigation: true,    // 위아래 화살표로 이동 가능
    scrollBar: false,
  };

  return (
    <>
      <div className="hidden lg:block">
        <SectionsContainer {...options}>
          <Section><MainIntro /></Section>
          <Section><SubIntro1 /></Section>
          <Section><SubIntro2 /></Section>
          <Section><SubIntro3 /></Section>
          <Section><SubIntro4 /></Section>
          <Section><MapIntro /></Section>
          <Section><DownloadIntro /></Section>
        </SectionsContainer>
      </div>
      <StyledSectionContainer className="lg:hidden styledSectionContainer">
        <StyledSection><MainIntro /></StyledSection>
        <StyledSection><SubIntro1 /></StyledSection>
        <StyledSection><SubIntro2 /></StyledSection>
        <StyledSection><SubIntro3 /></StyledSection>
        <StyledSection><SubIntro4 /></StyledSection>
        {/* <StyledSection><MapIntro /></StyledSection> */}
        <StyledSection><DownloadIntro /></StyledSection>
      </StyledSectionContainer>
    </>
  );
};

export default HomePage;

const StyledSectionContainer = styled.div`
  height: 100vh;  
  overflow-y: scroll;
`
const StyledSection = styled.div`
  height: 100vh;
`