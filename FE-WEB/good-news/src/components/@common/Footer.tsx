import styled from "styled-components";
import Text from "./Text";


const Footer = () => {
  return (
    <StyledFooter>
        <Text className="ms-12 pt-6" size="text6" color="Gray">(주) 파랑새</Text>
        <Text className="ms-12 pt-6" size="text6" color="Gray">34153 대전광역시 유성구 덕명동 124 교육동 20X B307</Text>
        <Text className="ms-12" size="text6" color="Gray">홈페이지 운영/장애 문의 : 042-307-B307</Text>
        {/* <Text className="me-12 text-right underline hover:text-gray-500" size="text6" color="Gray">관리자 로그인</Text> */}
    </StyledFooter>
  )
};

export default Footer;

const StyledFooter = styled.div`
  height: 30%;
  background-color: #C0D6DF;
`;
