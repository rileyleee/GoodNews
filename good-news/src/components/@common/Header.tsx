import styled from "styled-components";

const Header = () => {
  return <StyledHeader>임시 헤더입니당</StyledHeader>;
};

export default Header;

/** Header 임시 */
const StyledHeader = styled.div`
  width: 100%;
  height: 60px;
  line-height: 60px;
  /* background-color: #ccc; */
  position: absolute;
  top: 0;
  left: 0;
  z-index: 10;
`;
