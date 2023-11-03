// 메인 페이지
import {SectionsContainer, Section, Header, Footer} from 'react-fullpage';

const HomePage = () => {

  let options = {
    anchors: ['sectionOne', 'sectionTwo', 'sectionThree'],
  };

  return (
    <SectionsContainer {...options}>
    <Section>Page 1</Section>
    <Section>Page 2</Section>
    <Section>Page 3</Section>
  </SectionsContainer>
  );
};

export default HomePage;
