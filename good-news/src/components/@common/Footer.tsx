import styled from "styled-components";

const Footer = () => {
  return (
    <StyledFooter>
        <div>(주) 희소식</div>
        <div>34153 대전광역시 유성구 덕명동 124 교육동 20X B307</div>
        <div>홈페이지 운영/장애 문의 : 042-307-B307</div>
        <div>관리자 로그인</div>
    </StyledFooter>
  )
};

export default Footer;

const StyledFooter = styled.div`
  height: 30%;
  background-color: #C5C5C5;
`;
