import styled from "styled-components";

const Header = () => {
  return <StyledHeader>
    <StyledImage src="/assets/good_news_logo.png" alt="logo_image" />
  </StyledHeader>;
};

export default Header;

/** Header 임시 */
const StyledHeader = styled.div`
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 10;
  padding: 0 20px;
`;

const StyledImage = styled.img`
  height: 70%;
`